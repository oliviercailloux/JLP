package io.github.oliviercailloux.jlp.result;

import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.mp.IMP;

/**
 * <p>
 * An attempt to solve an MP results in one of the following state.
 * <ul>
 * <li>If the attempt has hit a limit, the result status is
 * {@link #MEMORY_LIMIT_REACHED} or {@link #TIME_LIMIT_REACHED}.</li>
 * <li>Otherwise, the solver has discovered something about the MP. If the MP is
 * known to admit no feasible solution, the result status is
 * {@link #INFEASIBLE}.</li>
 * <li>Otherwise, the MP is known to admit at least one feasible solution, and
 * the solver has concluded about the MP. In that case, the result status is
 * {@link #OPTIMAL} if the solver has found an optimal solution, or
 * {@link #UNBOUNDED} if the solver has determined that the MP is
 * unbounded.</li>
 * </ul>
 * </p>
 *
 * @author Olivier Cailloux
 * @see IMP
 *
 */
public enum ResultStatus {
	/**
	 * An optimal solution has been found within the imposed time and memory limits.
	 * (Recall that for an MP that has the {@link Objective#ZERO ZERO} objective,
	 * all feasible solutions are optimal.)
	 */
	OPTIMAL,
	/**
	 * The solver has determined that the mp is infeasible, no time or memory limit
	 * have been reached.
	 */
	INFEASIBLE,
	/**
	 * Feasible solutions exist but no optimal solution exists, an objective
	 * function has been given, and no time or memory limit have been reached. A
	 * feasible, non optimal solution might be available.
	 */
	UNBOUNDED,
	/**
	 * A user set time limit has been reached. A feasible solution (not known to be
	 * optimal) might be available.
	 */
	TIME_LIMIT_REACHED,
	/**
	 * A user set memory limit, or out-of-memory status, has been reached. A
	 * feasible solution (not known to be optimal) might be available.
	 */
	MEMORY_LIMIT_REACHED
}
