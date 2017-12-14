package org.decision_deck.jlp.cplex;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloLinearNumExprIterator;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.CplexStatus;
import ilog.cplex.IloCplex.DoubleParam;
import ilog.cplex.IloCplex.IntParam;
import ilog.cplex.IloCplex.ParallelMode;
import ilog.cplex.IloCplex.StringParam;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.util.Iterator;

import org.decision_deck.jlp.LpDirection;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpObjective;
import org.decision_deck.jlp.LpSolverException;
import org.decision_deck.jlp.instanciation.LpSolverType;
import org.decision_deck.jlp.parameters.LpDoubleParameter;
import org.decision_deck.jlp.parameters.LpIntParameter;
import org.decision_deck.jlp.parameters.LpObjectParameter;
import org.decision_deck.jlp.parameters.LpParameters;
import org.decision_deck.jlp.parameters.LpParametersUtils;
import org.decision_deck.jlp.parameters.LpStringParameter;
import org.decision_deck.jlp.parameters.LpTimingType;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.problem.LpVariableType;
import org.decision_deck.jlp.result.LpResultStatus;
import org.decision_deck.jlp.solver.experimental.LpResultTransient;
import org.decision_deck.jlp.solver.experimental.LpSolverExt;
import org.decision_deck.jlp.utils.LpLinearUtils;
import org.decision_deck.jlp.utils.LpSolverUtils;
import org.decision_deck.jlp.utils.TimingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

public class SolverExtCPLEX<V> implements LpSolverExt<V> {

	private IloCplex m_cplex;

	private static final int CPLEX_CLOCK_TYPE_CPU = 1;

	private static final int CPLEX_CLOCK_TYPE_WALL = 2;

	public SolverExtCPLEX() {
		m_result = null;
		m_problem = new ProblemCplex<V>(this);
	}

	@Override
	public void init() throws LpSolverException {
		try {
			m_cplex = new IloCplex();
		} catch (IloException exc) {
			close();
			throw new LpSolverException(exc);
		}
		setDefaultParameters();
	}

	@Override
	public void setSaveResults(boolean saveResults) {
		m_saveResults = saveResults;
	}

	@Override
	public LpParameters getParameters() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public ProblemCplex<V> getMP() {
		return m_problem;
	}

	@Override
	public LpResultTransient<V> getResult() {
		TODO();
	}

	@Override
	public void close() {
		if (m_cplex != null) {
			m_cplex.end();
			m_cplex = null;
		}
	}

	@Override
	public IloCplex getUnderlying() {
		checkState(m_cplex != null);
		return m_cplex;
	}

	@Override
	public LpResultStatus solve() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public LpResultStatus solve(LpProblem<V> problem, LpParameters parameters) throws LpSolverException {
		try {
			m_cplex.clearModel();
		} catch (IloException exc) {
			throw new IllegalStateException(exc);
		}
		if (m_result != null) {
			m_result.setObsolete();
		}
		m_variablesToCplex.clear();

		s_logger.info("Building problem {}.", problem.getName());
		setParameters(parameters);

		m_cplex.setName(problem.getName());

		final Function<?, ?> variableNamer = (Function<?, ?>) parameters.getValue(LpObjectParameter.NAMER_VARIABLES);
		if (variableNamer != null) {
			throw new UnsupportedOperationException("Unsupported: namer in parameters.");
		}
		m_variableNamer = problem.getVariablesNamer();

		final Function<?, ?> constraintsNamer = (Function<?, ?>) parameters
				.getValue(LpObjectParameter.NAMER_CONSTRAINTS);
		if (constraintsNamer != null) {
			throw new UnsupportedOperationException("Unsupported: namer in parameters.");
		}
		m_constraintsNamer = problem.getConstraintsNamer();

		try {
			setVariables(problem);

			setConstraints(problem);

			setObjective(problem);
		} catch (IloException exc) {
			close();
			throw new LpSolverException(exc);
			// } catch (LpSolverException exc) {
			// close();
			// throw exc;
		}

		try {
			final LpTimingType timingType = LpParametersUtils.getPreferredTimingType(parameters);
			final TimingHelper timingHelper = new TimingHelper();
			timingHelper.setSolverStart_ms(timingType, m_cplex.getCplexTime() * 1000);
			timingHelper.start();
			final boolean solved = m_cplex.solve();
			timingHelper.stop();
			timingHelper.setSolverEnd_ms(timingType, m_cplex.getCplexTime() * 1000);

			CplexStatus cplexStatus = m_cplex.getCplexStatus();
			final LpResultStatus resultStatus = getResultStatus(cplexStatus, solved, problem.getObjective());
			m_result = new ResultCplex<V>(this, resultStatus, timingHelper.getDuration());
			return resultStatus;
		} catch (IloException e) {
			throw new LpSolverException(e);
		}
	}

	Double getSolutionObjectiveValue() throws IloException {
		checkState(m_result.getResultStatus() == LpResultStatus.OPTIMAL);
		return Double.valueOf(m_cplex.getObjValue());
	}

