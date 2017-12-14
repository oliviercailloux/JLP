package io.github.oliviercailloux.jlp.result;

import java.util.Set;

import io.github.oliviercailloux.jlp.LpConstraint;

/**
 * <p>
 * A valid, but not necessarily optimal, solution to a mathematical program. The
 * problem that this solution satisfies is <em>not</em> bound to this solution
 * object. The interface {@link LpSolution} should be preferred to this one when
 * the related problem can be bound to the solution, see documentation there.
 * </p>
 * 
 * @param <V>
 *            the type of the variables.
 * 
 * @author Olivier Cailloux
 * 
 */
public interface LpSolutionAlone<V> {

	/**
	 * <p>
	 * A convenience method to return the primal value of the given variable as a
	 * boolean. This is <code>true</code> iff the value is one ± 1e-6,
	 * <code>false</code> iff the value is zero ± 1e-6. Otherwize a runtime
	 * exception is thrown.
	 * </p>
	 * <p>
	 * It is suggested to ensure, after a solution is obtained by a solver and
	 * before using this method, that the supposedly boolean variables are indeed
	 * set to a value that is zero or one ± 1e-6 because some solvers might have an
	 * imprecision factor higher than this.
	 * </p>
	 * 
	 * @param variable
	 *            not <code>null</code>, must have an associated value close enough
	 *            to zero or one.
	 * @return <code>true</code> for one, <code>false</code> for zero.
	 */
	public boolean getBooleanValue(V variable);

	/**
	 * Retrieves a copy or read-only view of the primal constraints, i.e. dual
	 * variables, which have their dual value set.
	 * 
	 * @return not <code>null</code>, but may be empty.
	 */
	public Set<LpConstraint<V>> getConstraints();

	/**
	 * Returns, if it is known, the value corresponding to the dual variable
	 * associated to the given primal constraint.
	 * 
	 * @param constraint
	 *            not <code>null</code>.
	 * @return <code>null</code> iff the variable has no associated dual value.
	 */
	public Number getDualValue(LpConstraint<V> constraint);

	/**
	 * Returns the objective value.
	 * 
	 * @return <code>null</code> if not set.
	 */
	public Number getObjectiveValue();

	/**
	 * Returns the primal value of the variable, if it is known. TODO indicate what
	 * happens if variable is unknown.
	 * 
	 * @param variable
	 *            not <code>null</code>.
	 * @return <code>null</code> iff the variable has no associated primal value.
	 */
	public Number getValue(V variable);

	/**
	 * Retrieves a copy or read-only view of the variables which have their primal
	 * value set.
	 * 
	 * @return not <code>null</code>, but may be empty.
	 */
	public Set<V> getVariables();

}
