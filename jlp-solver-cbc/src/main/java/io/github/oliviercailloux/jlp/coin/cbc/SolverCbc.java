package io.github.oliviercailloux.jlp.coin.cbc;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.swimp.coin.CoinPackedVector;
import net.sourceforge.swimp.coin.OsiIntParam;
import net.sourceforge.swimp.coin.OsiSolverInterface;
import net.sourceforge.swimp.osicbc.OsiCbcSolverInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterators;
import com.google.common.io.OutputSupplier;

import io.github.oliviercailloux.jlp.AbstractLpSolver;
import io.github.oliviercailloux.jlp.LpConstraint;
import io.github.oliviercailloux.jlp.LpDirection;
import io.github.oliviercailloux.jlp.LpFileFormat;
import io.github.oliviercailloux.jlp.LpLinear;
import io.github.oliviercailloux.jlp.LpOperator;
import io.github.oliviercailloux.jlp.LpSolverException;
import io.github.oliviercailloux.jlp.LpTerm;
import io.github.oliviercailloux.jlp.problem.LpVariableType;
import io.github.oliviercailloux.jlp.result.LpResultStatus;
import io.github.oliviercailloux.jlp.result.LpSolutionImpl;
import io.github.oliviercailloux.jlp.utils.LpSolverUtils;
import io.github.oliviercailloux.jlp.utils.TimingHelper;

/**
 * Work in progress!
 * 
 * @param <T>
 *            the class used for the variables.
 * 
 * @author Olivier Cailloux
 * 
 * 
 */
public class SolverCbc<T> extends AbstractLpSolver<T> {
	private static final Logger s_logger = LoggerFactory.getLogger(SolverCbc.class);

	private OsiSolverInterface m_solver;

	private LpFileFormat m_currentFormat;

	private ImmutableBiMap<T, Integer> m_variablesToIndex;

	private static final int LAZY_NAMES = 1;

	/**
	 * Creates a new solver instance.
	 */
	public SolverCbc() {
		m_solver = null;
		m_currentFormat = null;
		m_variablesToIndex = null;
	}

	@Override
	public void close() throws LpSolverException {
		if (m_solver != null) {
			m_solver.delete();
			m_solver = null;
		}
	}

	@Override
	public LpFileFormat getPreferredFormat() throws LpSolverException {
		return null;
	}

	@Override
	public Object getUnderlyingSolver() throws LpSolverException {
		lazyInit();
		return m_solver;
	}

	private void lazyInit() throws LpSolverException {
		if (m_solver != null) {
			return;
		}
		checkState(getProblem().getObjective().isEmpty() || getProblem().getObjective().isComplete(),
				"Objective function set without a direction (or inverse).");

		try {
			m_solver = new OsiCbcSolverInterface();

			setParameters();

			final boolean success = m_solver.setIntParam(0, 0);
			if (!success) {
				throw new LpSolverException("Couldn't set param.");
			}
			// m_solver.setName(getProblem().getName());
			s_logger.info("Building problem {}.", getProblem().getName());

			{
				final ImmutableBiMap.Builder<T, Integer> variablesToIndexBuilder = ImmutableBiMap.builder();
				int varIndex = 0;
				for (T variable : getProblem().getVariables()) {
					final String varName = getVariableName(variable, m_currentFormat);
					final LpVariableType varType = getProblem().getVariableType(variable);
					final Number lowerBound = LpSolverUtils.getVarLowerBoundBounded(getProblem(), variable);
					final Number upperBound = LpSolverUtils.getVarUpperBoundBounded(getProblem(), variable);

					m_solver.setContinuous(2);
					final int numCols = m_solver.getNumCols();
					s_logger.info("Num cols: {}.", numCols);
					m_solver.setColBounds(varIndex, lowerBound.doubleValue(), upperBound.doubleValue());

					if (varType.isInt()) {
						m_solver.setInteger(varIndex);
					}
					// if (varName.isEmpty()) {
					// num = m_cplex.numVar(lb, ub, type);
					// } else {
					// num = m_cplex.numVar(lb, ub, type, varName);
					// }
					s_logger.debug("Set variable {} with bounds " + lowerBound + ", " + upperBound + ", integer? "
							+ varType.isInt() + ", name " + varName + ".", variable);

					variablesToIndexBuilder.put(variable, Integer.valueOf(varIndex));
					++varIndex;
				}
				m_variablesToIndex = variablesToIndexBuilder.build();
			}

			LpConstraint<T> constraint1;
			LpConstraint<T> constraint2;
			final Iterator<LpConstraint<T>> constraintsIterator = getProblem().getConstraints().iterator();
			constraint1 = Iterators.getNext(constraintsIterator, null);
			constraint2 = Iterators.getNext(constraintsIterator, null);
			while (constraint1 != null) {
				final LpLinear<T> linear1 = constraint1.getLhs();
				final String name1 = getConstraintName(constraint1, m_currentFormat);
				final LpOperator op1 = constraint1.getOperator();
				final CoinPackedVector coinLinear = getCoinLinear(linear1);
				boolean merge;
				final double upperBound;
				final double lowerBound;
				if (constraint2 != null) {
					final LpLinear<T> linear2 = constraint2.getLhs();
					final String name2 = getConstraintName(constraint2, m_currentFormat);
					final LpOperator op2 = constraint2.getOperator();
					final boolean opLeGe = op1 == LpOperator.LE && op2 == LpOperator.GE;
					final boolean opGeLe = op1 == LpOperator.GE && op2 == LpOperator.LE;
					merge = linear1.equals(linear2) && name1.equals(name2) && (opLeGe || opGeLe);
				} else {
					merge = false;
				}
				if (merge) {
					assert constraint2 != null;
					if (op1 == LpOperator.LE) {
						upperBound = constraint1.getRhs();
						lowerBound = constraint2.getRhs();
					} else {
						upperBound = constraint2.getRhs();
						lowerBound = constraint1.getRhs();
					}
					constraint1 = Iterators.getNext(constraintsIterator, null);
					constraint2 = Iterators.getNext(constraintsIterator, null);
				} else {
					if (op1 == LpOperator.LE) {
						upperBound = constraint1.getRhs();
						lowerBound = Double.NEGATIVE_INFINITY;
					} else if (op1 == LpOperator.GE) {
						upperBound = Double.POSITIVE_INFINITY;
						lowerBound = constraint1.getRhs();
					} else {
						assert op1 == LpOperator.EQ;
						upperBound = constraint1.getRhs();
						lowerBound = constraint1.getRhs();
					}
					constraint1 = constraint2;
				}
				m_solver.addRow(coinLinear, lowerBound, upperBound);
			}

			if (getProblem().getObjective().isComplete()) {
				final LpLinear<T> objective = getProblem().getObjective().getFunction();
				for (LpTerm<T> lpTerm : objective) {
					final int varIndex = m_variablesToIndex.get(lpTerm.getVariable()).intValue();
					final double coefficient = lpTerm.getCoefficient();
					m_solver.setObjCoeff(varIndex, coefficient);
				}
				m_solver.setObjSense(getCoinSense(getProblem().getObjective().getDirection()));
			}
		} catch (LpSolverException exc) {
			close();
			throw exc;
		}
	}