	/**
	 * Retrieves the current solution value for the given variable. The variable
	 * must be in the current problem.
	 * 
	 * @param variable
	 *            not <code>null</code>, a known variable.
	 * @return the solution value, not <code>null</code>.
	 * @throws IloException
	 */
	public Number getSolutionValue(V variable) throws IloException {
		checkNotNull(variable);
		final IloNumVar iloNumVar = m_variablesToCplex.get(variable);
		checkArgument(iloNumVar != null);
		s_logger.debug("Querying value of {}.", iloNumVar);
		final double value;
		try {
			value = m_cplex.getValue(iloNumVar);
		} catch (UnknownObjectException exc) {
			throw new IllegalStateException(exc);
		}
		s_logger.debug("Value is " + value + ".", iloNumVar);
		return Double.valueOf(value);
	}

	void setObjective(LpProblem<V> problem) throws IloException {
		LpObjective<V> objective = problem.getObjective();
		checkState(objective.isEmpty() || objective.isComplete(),
				"Objective function set without a direction (or inverse).");

		if (objective.isComplete()) {
			final LpLinear<V> objectiveLinear = objective.getFunction();
			final IloLinearNumExpr lin = getAsCplex(objectiveLinear);

			final LpDirection direction = objective.getDirection();
			switch (direction) {
			case MAX:
				m_cplex.addMaximize(lin);
				break;
			case MIN:
				m_cplex.addMinimize(lin);
				break;
			}
		}
	}

	void setVariables(LpProblem<V> problem) throws IloException {
		for (V variable : problem.getVariables()) {
			LpVariableType varType = problem.getVariableType(variable);
			final Number lowerBound = LpSolverUtils.getVarLowerBoundBounded(problem, variable);
			final Number upperBound = LpSolverUtils.getVarUpperBoundBounded(problem, variable);
			final double lb = lowerBound.doubleValue();
			final double ub = upperBound.doubleValue();
			setVariable(variable, varType, lb, ub);
		}
	}

	@Override
	public LpSolverType getSolverBrand() {
		return LpSolverType.CPLEX;
	}

	private void setDefaultParameters() throws LpSolverException {
		try {
			// 0 = No node file
			// 1 = Node file in memory and compressed; default
			// 2 = Node file on disk
			// 3 = Node file on disk and compressed
			final int writeNodesToDisk = 2;
			m_cplex.setParam(IntParam.NodeFileInd, writeNodesToDisk);
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		}

		try {
			/** Defined from cplex 12.3 onwards. */
			m_cplex.setParam(StringParam.FileEncoding, "UTF-8");
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		}

		m_cplex.setOut(new CplexLogger(CplexLogger.OutLevel.INFO));
		m_cplex.setWarning(new CplexLogger(CplexLogger.OutLevel.WARNING));
	}

