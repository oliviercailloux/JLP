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

	private final LpSolverDuration duration;

	private final LpParameters parameters;

	private final LpSolution solution;

	private final LpResultStatus status;

	LpResultImpl(LpResultStatus status, LpSolverDuration duration, LpParameters parameters, LpSolution solution) {
		checkNotNull(status);
		checkNotNull(duration);
		checkNotNull(parameters);
		this.status = status;
		this.duration = duration;
		this.parameters = LpParametersUtils.newParameters(parameters);
		this.solution = solution == null ? null : LpSolutions.newImmutable(solution);
	}

	@Override
	public LpSolverDuration getDuration() {
		return duration;
	}

	@Override
	public LpParameters getParameters() {
		return parameters;
	}

	@Override
	public LpResultStatus getResultStatus() {
		return status;
	}

	@Override
	public LpSolution getSolution() {
		return solution;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("Status", status);
		helper.add("Duration", duration);
		helper.add("Parameters", parameters);
		if (solution != null) {
			helper.add("Solution", solution);
		}
		return helper.toString();
	}

}
