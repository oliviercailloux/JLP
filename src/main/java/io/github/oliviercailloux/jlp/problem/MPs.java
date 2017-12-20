package io.github.oliviercailloux.jlp.problem;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * Utilities methods (e.g. views, copies) related to a {@link IMP}.
 *
 * @author Olivier Cailloux
 *
 */
public class MPs {
	static public class DefaultConstraintsNamer implements Function<Constraint, String> {
		public DefaultConstraintsNamer() {
			/** Public default constructor. */
		}

		@Override
		public String apply(Constraint input) {
			return input.toString();
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof DefaultConstraintsNamer;
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
	 * Retrieves a long description, with line breaks, of the given problem.
	 *
	 * @param <T>
	 *            the type of the variables in the problem.
	 * @param problem
	 *            not <code>null</code>.
	 * @return not <code>null</code>, not empty.
	 */
	static public String getLongDescription(IMP problem) {
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
		for (Constraint constraint : problem.getConstraints()) {
			s += "\t" + constraint + N;
		}
		s += "Bounds" + N;
		for (Variable variable : problem.getVariables()) {
			final double lb = variable.getLowerBound();
			final double ub = variable.getUpperBound();

			if (lb != Double.NEGATIVE_INFINITY || ub != Double.POSITIVE_INFINITY) {
				s += "\t";
				if (lb != Double.NEGATIVE_INFINITY) {
					s += lb + " <= ";
				}
				s += variable;
				if (ub != Double.POSITIVE_INFINITY) {
					s += " <= " + ub;
				}
				s += N;
			}
		}

		s += "Variables" + N;
		for (Variable variable : problem.getVariables()) {
			s += "\t" + variable + " " + variable.getType() + N;
		}

		return s;

	}

	/**
	 * Creates a new, empty problem.
	 *
	 * @return a new problem.
	 */
	static public MP newProblem() {
		return new MP();
	}

	/**
	 * Returns a mutable problem containing the same information as the source
	 * problem. The returned problem does not visibly share any information with the
	 * source object: modifications in the source problem are not reflected in the
	 * returned problem, and conversely.
	 *
	 * @param source
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public MP newProblem(IMP source) {
		return MP.copyOf(source);
	}
}