	/**
	 * A method useful for debug which logs everly information that can be found in
	 * the given solver instance.
	 * 
	 * @param cplex
	 *            not <code>null</code>.
	 */
	public void logContent() {
		checkState(m_cplex != null, "Solver has not been initialized.");
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
			s_logger.error("Exception while trying to log content.", exc);
		}
	}

	void testCplexIterator() throws IloException {
		final Iterator<?> rangeIt = m_cplex.rangeIterator();
		while (rangeIt.hasNext()) {
			IloRange r = (IloRange) rangeIt.next();
			System.out.println("Constraint: " + r.getName());
			IloLinearNumExprIterator it2 = ((IloLinearNumExpr) r.getExpr()).linearIterator();
			while (it2.hasNext()) {
				System.out.println("\tVariable " + it2.nextNumVar().getName() + " has coefficient " + it2.getValue());
			}
			// get range bounds, checking for +/- infinity
			// (allowing for some rounding)
			String lb = (r.getLB() <= Double.MIN_VALUE + 1) ? "-infinity" : Double.toString(r.getLB());
			String ub = (r.getUB() >= Double.MAX_VALUE - 1) ? "+infinity" : Double.toString(r.getUB());
			System.out.println("\t" + lb + " <= LHS <= " + ub);
		}
	}

	/**
	 * Sets the parameter and transform a possible {@link IloException} into an
	 * {@link LpSolverException}.
	 * 
	 * @param param
	 *            not <code>null</code>.
	 * @param value
	 *            the value.
	 * @throws LpSolverException
	 *             if an {@link IloException} occurs while setting the parameter.
	 */
	public void setParam(final DoubleParam param, final double value) throws LpSolverException {
		checkState(m_cplex != null, "Solver has not been initialized.");
		checkNotNull(param);
		try {
			m_cplex.setParam(param, value);
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		}
	}

	/**
	 * Sets the parameter and transform a possible {@link IloException} into an
	 * {@link LpSolverException}.
	 * 
	 * @param param
	 *            not <code>null</code>.
	 * @param value
	 *            the value.
	 * @throws LpSolverException
	 *             if an {@link IloException} occurs while setting the parameter.
	 */
	public void setParam(final IntParam param, final int value) throws LpSolverException {
		checkState(m_cplex != null, "Solver has not been initialized.");
		checkNotNull(param);
		try {
			m_cplex.setParam(param, value);
		} catch (IloException exc) {
			throw new LpSolverException("Setting parameter " + param + " to value " + value + ".", exc);
		}
	}

	/**
	 * Sets the parameter and transform a possible {@link IloException} into an
	 * {@link LpSolverException}.
	 * 
	 * @param param
	 *            not <code>null</code>.
	 * @param value
	 *            the value.
	 * @throws LpSolverException
	 *             if an {@link IloException} occurs while setting the parameter.
	 */
	public void setParam(final StringParam param, final String value) throws LpSolverException {
		checkState(m_cplex != null, "Solver has not been initialized.");
		checkNotNull(param);
		try {
			m_cplex.setParam(param, value);
		} catch (IloException exc) {
			throw new LpSolverException(exc);
		}
	}

	/**
	 * Initializes the parameters of the underlying solver instance to appropriate
	 * values considering the parameters set in the given source.
	 * 
	 * @param source
	 *            not <code>null</code>.
	 * @throws LpSolverException
	 *             if an exception occurs while setting the parameters, or if some
	 *             parameters have values that are impossible to satisfy (e.g. if
	 *             both cpu and wall timings are set).
	 */
	public void setParameters(LpParameters source) throws LpSolverException {
		checkState(m_cplex != null, "Solver has not been initialized.");
		checkNotNull(source);

		setTimingParameter(source);

		final Integer maxThreads = source.getValue(LpIntParameter.MAX_THREADS);
		final int maxThreadsValue;
		if (maxThreads == null) {
			maxThreadsValue = 0;
		} else {
			maxThreadsValue = maxThreads.intValue();
		}
		setParam(IntParam.Threads, maxThreadsValue);

		if (source.getValue(LpDoubleParameter.MAX_TREE_SIZE_MB) != null) {
			setParam(DoubleParam.TreLim, source.getValue(LpDoubleParameter.MAX_TREE_SIZE_MB).doubleValue());
		}
		if (source.getValue(LpDoubleParameter.MAX_MEMORY_MB) != null) {
			setParam(DoubleParam.WorkMem, source.getValue(LpDoubleParameter.MAX_MEMORY_MB).doubleValue());
		}

		if (source.getValue(LpStringParameter.WORK_DIR) != null) {
			setParam(StringParam.WorkDir, source.getValue(LpStringParameter.WORK_DIR));
		}

		final int mode;
		if (source.getValue(LpIntParameter.DETERMINISTIC).intValue() == 0) {
			mode = ParallelMode.Opportunistic;
		} else {
			mode = ParallelMode.Deterministic;
		}
		setParam(IntParam.ParallelMode, mode);
	}

	void setTimingParameter(LpParameters source) throws LpSolverException {
		final LpTimingType timingType = LpParametersUtils.getPreferredTimingType(source);

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
		setParam(IntParam.ClockType, clockType);

		final Double timeLimit_s = LpParametersUtils.getTimeLimit(source, timingType);
		if (timeLimit_s != null) {
			setParam(DoubleParam.TiLim, timeLimit_s.doubleValue());
		}
	}

	/**
	 * Retrieves the result status equivalent to the given cplex status.
	 * 
	 * @param cplexStatus
	 *            not <code>null</code>.
	 * @param hasSolution
	 *            <code>true</code> iff the given status comes with a solution.
	 * @param objective
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	public LpResultStatus getResultStatus(CplexStatus cplexStatus, boolean hasSolution, LpObjective<V> objective) {
		checkNotNull(cplexStatus);
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
			 * NB cplex possibly returns optimal (and an objective value of zero) when no
			 * objective function is defined!
			 */
			if (objective.isComplete()) {
				resultStatus = LpResultStatus.OPTIMAL;
			} else {
				resultStatus = LpResultStatus.FEASIBLE;
			}
		} else if (cplexStatus == CplexStatus.OptimalTol) {
			if (objective.isComplete()) {
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

	private static final Logger s_logger = LoggerFactory.getLogger(SolverExtCPLEX.class);

	private ResultCplex<V> m_result;

	private final ProblemCplex<V> m_problem;

	V getVariable(IloNumVar numVar) {
		final V v = m_variablesToCplex.inverse().get(numVar);
		checkState(v != null);
		return v;
	}

	LpLinear<V> toLinear(IloLinearNumExpr iloLinear) {
		final LpLinear<V> linear = LpLinearUtils.newLinear();
		for (IloLinearNumExprIterator termsIterator = iloLinear.linearIterator(); termsIterator.hasNext();) {
			final IloNumVar numVar = termsIterator.nextNumVar();
			final V variable = getVariable(numVar);
			final double value = termsIterator.getValue();
			linear.addTerm(value, variable);
		}
		return linear;
	}

	public LpDirection toDirection(IloObjectiveSense iloSense) {
		checkNotNull(iloSense);
		if (iloSense == IloObjectiveSense.Maximize) {
			return LpDirection.MAX;
		}
		if (iloSense == IloObjectiveSense.Minimize) {
			return LpDirection.MIN;
		}
		throw new IllegalStateException("Unknown sense: " + iloSense + ".");
	}

}
