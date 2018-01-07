package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

import io.github.oliviercailloux.jlp.result.parameters.SolverParameters;
import io.github.oliviercailloux.jlp.result.parameters.SolverParametersUtils;

public class ResultImpl implements Result {

	static public ResultImpl noSolution(ResultStatus status, SolverDuration duration, SolverParameters parameters) {
		checkArgument(!status.foundFeasible());
		return new ResultImpl(status, duration, parameters, null);
	}

	static public ResultImpl withSolution(ResultStatus status, SolverDuration duration, SolverParameters parameters,
			Solution solution) {
		checkArgument(status.foundFeasible());
		checkNotNull(solution);
		return new ResultImpl(status, duration, parameters, solution);
	}

	private final SolverDuration duration;

	private final SolverParameters parameters;

	private final Solution solution;

	private final ResultStatus status;

	ResultImpl(ResultStatus status, SolverDuration duration, SolverParameters parameters, Solution solution) {
		checkNotNull(status);
		checkNotNull(duration);
		checkNotNull(parameters);
		this.status = status;
		this.duration = duration;
		this.parameters = SolverParametersUtils.newParameters(parameters);
		this.solution = solution == null ? null : Solutions.newImmutable(solution);
	}

	@Override
	public SolverDuration getDuration() {
		return duration;
	}

	@Override
	public SolverParameters getParameters() {
		return parameters;
	}

	@Override
	public ResultStatus getResultStatus() {
		return status;
	}

	@Override
	public Solution getSolution() {
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
