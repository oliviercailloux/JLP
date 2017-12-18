package io.github.oliviercailloux.jlp.result;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.github.oliviercailloux.jlp.elements.LpConstraint;
import io.github.oliviercailloux.jlp.elements.LpLinear;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.problem.LpProblem;
import io.github.oliviercailloux.jlp.problem.LpProblems;
import io.github.oliviercailloux.jlp.utils.LpLinearUtils;
import io.github.oliviercailloux.jlp.utils.LpSolverUtils;

public class LpSolutionImmutable implements LpSolution {

	/**
	 * No <code>null</code> key or value.
	 */
	private final Map<LpConstraint, Number> m_dualValues;

	private final Number m_objectiveValue;

	private final ImmutableMap<Variable, Number> m_primalValues;

	private final LpProblem m_problem;

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
	public LpSolutionImmutable(LpProblem problem, LpSolutionAlone solution) {
		this(problem, solution, true);
	}

	/**
	 * Copy constructor by value.
	 *
	 * @param solution
	 *            not <code>null</code>.
	 */
	public LpSolutionImmutable(LpSolution solution) {
		this(solution.getProblem(), solution, false);
	}

	private LpSolutionImmutable(LpProblem problem, LpSolutionAlone solution, boolean protectProblem) {
		Preconditions.checkNotNull(solution);
		final Builder<Variable, Number> primalValues = ImmutableMap.builder();
		final Builder<LpConstraint, Number> dualValues = ImmutableMap.builder();

		for (Variable variable : solution.getVariables()) {
			final Number value = solution.getValue(variable);
			if (value != null) {
				Preconditions.checkArgument(problem.getVariables().contains(variable),
						"Solution contains a variable that is not in the problem: " + variable + ".");
				primalValues.put(variable, value);
			}
		}
		for (LpConstraint constraint : solution.getConstraints()) {
			final Number value = solution.getDualValue(constraint);
			if (value != null) {
				Preconditions.checkArgument(problem.getConstraints().contains(constraint));
				dualValues.put(constraint, value);
			}
		}
		m_objectiveValue = solution.getObjectiveValue();
		m_primalValues = primalValues.build();
		m_dualValues = dualValues.build();
		if (protectProblem) {
			m_problem = LpProblems.newImmutable(problem);
		} else {
			m_problem = problem;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LpSolution)) {
			return false;
		}

		LpSolution s2 = (LpSolution) obj;
		return LpSolverUtils.equivalent(this, s2);
	}

	@Override
	public boolean getBooleanValue(Variable variable) {
		Number number = m_primalValues.get(variable);
		if (number == null) {
			if (!m_problem.getVariables().contains(variable)) {
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
	public Number getComputedObjectiveValue() {
		final LpLinear objectiveFunction = m_problem.getObjective().getFunction();
		if (objectiveFunction == null) {
			return null;
		}
		return LpLinearUtils.evaluate(objectiveFunction, m_primalValues);
	}

	@Override
	public Set<LpConstraint> getConstraints() {
		return Collections.unmodifiableSet(m_dualValues.keySet());
	}

	@Override
	public Number getDualValue(LpConstraint constraint) {
		Preconditions.checkNotNull(constraint);
		return m_dualValues.get(constraint);
	}

	@Override
	public Number getObjectiveValue() {
		return m_objectiveValue;
	}

	@Override
	public LpProblem getProblem() {
		return m_problem;
	}

	@Override
	public Number getValue(Variable variable) {
		Preconditions.checkNotNull(variable);
		return m_primalValues.get(variable);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.unmodifiableSet(m_primalValues.keySet());
	}

	@Override
	public int hashCode() {
		return LpSolverUtils.getSolutionEquivalence().hash(this);
	}

	@Override
	public String toString() {
		return LpSolverUtils.getAsString(this);
	}

}