	private double getCoinSense(LpDirection direction) {
		checkNotNull(direction);
		return direction == LpDirection.MIN ? 1d : -1d;
	}

	private CoinPackedVector getCoinLinear(final LpLinear<T> linear) {
		final CoinPackedVector rowVector = new CoinPackedVector();
		for (LpTerm<T> lpTerm : linear) {
			final int varIndex = m_variablesToIndex.get(lpTerm.getVariable()).intValue();
			final double coefficient = lpTerm.getCoefficient();
			rowVector.insert(varIndex, coefficient);
		}
		return rowVector;
	}

	@Override
	protected LpResultStatus solveUnderlying() throws LpSolverException {
		lazyInit();

		try {
			final TimingHelper timingHelper = new TimingHelper();
			timingHelper.start();
			m_solver.branchAndBound();
			timingHelper.stop();

			m_lastDuration = timingHelper.getDuration();

			final boolean abandoned = m_solver.isAbandoned();
			final boolean optimal = m_solver.isProvenOptimal();
			final boolean inf = m_solver.isProvenPrimalInfeasible();
			final boolean unb = m_solver.isProvenDualInfeasible();
			checkState(abandoned ^ optimal ^ (inf || unb));
			if (abandoned) {
				return LpResultStatus.ERROR_NO_SOLUTION;
			}
			if (inf || unb) {
				return LpResultStatus.INFEASIBLE_OR_UNBOUNDED;
			}

			final double objValue = m_solver.getObjValue();
			final double[] solutions = m_solver.getColSolutionVec();

			final LpSolutionImpl<T> solution = new LpSolutionImpl<T>(getProblem());

			for (Integer varIndex : m_variablesToIndex.inverse().keySet()) {
				final T variable = m_variablesToIndex.inverse().get(varIndex);

				s_logger.debug("Querying value of {}.", variable);
				final double value = solutions[varIndex.intValue()];
				s_logger.debug("Value of {} is " + value + ".", varIndex);

				solution.putValue(variable, Double.valueOf(value));
			}
			solution.setObjectiveValue(Double.valueOf(objValue));
			setSolution(solution);

			return LpResultStatus.OPTIMAL;
		} finally {
			close();
		}
	}

	private void setParameters() {
		final int nameDiscipline = OsiIntParam.OsiNameDiscipline;
		final boolean success = m_solver.setIntParam(nameDiscipline, LAZY_NAMES);
		checkState(success);
	}

	@Deprecated
	@Override
	public void writeProblem(LpFileFormat format, String file, boolean addExtension) throws LpSolverException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeProblem(LpFileFormat format, OutputSupplier<? extends Writer> destination)
			throws LpSolverException, IOException {
		m_currentFormat = format;
		lazyInit();
		try {
			m_solver.writeMps("out");
		} finally {
			close();
		}
		throw new UnsupportedOperationException();
	}
}
