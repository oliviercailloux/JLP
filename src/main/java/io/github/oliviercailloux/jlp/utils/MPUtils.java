package io.github.oliviercailloux.jlp.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.Sense;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.problem.IMP;
import io.github.oliviercailloux.jlp.result.Solution;

public class MPUtils {
	private static final Logger s_logger = LoggerFactory.getLogger(MPUtils.class);

	/**
	 * Retrieves a long description, with line breaks, of the given problem.
	 *
	 * @param problem
	 *            not <code>null</code>.
	 * @return not <code>null</code>, not empty.
	 */
	static public String getLongDescription(IMP problem) {
		Preconditions.checkNotNull(problem);
		String N = System.getProperty("line.separator");
		final String name = problem.getName().equals("") ? "" : " " + problem.getName();
		String s = "Problem" + name + N;

		if (!problem.getObjective().isZero()) {
			s += problem.getObjective().getSense() + N;
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
			final double lb = variable.getBounds().lowerEndpoint();
			final double ub = variable.getBounds().upperEndpoint();

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
			s += "\t" + variable + " " + variable.getDomain() + N;
		}

		return s;

	}

	/**
	 * <p>
	 * Retrieves the value of the given variable in the given solution, as a double
	 * value. If the variable is not defined in the solution, an exception is
	 * thrown.
	 * </p>
	 * <p>
	 * Using this method is equivalent to invoking {@link Solution#getValue(Object)}
	 * then {@link Number#doubleValue()} , except that this method produces a
	 * clearer exception message when the variable is not in the solution.
	 * </p>
	 *
	 * @param solution
	 *            not <code>null</code>.
	 * @param variable
	 *            not <code>null</code>.
	 * @return the value of the variable.
	 */
	static public double getSolutionValue(Solution solution, Variable variable) {
		final Number solutionValue = solution.getValue(variable);
		if (solutionValue == null) {
			throw new IllegalStateException("Solution value for " + variable + " not found.");
		}
		return solutionValue.doubleValue();
	}

	static public void logSolutionValues(Solution solution) {
		checkNotNull(solution);
		final Set<Variable> variables = solution.getVariables();
		for (Variable variable : variables) {
			final Number value = solution.getValue(variable);
			s_logger.info("Variable {} has value {}.", variable, value);
		}
		s_logger.info("Objective value: {}.", solution.getObjectiveValue());
	}

	static public Objective newWithDirection(Objective source, Sense newDirection) {
		checkNotNull(source);
		checkNotNull(newDirection);
		final SumTerms sourceFunction = source.getFunction();
		checkNotNull(sourceFunction);
		final Sense sourceDirection = source.getSense();
		checkNotNull(sourceDirection);
		if (sourceDirection.equals(newDirection)) {
			return source;
		}
		return Objective.of(SumTermUtils.newMult(-1d, sourceFunction), newDirection);
	}

}
