package io.github.oliviercailloux.jlp.result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import io.github.oliviercailloux.jlp.elements.LpConstraint;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.problem.LpProblem;

/**
 * <p>
 * A feasible, but not necessarily optimal, result of a {@link LpProblem}. The
 * problem that this solution satisfies is bound to this solution object. This
 * permits to also query for, e.g., constraints values, provided the adequate
 * variables have a value set.
 * </p>
 * <p>
 * The type of the objects used for the variables should have their
 * {@link #equals(Object)} method implemented in a meaningful way as this is
 * used when retrieving the values, and their {@link #hashCode()} method should
 * be correctly implemented. The variables should be immutable.
 * </p>
 * <p>
 * Two solutions are {@link #equals(Object)} iff they have the same values after
 * conversion by {@link Number#doubleValue()} for the objective value and the
 * variables (primal and dual) values, and their bound problem is equal.
 * </p>
 * <p>
 * This interface has been designed for use with immutable numbers. The types
 * {@link Double}, {@link Integer}, {@link BigDecimal}, {@link BigInteger} will
 * pose no problem. Using other types as numbers is unsupported.
 * </p>
 *
 * @param <V>
 *            the type of the variables.
 *
 * @author Olivier Cailloux
 *
 */
public interface LpSolution<V> extends LpSolutionAlone<V> {

	/**
	 * <p>
	 * A convenience method to return the primal value of the given variable as a
	 * boolean. This method takes the solver parameters into account to determine if
	 * a value represents a boolean <code>true</code> or <code>false</code>, even
	 * when the value is different than 1d or 0d. This may be so e.g. because the
	 * solver introduces tolerances, thus a value of 0.00001 may be a boolean
	 * <code>false</code>. If the value of the given variable does not correspond to
	 * a boolean, e.g. because its value is more different than zero and than one
	 * than allowed by the tolerance used by the given solver, this method throws an
	 * exception.
	 * </p>
	 * TODO the implementation should use a tolerance depending on the solver,
	 * currently is fixed at 1e-6.
	 * <p>
	 * If the given variable is not in the bound problem, an exception is thrown.
	 * </p>
	 *
	 * @param variable
	 *            not <code>null</code>, must have an associated value close enough
	 *            to zero or one.
	 * @return <code>true</code> for one, <code>false</code> for zero.
	 */
	@Override
	public boolean getBooleanValue(Variable variable);

	/**
	 * Retrieves the value of the objective function computed from the objective
	 * function itself with the values of the variables set in this solution.
	 * Returns <code>null</code> if the objective function is not set in the bound
	 * problem or one of the variables required value is not set.
	 *
	 * @return possibly <code>null</code>.
	 */
	public Number getComputedObjectiveValue();

	/**
	 * Returns, if it is known, the value corresponding to the dual variable
	 * associated to the given primal constraint. Returns necessarily
	 * <code>null</code> if the constraint is not in the associated problem.
	 *
	 * @param constraint
	 *            not <code>null</code>.
	 * @return <code>null</code> iff the variable has no associated dual value.
	 */
	@Override
	public Number getDualValue(LpConstraint<V> constraint);

	/**
	 * Returns the objective value. Returns necessarily <code>null</code> if the
	 * bound problem has no objective function.
	 *
	 * @return <code>null</code> if not set.
	 */
	@Override
	public Number getObjectiveValue();

	/**
	 * Retrieves the problem that this solution solves.
	 *
	 * @return not <code>null</code>, immutable.
	 */
	public LpProblem<V> getProblem();

	/**
	 * Returns the primal value of the variable, if it is known. Returns necessarily
	 * <code>null</code> if the given variable is not in the bound problem.
	 *
	 * @param variable
	 *            not <code>null</code>.
	 * @return <code>null</code> iff the variable has no associated primal value.
	 */
	@Override
	public Number getValue(Variable variable);

	/**
	 * Retrieves a copy or read-only view of the variables which have a solution
	 * value. The returned set is guaranteed to be included in the set of variables
	 * contained in the bound problem.
	 *
	 * @return not <code>null</code>, but may be empty.
	 */
	@Override
	public Set<Variable> getVariables();

}
