package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
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
 * In general, and depending on the solver, values associated to variables with
 * integer domains may not necessarily be assumed to be integers. See “Slightly
 * infeasible integer variables” in CPLEX help for an example.
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
	 * @param mp             not <code>null</code>.
	 * @param objectiveValue a finite value, must be zero if the given MP has the
	 *                       {@link Objective#ZERO ZERO} objective.
	 * @param values         not <code>null</code>, the keys must match the
	 *                       variables in the given MP.
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
	 * @param mp              not <code>null</code>.
	 * @param objectiveValue  a finite value, zero if the mp objective is
	 *                        {@link Objective#ZERO ZERO}.
	 * @param variablesValues not <code>null</code>, must correspond to the
	 *                        variables in the given mp.
	 */
	private Solution(IMP mp, double objectiveValue, Map<Variable, Double> variablesValues) {
		this.mp = MP.copyOf(mp);

		checkArgument(Double.isFinite(objectiveValue));
		checkArgument(!mp.getObjective().isZero() || objectiveValue == 0d);
		/** We make sure that zero is a positive zero. */
		this.objectiveValue = objectiveValue == 0d ? 0d : objectiveValue;
		this.values = ImmutableMap.copyOf(variablesValues);

		/**
		 * We should ideally copy to new ImmutableSets to ensure that the underlying
		 * equivalence relations are the same for the symmetric difference. (Otherwise,
		 * one could be an ImmutableSortedSet for example.)
		 */
		final ImmutableSet<Variable> varsFromMap = values.keySet();
		final ImmutableSet<Variable> varsFromMp = ImmutableSet.copyOf(this.mp.getVariables());
		final SetView<Variable> diff = Sets.symmetricDifference(varsFromMap, varsFromMp);
		if (!diff.isEmpty()) {
			throw new IllegalArgumentException(
					String.format(
							"The following variable (in total, %s variables) is present in the given variables values "
									+ "and not in the given mp, or conversely: %s.",
							diff.size(), diff.iterator().next()));
		}
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
	 * zero if the bound MP has the {@link Objective#ZERO ZERO} objective.
	 *
	 * @return a finite value.
	 */
	public double getObjectiveValue() {
		return objectiveValue;
	}

	/**
	 * Returns the value associated to the given variable in this solution.
	 *
	 * @param variable not <code>null</code>, must be one of the variables in the MP
	 *                 bound to this solution.
	 * @return the primal value associated to the given variable.
	 * @see #getVariables()
	 */
	public double getValue(Variable variable) {
		checkArgument(mp.getVariables().contains(requireNonNull(variable)));
		assert values.containsKey(variable);
		return values.get(variable);
	}

	/**
	 * Returns the variables of the MP bound to this solution and their values. The
	 * returned key set contains the same variables in the same order than the list
	 * returned by {@link MP#getVariables()} when called on {@link #getMP()}.
	 *
	 * @return not <code>null</code>.
	 */
	public ImmutableMap<Variable, Double> getVariableValues() {
		assert getMP().getVariables().equals(values.keySet().asList());
		return values;
	}

	/**
	 * Two solutions are equal iff they have equal bound mathematical programs and
	 * have the same values for the objective value and the variables values.
	 */
	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Solution)) {
			return false;
		}

		final Solution s2 = (Solution) o2;
		return objectiveValue == s2.objectiveValue && mp.equals(s2.mp) && values.equals(s2.values);
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
		helper.add("values", values);
		return helper.toString();
	}

}
