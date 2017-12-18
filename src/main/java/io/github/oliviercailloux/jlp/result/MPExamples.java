package io.github.oliviercailloux.jlp.result;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.NamedVariable;
import io.github.oliviercailloux.jlp.elements.OptimizationDirection;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsImmutable;
import io.github.oliviercailloux.jlp.elements.SumTermsImpl;
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
		final NamedVariable x = NamedVariable.newInt("x");
		final NamedVariable y = NamedVariable.newInt("y");
		problem.addVariable(x);
		problem.addVariable(y);

		SumTerms obj = new SumTermsImpl();
		obj.addTerm(143, x);
		obj.addTerm(60, y);
		problem.setObjective(obj, OptimizationDirection.MAX);

		SumTerms c1 = new SumTermsImpl();
		c1.addTerm(120, x);
		c1.addTerm(210, y);
		problem.add("c1", c1, ComparisonOperator.LE, 15000);

		SumTerms c2 = new SumTermsImpl();
		c2.addTerm(110, x);
		c2.addTerm(30, y);
		problem.add("c2", c2, ComparisonOperator.LE, 4000);

		SumTerms c3 = new SumTermsImpl();
		c3.addTerm(1, x);
		c3.addTerm(1, y);
		problem.add("c3", c3, ComparisonOperator.LE, 75);

		return problem;
	}

	public static MP getIntOneFourThreeLowX() {
		final MP problem = getIntOneFourThree();
		final NamedVariable x = NamedVariable.newInt("x");
		problem.add("low x", SumTermsImmutable.of(1, x), ComparisonOperator.LE, 16d);
		return problem;
	}

	/**
	 * Retrieves the optimal solution of the problem.
	 *
	 * @return the solution.
	 */
	static public Solution getIntOneFourThreeLowXSolution() {
		final SolutionImpl solution = new SolutionImpl(getIntOneFourThreeLowX());
		final NamedVariable x = NamedVariable.newInt("x");
		final NamedVariable y = NamedVariable.newInt("y");
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
		final NamedVariable x = NamedVariable.newInt("x");
		final NamedVariable y = NamedVariable.newInt("y");
		solution.setObjectiveValue(Integer.valueOf(6266));
		solution.putValue(x, Integer.valueOf(22));
		solution.putValue(y, Integer.valueOf(52));
		assert (solution.getComputedObjectiveValue().doubleValue() == solution.getObjectiveValue().doubleValue());
		return solution;
	}
}
