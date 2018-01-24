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
	 * Returns a representation of a feasible (and possibly optimal) solution to the
	 * given MP, with the given values as objective value and variables values.
	 *
	 * @param mp
	 *            not <code>null</code>.
	 * @param objectiveValue
	 *            a finite value, must be zero if the given MP has the
	 *            {@link Objective#ZERO ZERO} objective.
	 * @param values
	 *            not <code>null</code>, the keys must match the variables in the
	 *            given problem.
	 * @return
	 */
	public static Solution of(IMP mp, double objectiveValue, Map<Variable, Double> values) {
		return new Solution(mp, objectiveValue, values);
	}

	/**
	 * Not <code>null</code>.
	 */
	private final MP mp;

	/**
	 * Finite.
	 */
	private final double objectiveValue;

	/**
	 * Not <code>null</code>, containing no <code>null</code> keys or values.
	 */
	private final ImmutableMap<Variable, Double> values;

	/**
	 * @param mp
	 *            not <code>null</code>.
	 * @param objectiveValue
	 *            a finite value, zero if the mp objective is {@link Objective#ZERO
	 *            ZERO}.
	 * @param variablesValues
	 *            not <code>null</code>, must correspond to the variables in the
	 *            given mp.
	 */
	private Solution(IMP mp, double objectiveValue, Map<Variable, Double> variablesValues) {
		this.mp = MP.copyOf(mp);

		checkArgument(Double.isFinite(objectiveValue));
		checkArgument(!mp.getObjective().isZero() || objectiveValue == 0d);
		/** We make sure that zero is a positive zero. */
		this.objectiveValue = objectiveValue == 0d ? 0d : objectiveValue;

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
	}

	/**
	 * Two solutions are equal iff they have equal bound mathematical programs and
	 * have the same values for the objective value and the variables values.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Solution)) {
			return false;
		}

		final Solution s2 = (Solution) obj;
		return objectiveValue == s2.objectiveValue && mp.equals(s2.mp) && values.equals(s2.values);
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
		return Objects.hash(mp, objectiveValue, values);
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.add("mp", mp);
		helper.add("objective value", objectiveValue);
		return helper.toString();
	}

}
