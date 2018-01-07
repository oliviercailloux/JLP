package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import io.github.oliviercailloux.jlp.parameters.Configuration;

/**
 * <p>
 * A result of an attempt to solve an mp by a solver.
 * </p>
 * <p>
 * Immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Result {
	static public Result noSolution(ResultStatus status, ComputationTime duration, Configuration configuration) {
		return new Result(status, duration, configuration, Optional.empty());
	}

	static public Result withSolution(ResultStatus status, ComputationTime duration, Configuration configuration,
			Solution solution) {
		return new Result(status, duration, configuration, Optional.of(solution));
	}

	/**
	 * Not <code>null</code>.
	 */
	private final Configuration configuration;

	/**
	 * Not <code>null</code>.
	 */
	private final ComputationTime duration;

	/**
	 * Not <code>null</code>.
	 */
	private final Optional<Solution> solution;

	/**
	 * Not <code>null</code>.
	 */
	private final ResultStatus status;

	Result(ResultStatus status, ComputationTime duration, Configuration configuration, Optional<Solution> solution) {
		this.status = requireNonNull(status);
		this.duration = requireNonNull(duration);
		this.configuration = requireNonNull(configuration);
		this.solution = requireNonNull(solution);
		switch (status) {
		case FEASIBLE:
			checkArgument(solution.isPresent());
			break;
		case INFEASIBLE:
			checkArgument(!solution.isPresent());
			break;
		case MEMORY_LIMIT_REACHED:
		case TIME_LIMIT_REACHED:
			break;
		case OPTIMAL:
			checkArgument(solution.isPresent());
			break;
		case UNBOUNDED:
			break;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns the configuration that has been used to obtain this result.
	 *
	 * @return not <code>null</code>.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the duration of this attempt to solve the mp. If an error occurred,
	 * this is the duration until the error.
	 *
	 * @return not <code>null</code>.
	 */
	public ComputationTime getDuration() {
		return duration;
	}

	/**
	 * Returns the status obtained as a result from the solving attempt.
	 *
	 * @return not <code>null</code>.
	 */
	public ResultStatus getResultStatus() {
		return status;
	}

	/**
	 * Returns a solution to the mp, if one has been found. If the result of the
	 * solve is optimal, the returned solution is an optimal solution.
	 *
	 * @return not <code>null</code>, a solution if a solution has been found, an
	 *         empty optional otherwise.
	 * @see #getResultStatus()
	 */
	public Optional<Solution> getSolution() {
		return solution;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.add("Status", status);
		helper.add("Duration", duration);
		helper.add("Configuration", configuration);
		helper.add("Solution", solution);
		return helper.toString();
	}

}
