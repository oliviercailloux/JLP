/**
 * Java ILP is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Java ILP is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Java ILP. If not, see http://www.gnu.org/licenses/.
 */
package io.github.oliviercailloux.jlp.result;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.problem.IMP;
import io.github.oliviercailloux.jlp.problem.ImmutableMP;
import io.github.oliviercailloux.jlp.utils.SolverUtils;
import io.github.oliviercailloux.jlp.utils.SumTermUtils;

/**
 * The class {@code ResultImpl} is a {@code Map} based implementation of the
 * {@link Solution}.
 *
 * @author lukasiewycz
 * @author Olivier Cailloux
 *
 */
public class SolutionImpl implements Solution {

	/**
	 * No <code>null</code> key or value.
	 */
	private final Map<Constraint, Double> dualValues = new HashMap<>();

	private Double objectiveValue = null;

	/**
	 * No <code>null</code> key or value.
	 */
	private final Map<Variable, Double> primalValues = new HashMap<>();

	/**
	 * Not <code>null</code>, immutable.
	 */
	private final ImmutableMP problem;

	/**
	 * A new solution satisfying the given problem. The new solution is shielded
	 * from changes to the given problem.
	 *
	 * @param problem
	 *            not <code>null</code>.
	 */
	public SolutionImpl(IMP problem) {
		Preconditions.checkNotNull(problem);
		this.problem = ImmutableMP.copyOf(problem);
	}

	/**
	 * <p>
	 * A new solution related to the given problem, with solution values copied from
	 * an other solution. The set of (dual) variables in the given problem must be a
	 * superset of the set of (dual) variables in the given solution. This
	 * constructor may be useful e.g. if a solution <em>s</em> related to a problem
	 * <em>p</em> is known to be also appliable to a different, but related,
	 * problem, e.g. a problem <em>p'</em> with relaxed constraints compared to
	 * <em>p</em>.
	 * </p>
	 * <p>
	 * The new solution is shielded from changes to the given problem and the given
	 * solution.
	 * </p>
	 *
	 * @param problem
	 *            not <code>null</code>, must contain the variables for which the
	 *            given solution has a value.
	 * @param solution
	 *            not <code>null</code>.
	 */
	public SolutionImpl(IMP problem, SolutionAlone solution) {
		Preconditions.checkNotNull(problem);
		Preconditions.checkNotNull(solution);

		for (Variable variable : solution.getVariables()) {
			final Double value = solution.getValue(variable);
			if (value != null) {
				Preconditions.checkArgument(problem.getVariables().contains(variable));
				primalValues.put(variable, value);
			}
		}
		for (Constraint constraint : solution.getConstraints()) {
			final Double value = solution.getDualValue(constraint);
			if (value != null) {
				Preconditions.checkArgument(problem.getConstraints().contains(constraint));
				dualValues.put(constraint, value);
			}
		}

		objectiveValue = solution.getObjectiveValue();
		this.problem = ImmutableMP.copyOf(problem);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Solution)) {
			return false;
		}

		Solution s2 = (Solution) obj;
		return SolverUtils.equivalent(this, s2);
	}

	@Override
	public boolean getBooleanValue(Variable variable) {
		Number number = primalValues.get(variable);
		if (number == null) {
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

	@Override
	public Double getComputedObjectiveValue() {
		final SumTerms objectiveFunction = problem.getObjective().getFunction();
		if (objectiveFunction == null) {
			return null;
		}
		return SumTermUtils.evaluate(objectiveFunction, primalValues);
	}

	@Override
	public Set<Constraint> getConstraints() {
		return Collections.unmodifiableSet(dualValues.keySet());
	}

	@Override
	public Double getDualValue(Constraint constraint) {
		Preconditions.checkNotNull(constraint);
		return dualValues.get(constraint);
	}

	@Override
	public Double getObjectiveValue() {
		return objectiveValue;
	}

	@Override
	public IMP getProblem() {
		return problem;
	}

	@Override
	public Double getValue(Variable variable) {
		Preconditions.checkNotNull(variable);
		return primalValues.get(variable);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.unmodifiableSet(primalValues.keySet());
	}

	@Override
	public int hashCode() {
		return SolverUtils.getSolutionEquivalence().hash(this);
	}

	/**
	 * @param constraint
	 *            not <code>null</code>. Must exist in the bound problem.
	 * @param value
	 *            may be <code>null</code>.
	 * @return <code>true</code> iff the call changed this object state, or
	 *         equivalently, <code>false</code> iff the given value is
	 *         <code>null</code> and the given variable did not have its dual value
	 *         set, or the given value is not <code>null</code> and equals the dual
	 *         value previously associated with the given variable.
	 */
	public boolean putDualValue(Constraint constraint, Double value) {
		Preconditions.checkNotNull(constraint);
		Preconditions.checkArgument(problem.getConstraints().contains(constraint));
		dualValues.put(constraint, value);
		final Number previous;
		if (value == null) {
			previous = dualValues.remove(constraint);
		} else {
			previous = dualValues.put(constraint, value);
		}
		return Objects.equal(previous, value);
	}

	/**
	 * @param variable
	 *            not <code>null</code>. Must exist in the bound problem.
	 * @param value
	 *            may be <code>null</code>.
	 * @return <code>true</code> iff the call changed this object state, or
	 *         equivalently, <code>false</code> iff the given value is
	 *         <code>null</code> and the given variable did not have its primal
	 *         value set, or the given value is not <code>null</code> and equals the
	 *         primal value previously associated with the given variable.
	 */
	public boolean putValue(Variable variable, Double value) {
		Preconditions.checkNotNull(variable);
		Preconditions.checkArgument(problem.getVariables().contains(variable));
		final Number previous;
		if (value == null) {
			previous = primalValues.remove(variable);
		} else {
			previous = primalValues.put(variable, value);
		}
		return Objects.equal(previous, value);
	}

	/**
	 * The bound problem must have a defined objective.
	 *
	 * @param objectiveValue
	 *            may be <code>null</code>.
	 */
	public void setObjectiveValue(Double objectiveValue) {
		Preconditions.checkState(problem.getObjective().isComplete(),
				"Objective value only with complete objective please.");
		this.objectiveValue = objectiveValue;
	}

	@Override
	public String toString() {
		return SolverUtils.getAsString(this);
	}

}