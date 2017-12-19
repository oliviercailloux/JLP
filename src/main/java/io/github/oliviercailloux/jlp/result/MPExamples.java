package io.github.oliviercailloux.jlp.result;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.OptimizationDirection;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.problem.MP;
import io.github.oliviercailloux.jlp.problem.MPs;

public class MPExamples {
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
	static public MP getIntOneFourThree() {
		MP problem = MPs.newProblem();
		problem.setName("OneFourThree");
		final Variable x = Variable.newInt("x");
		final Variable y = Variable.newInt("y");
		problem.addVariable(x);
		problem.addVariable(y);

		problem.setObjective(SumTerms.of(143, x, 60, y), OptimizationDirection.MAX);
		problem.add("c1", SumTerms.of(120, x, 210, y), ComparisonOperator.LE, 15000);
		problem.add("c2", SumTerms.of(110, x, 30, y), ComparisonOperator.LE, 4000);
		problem.add("c3", SumTerms.of(1, x, 1, y), ComparisonOperator.LE, 75);

		return problem;
	}

	public static MP getIntOneFourThreeLowX() {
		final MP problem = getIntOneFourThree();
		final Variable x = Variable.newInt("x");
		problem.add("low x", SumTerms.of(1, x), ComparisonOperator.LE, 16d);
		return problem;
	}

	/**
	 * Retrieves the optimal solution of the problem.
	 *
	 * @return the solution.
	 */
	static public Solution getIntOneFourThreeLowXSolution() {
		final SolutionImpl solution = new SolutionImpl(getIntOneFourThreeLowX());
		final Variable x = Variable.newInt("x");
		final Variable y = Variable.newInt("y");
		solution.setObjectiveValue(Integer.valueOf(5828));
		solution.putValue(x, Integer.valueOf(16));
		solution.putValue(y, Integer.valueOf(59));
		assert (solution.getComputedObjectiveValue().doubleValue() == solution.getObjectiveValue().doubleValue());
		return solution;
	}

	/**
	 * Retrieves the optimal solution of the problem.
	 *
	 * @return the solution.
	 */
	static public Solution getIntOneFourThreeSolution() {
		final SolutionImpl solution = new SolutionImpl(getIntOneFourThree());
		final Variable x = Variable.newInt("x");
		final Variable y = Variable.newInt("y");
		solution.setObjectiveValue(Integer.valueOf(6266));
		solution.putValue(x, Integer.valueOf(22));
		solution.putValue(y, Integer.valueOf(52));
		assert (solution.getComputedObjectiveValue().doubleValue() == solution.getObjectiveValue().doubleValue());
		return solution;
	}
}
