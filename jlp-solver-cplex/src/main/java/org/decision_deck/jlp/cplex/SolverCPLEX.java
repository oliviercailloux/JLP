package org.decision_deck.jlp.cplex;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.decision_deck.jlp.AbstractLpSolver;
import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpDirection;
import org.decision_deck.jlp.LpFileFormat;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpSolverException;
import org.decision_deck.jlp.LpTerm;
import org.decision_deck.jlp.parameters.LpDoubleParameter;
import org.decision_deck.jlp.parameters.LpIntParameter;
import org.decision_deck.jlp.parameters.LpStringParameter;
import org.decision_deck.jlp.parameters.LpTimingType;
import org.decision_deck.jlp.problem.LpVariableType;
import org.decision_deck.jlp.result.LpResultStatus;
import org.decision_deck.jlp.result.LpSolutionImpl;
import org.decision_deck.jlp.utils.LpSolverUtils;
import org.decision_deck.jlp.utils.TimingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.io.CharSink;
import com.google.common.io.Files;

import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloLinearNumExprIterator;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.CplexStatus;
import ilog.cplex.IloCplex.DoubleParam;
import ilog.cplex.IloCplex.IntParam;
import ilog.cplex.IloCplex.ParallelMode;
import ilog.cplex.IloCplex.StringParam;

/**
 * The
 * <a href="http://publib.boulder.ibm.com/infocenter/cosinfoc/v12r3/index.jsp">
 * IBM ILOG CPLEX</a> solver.
 *
 * @param <T>
 *            the class used for the variables.
 *
 * @author Olivier Cailloux
 *
 *
 */
public class SolverCPLEX<T> extends AbstractLpSolver<T> {
	private static final int CPLEX_CLOCK_TYPE_CPU = 1;

	private static final int CPLEX_CLOCK_TYPE_WALL = 2;

	private static final Logger s_logger = LoggerFactory.getLogger(SolverCPLEX.class);

	private IloCplex m_cplex;

	private LpFileFormat m_currentFormat;

	private BiMap<T, IloNumVar> m_variablesToCplex;

	/**
	 * Creates a new solver instance.
	 */
	public SolverCPLEX() {
		m_cplex = null;
		m_variablesToCplex = null;
		m_currentFormat = null;
	}

	@Override
	public void close() throws LpSolverException {
		if (m_cplex != null) {
			m_cplex.end();
			m_cplex = null;
		}
	}

	@Override
	public LpFileFormat getPreferredFormat() throws LpSolverException {
		return null;
	}

	/**
	 * Retrieves the result status equivalent to the given cplex status.
	 *
	 * @param cplexStatus
	 *            not <code>null</code>.
	 * @param hasSolution
	 *            <code>true</code> iff the given status comes with a solution.
	 * @return not <code>null</code>.
	 */
	public LpResultStatus getResultStatus(CplexStatus cplexStatus, boolean hasSolution) {
		Preconditions.checkNotNull(cplexStatus);
		final LpResultStatus resultStatus;
		if (cplexStatus == CplexStatus.InfOrUnbd) {
			resultStatus = LpResultStatus.INFEASIBLE_OR_UNBOUNDED;
		} else if (cplexStatus == CplexStatus.AbortTimeLim) {
			if (hasSolution) {
				resultStatus = LpResultStatus.TIME_LIMIT_REACHED_WITH_SOLUTION;
			} else {
				resultStatus = LpResultStatus.TIME_LIMIT_REACHED_NO_SOLUTION;
			}
		} else if (cplexStatus == CplexStatus.Infeasible) {
			resultStatus = LpResultStatus.INFEASIBLE;
		} else if (cplexStatus == CplexStatus.Optimal) {
			/**
			 * NB cplex possibly returns optimal (and an objective value of
			 * zero) when no objective function is defined!
			 */
			if (getProblem().getObjective().isComplete()) {
				resultStatus = LpResultStatus.OPTIMAL;
			} else {
				resultStatus = LpResultStatus.FEASIBLE;
			}
		} else if (cplexStatus == CplexStatus.OptimalTol) {
			if (getProblem().getObjective().isComplete()) {
				resultStatus = LpResultStatus.OPTIMAL;
			} else {
				resultStatus = LpResultStatus.FEASIBLE;
			}
		} else if (cplexStatus == CplexStatus.MemLimInfeas) {
			resultStatus = LpResultStatus.MEMORY_LIMIT_REACHED_NO_SOLUTION;
		} else if (cplexStatus == CplexStatus.MemLimFeas) {
			resultStatus = LpResultStatus.MEMORY_LIMIT_REACHED_WITH_SOLUTION;
		} else {
			if (hasSolution) {
				resultStatus = LpResultStatus.ERROR_WITH_SOLUTION;
			} else {
				resultStatus = LpResultStatus.ERROR_NO_SOLUTION;
			}
		}
		return resultStatus;
	}

