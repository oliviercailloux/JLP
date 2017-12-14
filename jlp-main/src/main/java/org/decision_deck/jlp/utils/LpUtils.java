package org.decision_deck.jlp.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpDirection;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpObjective;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.result.LpSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LpUtils {
    private static final Logger s_logger = LoggerFactory.getLogger(LpUtils.class);

    static public <V> void logProblemContents(LpProblem<V> problem) {
	checkNotNull(problem);
	s_logger.info("Problem {}, {}.", problem.getName(), problem.getDimension());
	final Set<V> variables = problem.getVariables();
	for (V variable : variables) {
	    s_logger.info("Variable {} in problem: name {}, type " + problem.getVariableType(variable) + ", bounds "
			    + problem.getVariableLowerBound(variable) + " to "
			    + problem.getVariableUpperBound(variable)
		    + ".", variable,
		    problem.getVariableName(variable));
	}
	final Set<LpConstraint<V>> constraints = problem.getConstraints();
	for (LpConstraint<V> constraint : constraints) {
	    s_logger.info("Constraint {}.", constraint);
	}
	s_logger.info("Objective: {}.", problem.getObjective());
    }

    static public <V> LpObjective<V> newWithDirection(LpObjective<V> source, LpDirection newDirection) {
	checkNotNull(source);
	checkNotNull(newDirection);
	final LpLinear<V> sourceFunction = source.getFunction();
	checkNotNull(sourceFunction);
	final LpDirection sourceDirection = source.getDirection();
	checkNotNull(sourceDirection);
	if (sourceDirection.equals(newDirection)) {
	    return source;
	}
	return new LpObjective<V>(LpLinearUtils.newMult(-1d, sourceFunction), newDirection);
    }

    static public <V> void logSolutionValues(LpSolution<V> solution) {
	checkNotNull(solution);
	final Set<V> variables = solution.getVariables();
	for (V variable : variables) {
	    final Number value = solution.getValue(variable);
	    s_logger.info("Variable {} has value {}.", variable, value);
	}
	s_logger.info("Objective value: {}.", solution.getObjectiveValue());
    }

    /**
     * <p>
     * Retrieves the value of the given variable in the given solution, as a double value. If the variable is not
     * defined in the solution, an exception is thrown.
     * </p>
     * <p>
     * Using this method is equivalent to invoking {@link LpSolution#getValue(Object)} then {@link Number#doubleValue()}
     * , except that this method produces a clearer exception message when the variable is not in the solution.
     * </p>
     * 
     * @param solution
     *            not <code>null</code>.
     * @param variable
     *            not <code>null</code>.
     * @return the value of the variable.
     */
    static public <V> double getSolutionValue(LpSolution<V> solution, V variable) {
	final Number solutionValue = solution.getValue(variable);
	if (solutionValue == null) {
	    throw new IllegalStateException("Solution value for " + variable + " not found.");
	}
	return solutionValue.doubleValue();
    }

}
