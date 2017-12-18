package io.github.oliviercailloux.jlp.result;

public enum ResultStatus {
	ERROR_NO_SOLUTION,
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * An error happened, or an unknown status was returned from the solver, but a
	 * feasible, non necessarily optimal, solution has been found.
	 */
	ERROR_WITH_SOLUTION, FEASIBLE, INFEASIBLE, INFEASIBLE_OR_UNBOUNDED,
	/**
	 * Because of user set limit or out-of-memory status.
	 */
	MEMORY_LIMIT_REACHED_NO_SOLUTION, MEMORY_LIMIT_REACHED_WITH_SOLUTION,
	/**
	 * Optimal or optimal to the allowed imprecision.
	 */
	OPTIMAL, TIME_LIMIT_REACHED_NO_SOLUTION, TIME_LIMIT_REACHED_WITH_SOLUTION, UNBOUNDED_NO_SOLUTION;

	/**
	 * Tests whether this return status implies that a feasible solution has been
	 * found.
	 * 
	 * @return <code>false</code> iff no feasible solution has been found, including
	 *         in the case the problem is unbounded (which implies that feasible
	 *         solutions do exist).
	 */
	public boolean foundFeasible() {
		switch (this) {
		case OPTIMAL:
		case FEASIBLE:
		case TIME_LIMIT_REACHED_WITH_SOLUTION:
		case MEMORY_LIMIT_REACHED_WITH_SOLUTION:
		case ERROR_WITH_SOLUTION:
			return true;
		case INFEASIBLE:
		case UNBOUNDED_NO_SOLUTION:
		case INFEASIBLE_OR_UNBOUNDED:
		case TIME_LIMIT_REACHED_NO_SOLUTION:
		case MEMORY_LIMIT_REACHED_NO_SOLUTION:
		case ERROR_NO_SOLUTION:
			return false;
		default:
			throw new IllegalStateException();
		}
	}
}
