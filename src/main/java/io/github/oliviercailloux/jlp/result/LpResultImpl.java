package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

import io.github.oliviercailloux.jlp.parameters.LpParameters;
import io.github.oliviercailloux.jlp.parameters.LpParametersUtils;

public class LpResultImpl implements LpResult {

	static public LpResultImpl noSolution(LpResultStatus status, LpSolverDuration duration, LpParameters parameters) {
		checkArgument(!status.foundFeasible());
		return new LpResultImpl(status, duration, parameters, null);
	}

	static public LpResultImpl withSolution(LpResultStatus status, LpSolverDuration duration, LpParameters parameters,
			LpSolution solution) {
		checkArgument(status.foundFeasible());
		checkNotNull(solution);
		return new LpResultImpl(status, duration, parameters, solution);
	}

	private final LpSolverDuration m_duration;

	private final LpParameters m_parameters;

	private final LpSolution m_solution;

	private final LpResultStatus m_status;

	LpResultImpl(LpResultStatus status, LpSolverDuration duration, LpParameters parameters, LpSolution solution) {
		checkNotNull(status);
		checkNotNull(duration);
		checkNotNull(parameters);
		m_status = status;
		m_duration = duration;
		m_parameters = LpParametersUtils.newParameters(parameters);
		m_solution = solution == null ? null : LpSolutions.newImmutable(solution);
	}

	@Override
	public LpSolverDuration getDuration() {
		return m_duration;
	}

	@Override
	public LpParameters getParameters() {
		return m_parameters;
	}

	@Override
	public LpResultStatus getResultStatus() {
		return m_status;
	}

	@Override
	public LpSolution getSolution() {
		return m_solution;
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
