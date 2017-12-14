package org.decision_deck.jlp.cplex;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import ilog.concert.IloException;

import java.util.Set;

import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.result.LpSolution;

class SolutionCplex<V> implements LpSolution<V> {
	private boolean m_valid;

	/**
	 * Not <code>null</code>.
	 */
	private final SolverExtCPLEX<V> m_solver;

	private final Double m_objectiveValue;

	/**
	 * @param underlyingSolver
	 *            not <code>null</code>.
	 * @param objectiveValue
	 *            may be <code>null</code>.
	 */
	public SolutionCplex(SolverExtCPLEX<V> underlyingSolver, Double objectiveValue) {
		checkNotNull(underlyingSolver);
		m_solver = underlyingSolver;
		m_objectiveValue = objectiveValue;
		m_valid = true;
	}

	@Override
	public Set<LpConstraint<V>> getConstraints() {
		checkState(m_valid, "This solution object is invalid. It may not be queried any more.");
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getBooleanValue(V variable) {
		checkState(m_valid, "This solution object is invalid. It may not be queried any more.");
		final Number value = getValue(variable);
		/** duplicated code! */
		double v = value.doubleValue();
		if (Math.abs(v - 0) < 1e-6) {
			return false;
		}
		if (Math.abs(v - 1) < 1e-6) {
			return true;
		}
		throw new IllegalStateException("Variable has a non boolean value: " + variable + ".");
	}

	@Override
	public Number getComputedObjectiveValue() {
		checkState(m_valid, "This solution object is invalid. It may not be queried any more.");
		final LpLinear<V> objectiveFunction = getProblem().getObjective().getFunction();
		if (objectiveFunction == null) {
			return null;
		}
		// return LpLinearUtils.evaluate(objectiveFunction, m_primalValues);
		throw new UnsupportedOperationException();
	}

	@Override
	public Number getDualValue(LpConstraint<V> constraint) {
		checkState(m_valid, "This solution object is invalid. It may not be queried any more.");
		throw new UnsupportedOperationException();
	}

	@Override
	public Number getObjectiveValue() {
		checkState(m_valid, "This solution object is invalid. It may not be queried any more.");
		return m_objectiveValue;
	}

	/**
	 * Note that the returned problem becomes invalid when this object becomes
	 * invalid. This is not implemented, the returned object will continue to work
	 * after it has become logically invalid. However, it should not be used any
	 * more!
	 */
	@Override
	public LpProblem<V> getProblem() {
		checkState(m_valid, "This solution object is invalid. It may not be queried any more.");
		return m_solver.getMP();
	}

	void setInvalid() {
		m_valid = false;
	}

	@Override
	public Number getValue(V variable) {
		checkState(m_valid, "This solution object is invalid. It may not be queried any more.");
		if (!getProblem().getVariables().contains(variable)) {
			return null;
		}
		try {
			return m_solver.getSolutionValue(variable);
		} catch (IloException exc) {
			throw new IllegalStateException(exc);
		}
	}

	@Override
	public Set<V> getVariables() {
		checkState(m_valid, "This solution object is invalid. It may not be queried any more.");
		return getProblem().getVariables();
	}
}