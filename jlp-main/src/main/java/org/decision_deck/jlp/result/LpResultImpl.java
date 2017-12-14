package org.decision_deck.jlp.result;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jlp.parameters.LpParameters;
import org.decision_deck.jlp.parameters.LpParametersUtils;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class LpResultImpl<V> implements LpResult<V> {

	private final LpResultStatus m_status;

	private final LpSolverDuration m_duration;

	private final LpSolution<V> m_solution;

	private final LpParameters m_parameters;

	LpResultImpl(LpResultStatus status, LpSolverDuration duration, LpParameters parameters, LpSolution<V> solution) {
		checkNotNull(status);
		checkNotNull(duration);
		checkNotNull(parameters);
		m_status = status;
		m_duration = duration;
		m_parameters = LpParametersUtils.newParameters(parameters);
		m_solution = solution == null ? null : LpSolutions.newImmutable(solution);
	}

	static public <V> LpResultImpl<V> withSolution(LpResultStatus status, LpSolverDuration duration,
			LpParameters parameters, LpSolution<V> solution) {
		checkArgument(status.foundFeasible());
		checkNotNull(solution);
		return new LpResultImpl<V>(status, duration, parameters, solution);
	}

	@Override
	public LpResultStatus getResultStatus() {
		return m_status;
	}

	@Override
	public LpSolverDuration getDuration() {
		return m_duration;
	}

	@Override
	public LpSolution<V> getSolution() {
		return m_solution;
	}

	static public <V> LpResultImpl<V> noSolution(LpResultStatus status, LpSolverDuration duration,
			LpParameters parameters) {
		checkArgument(!status.foundFeasible());
		return new LpResultImpl<V>(status, duration, parameters, null);
	}

	@Override
	public LpParameters getParameters() {
		return m_parameters;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("Status", m_status);
		helper.add("Duration", m_duration);
		helper.add("Parameters", m_parameters);
		if (m_solution != null) {
			helper.add("Solution", m_solution);
		}
		return helper.toString();
	}

}
