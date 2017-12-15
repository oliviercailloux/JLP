package io.github.oliviercailloux.jlp.result;

import io.github.oliviercailloux.jlp.parameters.LpParameters;

/**
 * <p>
 * A result of a problem solved by a solver, giving informations about the solve
 * attempt result. This is different from a solution: attempting to solve a
 * problem always ends up producing a result object; whereas solving a problem
 * does not necessarily yield a solution to the problem: a solution object only
 * exists if a feasible solution has been found. If the solve attempt resulted
 * in an error, a result object will be produced indicating that an error
 * occurred, but no solution will be produced.
 * </p>
 * <p>
 * Immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 * @param <V>
 *            the type of the variables.
 */
/**
 * <p>
 * A result of a problem solved by a solver, giving informations about the solve
 * attempt result. This is different from a solution: attempting to solve a
 * problem always ends up producing a result object; whereas solving a problem
 * does not necessarily yield a solution to the problem: a solution object only
 * exists if a feasible solution has been found. If the solve attempt resulted
 * in an error, a result object will be produced indicating that an error
 * occurred, but no solution will be produced.
 * </p>
 * <p>
 * A transient result has two possible states: up to date and obsolete. When it
 * is created, it is necessarily up to date. At some point it may become
 * obsolete. When it is obsolete, it does not give access to parameters and
 * solution objects, but it still gives access to duration and result status.
 * The object which exposes a {@link LpResultTransient} object should document
 * when the object will become obsolete.
 * </p>
 * <p>
 * If informations contained in such an object are going to be useful after it
 * becomes obsolete, it is advisable to create a copy of this object when it is
 * still up to date through {@link LpResultImpl} constructors.
 * </p>
 * </p>
 *
 * @author Olivier Cailloux
 *
 * @param <V>
 *            the type of the variables.
 */
public interface LpResult<V> {

	/**
	 * The duration of solving the problem. If an error occurred, this is the
	 * duration until the error.
	 *
	 * @return not <code>null</code>.
	 */
	public LpSolverDuration getDuration();

	/**
	 * Retrieves the parameters that have been used to obtain this result.
	 *
	 * @return not <code>null</code>.
	 */
	public LpParameters getParameters();

	/**
	 * Retrieves the results status obtained when solving the problem.
	 *
	 * @return not <code>null</code>.
	 */
	public LpResultStatus getResultStatus();

	/**
	 * Retrieves one solution found to the problem solved. If the result of the
	 * solve is optimal, the returned solution is an optimal solution. This method
	 * may <em>not</em> be called if no feasible solution to the problem has been
	 * found.
	 *
	 * @return not <code>null</code>. Immutable.
	 * @see #getResultStatus()
	 * @see LpResultStatus#foundFeasible()
	 */
	public LpSolution<V> getSolution();

}
