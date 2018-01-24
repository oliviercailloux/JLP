package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.mp.IMP;
import io.github.oliviercailloux.jlp.mp.MP;

/**
 * <p>
 * A feasible, but not necessarily optimal, solution to a mathematical program.
 * The MP is bound to this solution.
 * </p>
 * <p>
 * Immutable (provided variables are immutable).
 * </p>
 *
 * @author Olivier Cailloux
 * @see IMP
 */
public class Solution {

	/**
	 * Returns a representation of an optimal solution to the given mp, with the
	 * given values as objective value and variables values.
	 *
	 * @param mp
	 *            not <code>null</code>.
	 * @param objectiveValue
	 *            a finite value, must be zero if the given mp has the
	 *            {@link Objective#ZERO ZERO} objective.
	 * @param values
	 *            not <code>null</code>, the keys must match the variables in the
	 *            given problem.
	 * @return
	 */
	public static Solution optimal(IMP mp, double objectiveValue, Map<Variable, Double> values) {
		return new Solution(mp, objectiveValue, values, true);
	}

	/**
	 * Not <code>null</code>.
	 */
	private final MP mp;

	/**
	 * Finite.
	 */
	private final double objectiveValue;

	private final boolean optimal;

	/**
	 * Not <code>null</code>, containing no <code>null</code> keys or values.
	 */
	private final ImmutableMap<Variable, Double> values;

	/**
	 * @param mp
	 *            not <code>null</code>.
	 * @param objectiveValue
	 *            a finite value, positive zero if the mp objective is
	 *            {@link Objective#ZERO ZERO}.
	 * @param variablesValues
	 *            not <code>null</code>, must correspond to the variables in the
	 *            given mp.
	 * @param optimal
	 *            must be <code>true</code> if the mp objective is
	 *            {@link Objective#ZERO ZERO}.
	 */
	private Solution(IMP mp, double objectiveValue, Map<Variable, Double> variablesValues, boolean optimal) {
		this.mp = MP.copyOf(mp);

		checkArgument(Double.isFinite(objectiveValue));
		/** We also check that the given zero is positive. */
		checkArgument(!mp.getObjective().isZero()
				|| (objectiveValue == 0d && ((1d / objectiveValue) == Double.POSITIVE_INFINITY)));
		this.objectiveValue = objectiveValue;

		/**
		 * We must copy to ensure that the identity concept are the same for the
		 * symmetric difference.
		 */
		final Set<Variable> varsFromMap = ImmutableSet.copyOf(requireNonNull(variablesValues).keySet());
		final ImmutableSet<Variable> varsFromMp = ImmutableSet.copyOf(this.mp.getVariables());
		final SetView<Variable> diff = Sets.symmetricDifference(varsFromMap, varsFromMp);
		checkArgument(diff.isEmpty(),
				"The following variable (in total, %s variables) are present in the given variables values and not in the given mp, or conversely: %s.",
				diff.size(), diff.iterator().next());
		this.values = ImmutableMap.copyOf(variablesValues);

		checkArgument(!mp.getObjective().isZero() || optimal);
		this.optimal = optimal;
	}

	/**
	 * Two solutions are equal iff they have equal bound mathematical programs, they
	 * have the same values for the objective value and the variables values, and
	 * one is an optimal solution iff the other one is an optimal solution.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Solution)) {
			return false;
		}

		final Solution s2 = (Solution) obj;
		return objectiveValue == s2.objectiveValue && optimal == s2.optimal && mp.equals(s2.mp)
				&& values.equals(s2.values);
	}

	/**
	 * Returns the MP that this solution is about.
	 *
	 * @return not <code>null</code>.
	 */
	public MP getMP() {
		return mp;
	}

	/**
	 * Returns the value of the objective function with the solution found. Returns
	 * zero if the bound problem has the {@link Objective#ZERO ZERO} objective.
	 *
	 * @return a finite value.
	 */
	public double getObjectiveValue() {
		return objectiveValue;
	}

	/**
	 * Returns the value associated to the given variable in this solution.
	 *
	 * @param variable
	 *            not <code>null</code>, must be one of the variables in the MP
	 *            bound to this solution.
	 * @return <code>null</code> iff the variable has no associated primal value.
	 * @see #getVariables()
	 */
	public double getValue(Variable variable) {
		checkArgument(mp.getVariables().contains(requireNonNull(variable)));
		return values.get(variable);
	}

	/**
	 * Returns the variables of the MP bound to this solution. The returned list
	 * equals the list returned by {@link MP#getVariables()} when called on
	 * {@link #getMP()}.
	 *
	 * @return not <code>null</code>.
	 */
	public ImmutableList<Variable> getVariables() {
		return mp.getVariables();
	}

	@Override
	public int hashCode() {
		return Objects.hash(mp, objectiveValue, optimal, values);
	}

	/**
	 * <p>
	 * Returns <code>true</code> iff this solution is known to be an optimal
	 * solution.
	 * </p>
	 * <p>
	 * This method returns <code>false</code> iff the solution is not known to be
	 * optimal, thus, iff the solver giving this solution has not been able to prove
	 * that it is optimal. It may still be optimal.
	 * </p>
	 * <p>
	 * If the MP bound to this object has the {@link Objective#ZERO ZERO} objective,
	 * then this method returns <code>true</code>.
	 * </p>
	 *
	 * TODO delete this, not very useful.
	 *
	 * @return <code>true</code> if this solution has an objective value that no
	 *         feasible solution to the bound MP can improve.
	 */
	public boolean isOptimal() {
		return optimal;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.add("mp", mp);
		helper.add("objective value", objectiveValue);
		helper.add("optimal", optimal);
		return helper.toString();
	}

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
	@SuppressWarnings("unused")
	private boolean getBooleanValue(Variable variable) {
		Number number = values.get(variable);
		if (number == null) {
			if (!mp.getVariables().contains(variable)) {
				throw new IllegalArgumentException("Unknown variable: " + variable + ".");

			}
			throw new IllegalArgumentException("Variable has no value: " + variable + ".");
		}
		double v = number.doubleValue();
		if (Math.abs(v - 0) < 1e-6) {
			return false;
		}
		if (Math.abs(v - 1) < 1e-6) {
			return true;
		}
		throw new IllegalStateException("Variable has a non boolean value: " + variable + ".");
	}

}
