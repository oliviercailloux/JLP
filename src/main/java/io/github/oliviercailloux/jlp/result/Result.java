package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import io.github.oliviercailloux.jlp.mp.IMP;
import io.github.oliviercailloux.jlp.parameters.Configuration;

/**
 * <p>
 * A result of an attempt to solve an MP by a solver.
 * </p>
 * <p>
 * Immutable.
 * </p>
 *
 * @see IMP
 *
 * @author Olivier Cailloux
 *
 */
public class Result {
	/**
	 * Returns a result of an attempt to solve an MP that did not yield a feasible
	 * solution.
	 *
	 * @param status        not <code>null</code>, may be
	 *                      {@link ResultStatus#MEMORY_LIMIT_REACHED
	 *                      MEMORY_LIMIT_REACHED},
	 *                      {@link ResultStatus#TIME_LIMIT_REACHED
	 *                      TIME_LIMIT_REACHED}, {@link ResultStatus#INFEASIBLE
	 *                      INFEASIBLE} or {@link ResultStatus#UNBOUNDED UNBOUNDED},
	 *                      but not {@link ResultStatus#OPTIMAL OPTIMAL}.
	 * @param duration      not <code>null</code>, the duration this attempt of
	 *                      solving has taken (before finding an answer or before
	 *                      hitting a limit).
	 * @param configuration not <code>null</code>, the configuration of the solver
	 *                      used for this attempt.
	 * @return not <code>null</code>.
	 */
	public static Result noSolution(ResultStatus status, ComputationTime duration, Configuration configuration) {
		return new Result(status, duration, configuration, Optional.empty());
	}

	/**
	 * Returns a result of an attempt to solve an MP that found a feasible (and
	 * possibly optimal) solution.
	 *
	 * @param status        not <code>null</code>, may be
	 *                      {@link ResultStatus#OPTIMAL OPTIMAL},
	 *                      {@link ResultStatus#MEMORY_LIMIT_REACHED
	 *                      MEMORY_LIMIT_REACHED},
	 *                      {@link ResultStatus#TIME_LIMIT_REACHED
	 *                      TIME_LIMIT_REACHED} or {@link ResultStatus#UNBOUNDED
	 *                      UNBOUNDED}, but not {@link ResultStatus#INFEASIBLE
	 *                      INFEASIBLE}.
	 * @param duration      not <code>null</code>, the duration this attempt of
	 *                      solving has taken (before finding an answer or before
	 *                      failing).
	 * @param configuration not <code>null</code>, the configuration of the solver
	 *                      used for this attempt.
	 * @param solution      not <code>null</code>, the feasible solution found.
	 * @return not <code>null</code>.
	 */
	public static Result withSolution(ResultStatus status, ComputationTime duration, Configuration configuration,
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

	/**
	 * This constructor will check the given parameters according to the documented
	 * semantics of the given status. (Though it does not check that the solution is
	 * feasible.)
	 *
	 * @param status        not <code>null</code>.
	 * @param duration      not <code>null</code>.
	 * @param configuration not <code>null</code>.
	 * @param solution      not <code>null</code>.
	 * @see ResultStatus
	 */
	Result(ResultStatus status, ComputationTime duration, Configuration configuration, Optional<Solution> solution) {
		this.status = requireNonNull(status);
		this.duration = requireNonNull(duration);
		this.configuration = requireNonNull(configuration);
		this.solution = requireNonNull(solution);
		switch (status) {
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
			checkArgument(!solution.isPresent() || !solution.get().getMP().getObjective().isZero());
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
	 * @see ResultStatus
	 */
	public ResultStatus getResultStatus() {
		return status;
	}

	/**
	 * <p>
	 * Returns a solution to the MP, if one has been found.
	 * <p>
	 * If the result status is {@link ResultStatus#OPTIMAL OPTIMAL}, this method is
	 * guaranteed to return a non empty optional. Otherwise, a feasible solution
	 * (non necessarily optimal) is returned if one has been found.
	 * </p>
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
