package io.github.oliviercailloux.jlp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

import io.github.oliviercailloux.jlp.elements.LpConstraint;
import io.github.oliviercailloux.jlp.elements.LpOperator;
import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * Utilities methods (e.g. views, copies) related to a {@link LpProblem}.
 *
 * @author Olivier Cailloux
 *
 */
public class LpProblems {
	static public class DefaultConstraintsNamer<V> implements Function<LpConstraint<V>, String> {
		public DefaultConstraintsNamer() {
			/** Public default constructor. */
		}

		@Override
		public String apply(LpConstraint<V> input) {
			return input.toString();
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof DefaultConstraintsNamer<?>;
		}

		@Override
		public int hashCode() {
			return 1555;
		}

		@Override
		public String toString() {
			return "Constraint namer from internal constraint name";
		}
	}

	/**
	 * <p>
	 * Completely erase the target data and replace it with the data in the source
	 * problem. The order of the variables and constraints in target is set to be
	 * the same as the order of the source, thus the variables and constraints sets
	 * iteration order of the target will be the same as the sets iteration order of
	 * the source. When this method returns the target is identical to the source.
	 * </p>
	 * <p>
	 * After this method returns, the target problem uses <em>the same</em> namer
	 * functions for variable and constraint names as the source problem.
	 * </p>
	 *
	 * @param <V>
	 *            the type of the variables objects.
	 *
	 * @param source
	 *            not <code>null</code>.
	 * @param target
	 *            not <code>null</code>.
	 */
	static public <V> void copyTo(LpProblem<V> source, LpProblem<V> target) {
		checkNotNull(target);
		checkNotNull(source);

		target.clear();
		target.setName(source.getName());
		target.setVariablesNamer(source.getVariablesNamer());
		target.setConstraintsNamer(source.getConstraintsNamer());
		for (Variable variable : source.getVariables()) {
			target.setVariableType(variable, source.getVariableType(variable));
			target.setVariableBounds(variable, source.getVariableLowerBound(variable),
					source.getVariableUpperBound(variable));
		}
		target.setObjective(source.getObjective().getFunction(), source.getObjective().getDirection());
		for (LpConstraint<V> constraint : source.getConstraints()) {
			target.add(constraint);
		}
	}

	/**
	 * Retrieves a long description, with line breaks, of the given problem.
	 *
	 * @param <T>
	 *            the type of the variables in the problem.
	 * @param problem
	 *            not <code>null</code>.
	 * @return not <code>null</code>, not empty.
	 */
	static public <T> String getLongDescription(LpProblem<T> problem) {
		Preconditions.checkNotNull(problem);
		String N = System.getProperty("line.separator");
		final String name = problem.getName().equals("") ? "" : " " + problem.getName();
		String s = "Problem" + name + N;

		if (!problem.getObjective().isEmpty()) {
			s += problem.getObjective().getDirection() + N;
			s += " " + problem.getObjective().getFunction() + N;
		} else {
			s += "Find one solution" + N;
		}
		s += "Subject To" + N;
		for (LpConstraint<T> constraint : problem.getConstraints()) {
			s += "\t" + constraint + N;
		}
		s += "Bounds" + N;
		for (Variable variable : problem.getVariables()) {
			final Number lb = problem.getVariableLowerBound(variable);
			final Number ub = problem.getVariableUpperBound(variable);

			if (lb.doubleValue() != Double.NEGATIVE_INFINITY || ub.doubleValue() != Double.POSITIVE_INFINITY) {
				s += "\t";
				if (lb.doubleValue() != Double.NEGATIVE_INFINITY) {
					s += lb + " <= ";
				}
				s += variable;
				if (ub.doubleValue() != Double.POSITIVE_INFINITY) {
					s += " <= " + ub;
				}
				s += N;
			}
		}

		s += "Variables" + N;
		for (Variable variable : problem.getVariables()) {
			s += "\t" + variable + " " + problem.getVariableType(variable) + N;
		}

		return s;

	}

	/**
	 * Retrieves a read-only view of the given delegate problem.
	 *
	 * @param <V>
	 *            the type of the variables.
	 * @param delegate
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <V> LpProblemReadView<V> getReadView(LpProblem<V> delegate) {
		if (delegate instanceof LpProblemReadView<?>) {
			return (LpProblemReadView<V>) delegate;
		}
		return new LpProblemReadView<>(delegate);
	}

	/**
	 * Returns a problem containing the same information as the source problem, and
	 * which is immutable.
	 *
	 * @param <V>
	 *            the type of variables.
	 * @param source
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <V> LpProblemImmutable<V> newImmutable(LpProblem<V> source) {
		if (source instanceof LpProblemImmutable<?>) {
			return (LpProblemImmutable<V>) source;
		}
		return new LpProblemImmutable<>(source);
	}

	/**
	 * Creates a new, empty problem.
	 *
	 * @param <V>
	 *            the type of the variables.
	 * @return a new problem.
	 */
	static public <V> LpProblem<V> newProblem() {
		return new LpProblemImpl<>();
	}

	/**
	 * Returns a mutable problem containing the same information as the source
	 * problem. The returned problem does not visibly share any information with the
	 * source object: modifications in the source problem are not reflected in the
	 * returned problem, and conversely.
	 *
	 * @param <V>
	 *            the type of variables.
	 * @param source
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <V> LpProblem<V> newProblem(LpProblem<V> source) {
		return new LpProblemImpl<>(source);
	}
}
