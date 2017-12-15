package io.github.oliviercailloux.jlp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

import io.github.oliviercailloux.jlp.LpConstraint;
import io.github.oliviercailloux.jlp.LpOperator;

/**
 * Utilities methods (e.g. views, copies) related to a {@link LpProblem}.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpProblems {
	/**
	 * The default constraints namer used by {@link LpProblem} instances. It uses
	 * the constraint id transformed to string as per {@link LpConstraint#getId()}
	 * and {@link #toString()}, and the empty string if the constraint has a
	 * <code>null</code> id.
	 * 
	 * @author Olivier Cailloux
	 * 
	 * @param <V>
	 *            the type of the variables.
	 */
	static public class DefaultConstraintsNamer<V> implements Function<LpConstraint<V>, String> {
		public DefaultConstraintsNamer() {
			/** Public default constructor. */
		}

		@Override
		public String apply(LpConstraint<V> input) {
			return input.getIdAsString();
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof DefaultConstraintsNamer<?>;
		}

		@Override
		public String toString() {
			return "Constraint namer from internal constraint name";
		}

		@Override
		public int hashCode() {
			return 1555;
		}
	}

	static public class GetVariableType<T> implements Function<T, LpVariableType> {
		private final LpProblem<T> m_problem;

		public GetVariableType(LpProblem<T> problem) {
			checkNotNull(problem);
			m_problem = problem;
		}

		@Override
		public LpVariableType apply(T variable) {
			return m_problem.getVariableType(variable);
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
		for (T variable : problem.getVariables()) {
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
		for (T variable : problem.getVariables()) {
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
		return new LpProblemReadView<V>(delegate);
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
		return new LpProblemImmutable<V>(source);
	}

	/**
	 * Retrieves a live read-only view of all the variables in the given problem
	 * that have the given type.
	 * 
	 * @param <T>
	 *            the class of the variables.
	 * @param problem
	 *            not <code>null</code>.
	 * @param type
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <T> Set<T> getVariables(LpProblem<T> problem, LpVariableType type) {
		Preconditions.checkNotNull(problem);
		Preconditions.checkNotNull(type);
		return Collections.unmodifiableSet(Sets.filter(problem.getVariables(),
				Predicates.compose(Predicates.equalTo(type), new GetVariableType<T>(problem))));
	}

	/**
	 * <p>
	 * Restricts the bounds associated with the given variable in the given problem
	 * to make sure the variable satisfies the given constraint. For example, if the
	 * operator is less or equal to and the value is three, the variable will have
	 * its upper bound set to three if the current upper bound set in the problem
	 * for that variable is greater than three. Note that this method only possibly
	 * <em>restrict</em> the problem: if the given parameters do not constrain the
	 * variable further than it is constrained already considering its existing
	 * bounds, the problem is <em>not</em> modified.
	 * </p>
	 * <p>
	 * The resulting bounds must define a non empty interval: it is not allowed, for
	 * example, to restrict the upper bound to a lower value than the current lower
	 * bound. In such a case the problem is not modified and this method throws an
	 * exception. Defining such contradictory constraints (and, thus, defining a
	 * problem with no satisfactory solution) is permitted through addition of
	 * constraints but not through restriction of bounds.
	 * </p>
	 * <p>
	 * Assuming the resulting restricted interval is not empty, restricting the
	 * bounds with this method has the same effect on the set of admitted solutions
	 * than adding an equivalent constraint. In the previous example, instead of
	 * restricting the bound, a constraint could have been added specifying that the
	 * variable must be less than or equal to three. Restricting the bounds instead
	 * of adding constraints may enhance readability.
	 * </p>
	 * <p>
	 * This method may be used to transform a variable into a constant without
	 * changing the structure of the problem. For this, use the operator
	 * {@link LpOperator#EQ}.
	 * </p>
	 * <p>
	 * This method uses a double value instead of a Number because it must compare
	 * the given value to the existing bound and use the most constraining one, thus
	 * it would have to somehow convert the given number to a double value anyway.
	 * To make sure the bound of a variable uses a precise Number subclass, better
	 * use the method {@link LpProblem#setVariableBounds}. This method uses the
	 * method {@link Number#doubleValue()} to compare the possibly existing bound to
	 * the given value.
	 * </p>
	 * 
	 * @param <V>
	 *            the type of the variables in the problem.
	 * @param problem
	 *            not <code>null</code>.
	 * @param variable
	 *            not <code>null</code>, must be a variable of the given problem.
	 * @param op
	 *            not <code>null</code>.
	 * @param value
	 *            a real number.
	 * @return <code>true</code> iff the problem has been modified.
	 */
	static public <V> boolean restrictBounds(LpProblem<V> problem, V variable, LpOperator op, double value) {
		checkNotNull(problem);
		checkNotNull(variable);
		checkArgument(problem.getVariables().contains(variable));
		checkNotNull(op);
		checkArgument(!Double.isInfinite(value));
		checkArgument(!Double.isNaN(value));

		final Double newLower;
		final Double newUpper;
		switch (op) {
		case EQ:
			newLower = Double.valueOf(value);
			newUpper = Double.valueOf(value);
			break;
		case GE:
			newLower = Double.valueOf(value);
			newUpper = null;
			break;
		case LE:
			newLower = null;
			newUpper = Double.valueOf(value);
			break;
		default:
			throw new IllegalStateException("Unknown OP.");
		}

		final Number currentLower = problem.getVariableLowerBound(variable);
		final Number currentUpper = problem.getVariableUpperBound(variable);
		final Number effectiveLower;
		final Number effectiveUpper;
		if (newLower != null && newLower.doubleValue() > currentLower.doubleValue()) {
			effectiveLower = newLower;
		} else {
			effectiveLower = currentLower;
		}
		if (newUpper != null && newUpper.doubleValue() < currentUpper.doubleValue()) {
			effectiveUpper = newUpper;
		} else {
			effectiveUpper = currentUpper;
		}
		assert effectiveLower != null;
		assert effectiveUpper != null;
		if (newLower != null && newLower.doubleValue() > effectiveUpper.doubleValue()) {
			throw new IllegalStateException(
					"New lower bound of " + newLower + " contradicts upper bound of " + effectiveUpper + ".");
		}
		if (newUpper != null && newUpper.doubleValue() < effectiveLower.doubleValue()) {
			throw new IllegalStateException(
					"New upper bound of " + newUpper + " contradicts lower bound of " + effectiveLower + ".");
		}
		/**
		 * TODO clean this code, avoid calling set if nothing changed (risk of
		 * imprecision when converting to double).
		 */
		return problem.setVariableBounds(variable, effectiveLower, effectiveUpper);
	}

	/**
	 * Creates a new, empty problem.
	 * 
	 * @param <V>
	 *            the type of the variables.
	 * @return a new problem.
	 */
	static public <V> LpProblem<V> newProblem() {
		return new LpProblemImpl<V>();
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
		return new LpProblemImpl<V>(source);
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
		for (V variable : source.getVariables()) {
			target.setVariableType(variable, source.getVariableType(variable));
			target.setVariableBounds(variable, source.getVariableLowerBound(variable),
					source.getVariableUpperBound(variable));
		}
		target.setObjective(source.getObjective().getFunction(), source.getObjective().getDirection());
		for (LpConstraint<V> constraint : source.getConstraints()) {
			target.add(constraint);
		}
	}
}