	@Override
	public Object getUnderlyingSolver() throws LpSolverException {
		m_currentFormat = null;
		lazyInit();
		return m_cplex;
	}

	/**
	 * A method useful for debug which logs everly information that can be found
	 * in the given solver instance.
	 *
	 * @param cplex
	 *            not <code>null</code>.
	 */
	public void logContent(IloCplex cplex) {
		checkNotNull(cplex);
		try {
			for (Iterator<?> iterator = cplex.iterator(); iterator.hasNext();) {
				final Object obj = iterator.next();
				if (obj instanceof IloLPMatrix) {
					IloLPMatrix mat = (IloLPMatrix) obj;
					final IloNumVar[] numVars = mat.getNumVars();
					for (final IloNumVar var : numVars) {
						final double value;
						try {
							value = cplex.getValue(var);
							s_logger.debug("Var {}, value " + value + ".", var);
						} catch (IloException exc) {
							s_logger.debug("Var {}, value unknown.", var);
						}
					}
					final IloRange[] ranges = mat.getRanges();
					for (final IloRange range : ranges) {
						final double value;
						try {
							value = cplex.getValue(range.getExpr());
							s_logger.debug("Range {}, value " + value + ".", range);
						} catch (IloException exc) {
							s_logger.debug("Range {}, value unknown.", range);
						}
					}
				} else if (obj instanceof IloRange) {
					final IloRange range = (IloRange) obj;
					final double value;
					try {
						value = cplex.getValue(range.getExpr());
						s_logger.debug("Range {}, value " + value + ".", range);
					} catch (IloException exc) {
						s_logger.debug("Range {}, value unknown.", range);
					}
					IloLinearNumExprIterator it2 = ((IloLinearNumExpr) range.getExpr()).linearIterator();
					while (it2.hasNext()) {
						final IloNumVar numVar = it2.nextNumVar();
						final double varValue;
						try {
							varValue = cplex.getValue(numVar);
							s_logger.debug("Var {}, value " + varValue + ".", numVar);
						} catch (IloException exc) {
							s_logger.debug("Var {}, value unknown.", numVar);
						}
						// final String name = numVar.getName();
					}
				} else if (obj instanceof IloNumExpr) {
					IloNumExpr expr = (IloNumExpr) obj;
					// final double value = cplex.getValue(expr);
					// s_logger.info("Expr {}, value " + value + ".", expr);
					s_logger.debug("Expr {}.", expr);
				}
			}
		} catch (Exception exc) {
			s_logger.warn("Exception while trying to log content.", exc);
		}
	}

	/**
	 * Sets the parameter and transform a possible {@link IloException} into an
	 * {@link LpSolverException}.
	 *
	 * @param cplex
	 *            not <code>null</code>.
	 * @param param
	 *            not <code>null</code>.
	 * @param value
	 *            the value.
	 * @throws LpSolverException
	 *             if an {@link IloException} occurs while setting the
	 *             parameter.
	 */
	public void setParam(IloCplex cplex, final DoubleParam param, final double value) throws LpSolverException {
		Preconditions.checkNotNull(cplex);
		Preconditions.checkNotNull(param);
		try {
			cplex.setParam(param, value);
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		}
	}

	/**
	 * Sets the parameter and transform a possible {@link IloException} into an
	 * {@link LpSolverException}.
	 *
	 * @param cplex
	 *            not <code>null</code>.
	 * @param param
	 *            not <code>null</code>.
	 * @param value
	 *            the value.
	 * @throws LpSolverException
	 *             if an {@link IloException} occurs while setting the
	 *             parameter.
	 */
	public void setParam(IloCplex cplex, final IntParam param, final int value) throws LpSolverException {
		Preconditions.checkNotNull(cplex);
		Preconditions.checkNotNull(param);
		try {
			cplex.setParam(param, value);
		} catch (IloException exc) {
			throw new LpSolverException("Setting parameter " + param + " to value " + value + ".", exc);
		}
	}

