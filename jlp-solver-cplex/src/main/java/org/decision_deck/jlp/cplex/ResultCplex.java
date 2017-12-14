package org.decision_deck.jlp.cplex;

import static com.google.common.base.Preconditions.checkState;
import ilog.concert.IloException;

import org.decision_deck.jlp.parameters.LpParameters;
import org.decision_deck.jlp.result.LpResultStatus;
import org.decision_deck.jlp.result.LpSolution;
import org.decision_deck.jlp.result.LpSolverDuration;
import org.decision_deck.jlp.solver.experimental.LpResultTransient;

public class ResultCplex<V> implements LpResultTransient<V> {
	private final SolverExtCPLEX<V> m_solver;

	private final LpResultStatus m_resultStatus;

	private final LpSolverDuration m_duration;

	private boolean m_uptodate;

	private final SolutionCplex<V> m_solution;

	public ResultCplex(SolverExtCPLEX<V> underlyingSolver, LpResultStatus resultStatus, LpSolverDuration duration) {
		m_solver = underlyingSolver;
		m_resultStatus = resultStatus;
		m_duration = duration;
		m_uptodate = true;
		if (m_resultStatus.foundFeasible()) {
			final Double objectiveValue;
			if (m_resultStatus == LpResultStatus.OPTIMAL) {
				try {
					objectiveValue = m_solver.getSolutionObjectiveValue();
				} catch (IloException exc) {
					throw new IllegalStateException(exc);
				}
			} else {
				objectiveValue = null;
			}
			m_solution = new SolutionCplex<V>(underlyingSolver, objectiveValue);
		} else {
			m_solution = null;
		}
	}

	@Override
	public LpResultStatus getResultStatus() {
		return m_resultStatus;
	}

	@Override
	public LpSolverDuration getDuration() {
		return m_duration;
	}

	@Override
	public LpSolution<V> getSolution() {
		checkState(m_resultStatus.foundFeasible());
		if (!m_uptodate) {
			return null;
		}
		return m_solution;
	}

	@Override
	public LpParameters getParameters() {
		if (!m_uptodate) {
			return null;
		}
		// TODO should become invalid when requested.
		return m_solver.getParameters();
	}

	void setObsolete() {
		m_uptodate = false;
		m_solution.setInvalid();
	}
}