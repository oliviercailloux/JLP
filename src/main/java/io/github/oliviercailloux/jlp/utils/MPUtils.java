package io.github.oliviercailloux.jlp.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.OptimizationDirection;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.ObjectiveFunction;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.problem.IMP;
import io.github.oliviercailloux.jlp.result.Solution;

public class MPUtils {
	private static final Logger s_logger = LoggerFactory.getLogger(MPUtils.class);

	/**
	 * <p>
	 * Retrieves the value of the given variable in the given solution, as a double
	 * value. If the variable is not defined in the solution, an exception is
	 * thrown.
	 * </p>
	 * <p>
	 * Using this method is equivalent to invoking
	 * {@link Solution#getValue(Object)} then {@link Number#doubleValue()} ,
	 * except that this method produces a clearer exception message when the
	 * variable is not in the solution.
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

	static public <V> void logProblemContents(IMP problem) {
		checkNotNull(problem);
		s_logger.info("Problem {}, {}.", problem.getName(), problem.getDimension());
		final Set<Variable> variables = problem.getVariables();
		for (Variable variable : variables) {
			s_logger.info("Variable {} in problem: name {}, type " + problem.getVariableType(variable) + ", bounds "
					+ problem.getVariableLowerBound(variable) + " to " + problem.getVariableUpperBound(variable) + ".",
					variable, problem.getVariableName(variable));
		}
		final Set<Constraint> constraints = problem.getConstraints();
		for (Constraint constraint : constraints) {
			s_logger.info("Constraint {}.", constraint);
		}
		s_logger.info("Objective: {}.", problem.getObjective());
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

	static public ObjectiveFunction newWithDirection(ObjectiveFunction source, OptimizationDirection newDirection) {
		checkNotNull(source);
		checkNotNull(newDirection);
		final SumTerms sourceFunction = source.getFunction();
		checkNotNull(sourceFunction);
		final OptimizationDirection sourceDirection = source.getDirection();
		checkNotNull(sourceDirection);
		if (sourceDirection.equals(newDirection)) {
			return source;
		}
		return new ObjectiveFunction(SumTermUtils.newMult(-1d, sourceFunction), newDirection);
	}

}
