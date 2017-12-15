package io.github.oliviercailloux.jlp.result;

import io.github.oliviercailloux.jlp.LpDirection;
import io.github.oliviercailloux.jlp.LpLinear;
import io.github.oliviercailloux.jlp.LpLinearImpl;
import io.github.oliviercailloux.jlp.LpOperator;
import io.github.oliviercailloux.jlp.problem.LpProblem;
import io.github.oliviercailloux.jlp.problem.LpProblems;
import io.github.oliviercailloux.jlp.problem.LpVariableType;

public class LpProblemExamples {
	/**
	 * Builds a new problem with integer variables:
	 * <ul>
	 * <li>Maximize: 143x+60y Subject to:</li>
	 * <li>120x+210y <= 15000</li>
	 * <li>110x+30y <= 4000</li>
	 * <li>x+y <= 75</li>
	 * </ul>
	 * 
	 * The problem is named after the coefficient of the x variable in the objective
	 * function.
	 * 
	 * @return a new problem.
	 */
	static public LpProblem<String> getIntOneFourThree() {
		LpProblem<String> problem = LpProblems.newProblem();
		problem.setName("OneFourThree");
		problem.setVariableType("x", LpVariableType.INT);
		problem.setVariableType("y", LpVariableType.INT);

		LpLinear<String> obj = new LpLinearImpl<String>();
		obj.addTerm(143, "x");
		obj.addTerm(60, "y");
		problem.setObjective(obj, LpDirection.MAX);

		LpLinear<String> c1 = new LpLinearImpl<String>();
		c1.addTerm(120, "x");
		c1.addTerm(210, "y");
		problem.add("c1", c1, LpOperator.LE, 15000);

		LpLinear<String> c2 = new LpLinearImpl<String>();
		c2.addTerm(110, "x");
		c2.addTerm(30, "y");
		problem.add("c2", c2, LpOperator.LE, 4000);

		LpLinear<String> c3 = new LpLinearImpl<String>();
		c3.addTerm(1, "x");
		c3.addTerm(1, "y");
		problem.add("c3", c3, LpOperator.LE, 75);

		return problem;
	}

	public static LpProblem<String> getIntOneFourThreeLowX() {
		final LpProblem<String> problem = getIntOneFourThree();
		problem.setVariableBounds("x", null, Double.valueOf(16d));
		return problem;
	}

	/**
	 * Retrieves the optimal solution of the problem.
	 * 
	 * @return the solution.
	 */
	static public LpSolution<String> getIntOneFourThreeLowXSolution() {
		final LpSolutionImpl<String> solution = new LpSolutionImpl<String>(getIntOneFourThreeLowX());
		solution.setObjectiveValue(Integer.valueOf(5828));
		solution.putValue("x", Integer.valueOf(16));
		solution.putValue("y", Integer.valueOf(59));
		assert (solution.getComputedObjectiveValue().doubleValue() == solution.getObjectiveValue().doubleValue());
		return solution;
	}

	/**
	 * Retrieves the optimal solution of the problem.
	 * 
	 * @return the solution.
	 */
	static public LpSolution<String> getIntOneFourThreeSolution() {
		final LpSolutionImpl<String> solution = new LpSolutionImpl<String>(getIntOneFourThree());
		solution.setObjectiveValue(Integer.valueOf(6266));
		solution.putValue("x", Integer.valueOf(22));
		solution.putValue("y", Integer.valueOf(52));
		assert (solution.getComputedObjectiveValue().doubleValue() == solution.getObjectiveValue().doubleValue());
		return solution;
	}
}
