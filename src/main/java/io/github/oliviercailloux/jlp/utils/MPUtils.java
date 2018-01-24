package io.github.oliviercailloux.jlp.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.Sense;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.mp.IMP;
import io.github.oliviercailloux.jlp.result.Solution;

public class MPUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(MPUtils.class);

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

	static public void logSolutionValues(Solution solution) {
		checkNotNull(solution);
		LOGGER.info("Solution to {}.", solution.getMP());
		final ImmutableList<Variable> variables = solution.getVariables();
		for (Variable variable : variables) {
			final Number value = solution.getValue(variable);
			LOGGER.info("Variable {} has value {}.", variable, value);
		}
		LOGGER.info("Objective value: {}.", solution.getObjectiveValue());
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
