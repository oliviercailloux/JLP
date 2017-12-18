package io.github.oliviercailloux.jlp.problem;

import static com.google.common.base.Preconditions.checkNotNull;

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
	 * @param source
	 *            not <code>null</code>.
	 * @param target
	 *            not <code>null</code>.
	 */
	static public void copyTo(IMP source, MP target) {
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
		for (Constraint constraint : source.getConstraints()) {
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
		return new MP(source);
	}
}
