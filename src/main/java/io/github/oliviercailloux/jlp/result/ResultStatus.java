package io.github.oliviercailloux.jlp.result;

public enum ResultStatus {
	/**
	 * The solver has found a feasible solution, no objective function were given,
	 * no time or memory limit have been reached.
	 */
	FEASIBLE,
	/**
	 * The solver has determined that the mp is infeasible, no time or memory limit
	 * have been reached.
	 */
	INFEASIBLE,
	/**
	 * A user set memory limit, or out-of-memory status, has been reached. A
	 * feasible, non optimal solution might be available.
	 */
	MEMORY_LIMIT_REACHED,
	/**
	 * An optimal solution has been found within the imposed time and memory limits.
	 */
	OPTIMAL,
	/**
	 * A user set limit has been reached. A feasible, non optimal solution might be
	 * available.
	 */
	TIME_LIMIT_REACHED,
	/**
	 * Feasible solutions exist but no optimal solution exists, an objective
	 * function has been given, no time or memory limit have been reached. A
	 * feasible, non optimal solution might be available.
	 */
	UNBOUNDED
}