	/**
	 * Sets the parameter and transform a possible {@link IloException} into an
	 * {@link LpSolverException}.
	 *
	 * @param cplex
	 *            not <code>null</code>.
	 * @param param
	 *            not <code>null</code>.
	 * @param value
	 *            the value.
	 * @throws LpSolverException
	 *             if an {@link IloException} occurs while setting the
	 *             parameter.
	 */
	public void setParam(IloCplex cplex, final StringParam param, final String value) throws LpSolverException {
		Preconditions.checkNotNull(cplex);
		Preconditions.checkNotNull(param);
		try {
			cplex.setParam(param, value);
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		}
	}

	/**
	 * Initializes the parameters, including logging parameters, of the given
	 * solver instance to appropriate values considering the parameters set in
	 * this object, or to default values.
	 *
	 * @param cplex
	 *            not <code>null</code>.
	 * @throws LpSolverException
	 *             if an exception occurs while setting the parameters, or if
	 *             some parameters have values that are impossible to satisfy
	 *             (e.g. if both cpu and wall timings are set).
	 */
	public void setParameters(IloCplex cplex) throws LpSolverException {
		setDefaultParameters(cplex);

		final LpTimingType timingType = getPreferredTimingType();

		final int clockType;
		switch (timingType) {
		case WALL_TIMING:
			clockType = CPLEX_CLOCK_TYPE_WALL;
			break;
		case CPU_TIMING:
			clockType = CPLEX_CLOCK_TYPE_CPU;
			break;
		default:
			throw new IllegalStateException("Unknown timing type.");
		}
		setParam(cplex, IntParam.ClockType, clockType);

		final Double timeLimit_s = getTimeLimit(timingType);
		if (timeLimit_s != null) {
			setParam(cplex, DoubleParam.TiLim, timeLimit_s.doubleValue());
		}

		final Integer maxThreads = getParameter(LpIntParameter.MAX_THREADS);
		final int maxThreadsValue;
		if (maxThreads == null) {
			maxThreadsValue = 0;
		} else {
			maxThreadsValue = maxThreads.intValue();
		}
		setParam(cplex, IntParam.Threads, maxThreadsValue);

		if (getParameter(LpDoubleParameter.MAX_TREE_SIZE_MB) != null) {
			setParam(cplex, DoubleParam.TreLim, getParameter(LpDoubleParameter.MAX_TREE_SIZE_MB).doubleValue());
		}
		if (getParameter(LpDoubleParameter.MAX_MEMORY_MB) != null) {
			setParam(cplex, DoubleParam.WorkMem, getParameter(LpDoubleParameter.MAX_MEMORY_MB).doubleValue());
		}

		if (getParameter(LpStringParameter.WORK_DIR) != null) {
			setParam(cplex, StringParam.WorkDir, getParameter(LpStringParameter.WORK_DIR));
		}

		final int mode;
		if (getParameter(LpIntParameter.DETERMINISTIC).intValue() == 0) {
			mode = ParallelMode.Opportunistic;
		} else {
			mode = ParallelMode.Deterministic;
		}
		setParam(cplex, IntParam.ParallelMode, mode);

		m_cplex.setOut(new CplexLogger(CplexLogger.OutLevel.INFO));
		m_cplex.setWarning(new CplexLogger(CplexLogger.OutLevel.WARNING));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Note that this goes through a temporary file because cplex does not allow
	 * exporting to a stream.
	 * </p>
	 */
	public void writeProblem(LpFileFormat format, CharSink destination) throws LpSolverException, IOException {
		/**
		 * Note that cplex 12.2 writes in unknown default export format (perhaps
		 * ISO 8859-1?) and this method assumes export is in UTF-8. Therefore,
		 * it might not work. However, oddly enough, tests show it seems to
		 * work.
		 */
		final LpFileFormat effFormat = format == null ? LpFileFormat.SOLVER_PREFERRED : format;
		final String ext;
		switch (effFormat) {
		case CPLEX_LP:
			ext = ".lp";
			break;
		case MPS:
			ext = ".mps";
			break;
		case CPLEX_SAV:
		case SOLVER_PREFERRED:
			ext = ".sav";
			break;
		default:
			throw new IllegalArgumentException();
		}

		final File tempFile = File.createTempFile("CPLEXexport", ext);
		try {
			tempFile.deleteOnExit();

			switch (effFormat) {
			case CPLEX_LP:
				exportModel(tempFile.getPath(), LpFileFormat.CPLEX_LP);
				break;
			case MPS:
				exportModel(tempFile.getPath(), LpFileFormat.MPS);
				break;
			case CPLEX_SAV:
			case SOLVER_PREFERRED:
				exportModel(tempFile.getPath(), LpFileFormat.CPLEX_SAV);
				break;
			default:
				throw new IllegalArgumentException();
			}

			Files.asCharSource(tempFile, Charsets.UTF_8).copyTo(destination);
		} finally {
			if (!tempFile.delete()) {
				throw new IOException("Could not delete temporary file " + tempFile + ".");
			}
		}
	}

	@Deprecated
	@Override
	public void writeProblem(LpFileFormat format, String file, boolean addExtension) throws LpSolverException {
		if (!addExtension) {
			throw new LpSolverException("Not supported without ext.");
		}
		switch (format) {
		case CPLEX_LP:
			exportModel(file + ".lp", LpFileFormat.CPLEX_LP);
			break;
		case MPS:
			exportModel(file + ".mps", LpFileFormat.MPS);
			break;
		case CPLEX_SAV:
		case SOLVER_PREFERRED:
			exportModel(file + ".sav", LpFileFormat.CPLEX_SAV);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void exportModel(String file, LpFileFormat format) throws LpSolverException {
		m_currentFormat = format;
		lazyInit();
		try {
			m_cplex.exportModel(file);
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		} catch (RuntimeException exc2) {
			logContent(m_cplex);
			throw exc2;
		} finally {
			close();
		}
	}

	private IloLinearNumExpr getAsCplex(LpLinear<T> linear) throws IloException {
		IloLinearNumExpr lin = m_cplex.linearNumExpr();
		for (LpTerm<T> term : linear) {
			lin.addTerm(term.getCoefficient(), m_variablesToCplex.get(term.getVariable()));
		}
		return lin;
	}

	private void lazyInit() throws LpSolverException {
		if (m_cplex != null) {
			return;
		}
		checkState(getProblem().getObjective().isEmpty() || getProblem().getObjective().isComplete(),
				"Objective function set without a direction (or inverse).");

		try {
			m_cplex = new IloCplex();

			setParameters(m_cplex);

			m_cplex.setName(getProblem().getName());
			s_logger.info("Building problem {}.", getProblem().getName());

			setVariables();

			for (LpConstraint<T> constraint : getProblem().getConstraints()) {
				LpLinear<T> linear = constraint.getLhs();
				final IloLinearNumExpr lin = getAsCplex(linear);

				final double rhs = constraint.getRhs();

				final String constraintName = m_currentFormat == null ? getConstraintName(constraint)
						: getConstraintName(constraint, m_currentFormat);

				switch (constraint.getOperator()) {
				case EQ:
					if (constraintName.isEmpty()) {
						m_cplex.addEq(lin, rhs);
					} else {
						m_cplex.addEq(lin, rhs, constraintName);
					}
					break;
				case GE:
					if (constraintName.isEmpty()) {
						m_cplex.addGe(lin, rhs);
					} else {
						m_cplex.addGe(lin, rhs, constraintName);
					}
					break;
				case LE:
					if (constraintName.isEmpty()) {
						m_cplex.addLe(lin, rhs);
					} else {
						m_cplex.addLe(lin, rhs, constraintName);
					}
					break;
				default:
					throw new IllegalStateException("Unknown operator.");
				}
				s_logger.debug("Set constraint {}.", constraint);
			}

			if (getProblem().getObjective().isComplete()) {
				final LpLinear<T> objective = getProblem().getObjective().getFunction();
				final IloLinearNumExpr lin = getAsCplex(objective);

				final LpDirection direction = getProblem().getObjective().getDirection();
				switch (direction) {
				case MAX:
					m_cplex.addMaximize(lin);
					break;
				case MIN:
					m_cplex.addMinimize(lin);
					break;
				default:
					throw new IllegalStateException("Unknown direction.");
				}
			}
		} catch (IloException exc) {
			close();
			throw new LpSolverException(exc);
		} catch (LpSolverException exc) {
			close();
			throw exc;
		}
	}

	private void setDefaultParameters(IloCplex cplex) throws LpSolverException {
		try {
			// 0 = No node file
			// 1 = Node file in memory and compressed; default
			// 2 = Node file on disk
			// 3 = Node file on disk and compressed
			final int writeNodesToDisk = 2;
			cplex.setParam(IntParam.NodeFileInd, writeNodesToDisk);
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		}

		/** Defined from cplex 12.3 onwards. */
		try {
			cplex.setParam(StringParam.FileEncoding, "UTF-8");
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		}
	}

	private void setVariables() throws IloException {
		final Builder<T, IloNumVar> variablesToCplexBuilder = ImmutableBiMap.builder();
		for (T variable : getProblem().getVariables()) {
			final String varName = m_currentFormat == null ? getVariableName(variable)
					: getVariableName(variable, m_currentFormat);
			LpVariableType varType = getProblem().getVariableType(variable);
			final Number lowerBound = LpSolverUtils.getVarLowerBoundBounded(getProblem(), variable);
			final Number upperBound = LpSolverUtils.getVarUpperBoundBounded(getProblem(), variable);

			final IloNumVar num;
			final double lb = lowerBound.doubleValue();
			final double ub = upperBound.doubleValue();
			final IloNumVarType type;
			switch (varType) {
			case BOOL:
				type = IloNumVarType.Bool;
				break;
			case INT:
				type = IloNumVarType.Int;
				break;
			case REAL:
				type = IloNumVarType.Float;
				break;
			default:
				throw new IllegalStateException("Unexpected type.");
			}
			if (varName.isEmpty()) {
				num = m_cplex.numVar(lb, ub, type);
			} else {
				num = m_cplex.numVar(lb, ub, type, varName);
			}
			m_cplex.add(num);
			s_logger.debug(
					"Set variable {} with bounds " + lb + ", " + ub + ", type " + type + ", name " + varName + ".",
					variable);

			variablesToCplexBuilder.put(variable, num);
		}
		m_variablesToCplex = variablesToCplexBuilder.build();
		// m_cplex.iterator();
		// m_cplex.LPMatrixIterator();
	}

	@Override
	protected LpResultStatus solveUnderlying() throws LpSolverException {
		m_currentFormat = null;
		lazyInit();

		try {
			final LpTimingType timingType = getPreferredTimingType();
			final TimingHelper timingHelper = new TimingHelper();
			timingHelper.setSolverStart_ms(timingType, m_cplex.getCplexTime() * 1000);
			timingHelper.start();
			final boolean solved = m_cplex.solve();
			timingHelper.stop();
			timingHelper.setSolverEnd_ms(timingType, m_cplex.getCplexTime() * 1000);

			CplexStatus cplexStatus = m_cplex.getCplexStatus();
			final LpResultStatus resultStatus = getResultStatus(cplexStatus, solved);
			if (resultStatus.foundFeasible()) {
				final LpSolutionImpl<T> solution = new LpSolutionImpl<T>(getProblem());

				for (IloNumVar num : m_variablesToCplex.inverse().keySet()) {
					final T variable = m_variablesToCplex.inverse().get(num);

					s_logger.debug("Querying value of {}.", num);
					final double value = m_cplex.getValue(num);
					s_logger.debug("Value is " + value + ".", num);

					solution.putValue(variable, Double.valueOf(value));
				}
				if (resultStatus == LpResultStatus.OPTIMAL) {
					solution.setObjectiveValue(Double.valueOf(m_cplex.getObjValue()));
				}
				setSolution(solution);
			}
			m_lastDuration = timingHelper.getDuration();

			return resultStatus;
		} catch (IloException e) {
			throw new LpSolverException(e);
		} finally {
			close();
		}
	}

	void setVariableNames(LpFileFormat format) {
		final Set<T> variables = m_variablesToCplex.keySet();
		for (T variable : variables) {
			final IloNumVar cplexVar = m_variablesToCplex.get(variable);
			final String name;
			if (format == null) {
				name = getVariableName(variable);
			} else {
				name = getVariableName(variable, format);
			}
			if (!name.isEmpty()) {
				cplexVar.setName(name);
			}
		}
	}
}
