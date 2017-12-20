package io.github.oliviercailloux.jlp.result;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.problem.IMP;
import io.github.oliviercailloux.jlp.problem.ImmutableMP;
import io.github.oliviercailloux.jlp.utils.SolverUtils;
import io.github.oliviercailloux.jlp.utils.SumTermUtils;

public class SolutionImmutable implements Solution {

	/**
	 * No <code>null</code> key or value.
	 */
	private final Map<Constraint, Double> dualValues;

	private final double objectiveValue;

	private final ImmutableMap<Variable, Double> primalValues;

	private final IMP problem;

	/**
	 * <p>
	 * An immutable solution related to the given problem, with solution values
	 * copied from an other solution. The set of variables in the given problem must
	 * be a superset of the set of variables in the given solution. This constructor
	 * may be useful e.g. if a solution <em>s</em> related to a problem <em>p</em>
	 * is known to be also appliable to a different, but related, problem, e.g. a
	 * problem <em>p'</em> with relaxed constraints compared to <em>p</em>.
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
	public SolutionImmutable(IMP problem, SolutionAlone solution) {
		this(problem, solution, true);
	}

	/**
	 * Copy constructor by value.
	 *
	 * @param solution
	 *            not <code>null</code>.
	 */
	public SolutionImmutable(Solution solution) {
		this(solution.getProblem(), solution, false);
	}

	private SolutionImmutable(IMP problem, SolutionAlone solution, boolean protectProblem) {
		Preconditions.checkNotNull(solution);
		final Builder<Variable, Double> primalValuesB = ImmutableMap.builder();
		final Builder<Constraint, Double> dualValuesB = ImmutableMap.builder();

		for (Variable variable : solution.getVariables()) {
			final Double value = solution.getValue(variable);
			if (value != null) {
				Preconditions.checkArgument(problem.getVariables().contains(variable),
						"Solution contains a variable that is not in the problem: " + variable + ".");
				primalValuesB.put(variable, value);
			}
		}
		for (Constraint constraint : solution.getConstraints()) {
			final Double value = solution.getDualValue(constraint);
			if (value != null) {
				Preconditions.checkArgument(problem.getConstraints().contains(constraint));
				dualValuesB.put(constraint, value);
			}
		}
		objectiveValue = solution.getObjectiveValue();
		this.primalValues = primalValuesB.build();
		this.dualValues = dualValuesB.build();
		if (protectProblem) {
			this.problem = ImmutableMP.copyOf(problem);
		} else {
			this.problem = problem;
		}
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
			if (!problem.getVariables().contains(variable)) {
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

	@Override
	public String toString() {
		return SolverUtils.getAsString(this);
	}

}
