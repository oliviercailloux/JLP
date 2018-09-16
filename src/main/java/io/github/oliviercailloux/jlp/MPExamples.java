package io.github.oliviercailloux.jlp;

import java.util.Map;

import com.google.common.collect.Maps;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.mp.MP;
import io.github.oliviercailloux.jlp.mp.MPBuilder;
import io.github.oliviercailloux.jlp.result.Solution;

public class MPExamples {
	private MPExamples() {
		/** Non instantiable. */
	}

	/**
	 * <p>
	 * Builds a new problem with integer variables:
	 * <ul>
	 * <li>Maximize: 143x+60y Subject to:</li>
	 * <li>120x+210y <= 15000</li>
	 * <li>110x+30y <= 4000</li>
	 * <li>x+y <= 75</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The problem is named after the coefficient of the x variable in the objective
	 * function.
	 * </p>
	 *
	 * @return a new problem.
	 */
	public static MPBuilder getIntOneFourThree() {
		MPBuilder problem = MP.builder();
		problem.setName("OneFourThree");
		final Variable x = Variable.integer("x");
		final Variable y = Variable.integer("y");
		problem.getVariables().add(x);
		problem.getVariables().add(y);

		problem.setObjective(Objective.max(SumTerms.of(143, x, 60, y)));
		problem.getConstraints().add(Constraint.of("c1", SumTerms.of(120, x, 210, y), ComparisonOperator.LE, 15000));
		problem.getConstraints().add(Constraint.of("c2", SumTerms.of(110, x, 30, y), ComparisonOperator.LE, 4000));
		problem.getConstraints().add(Constraint.of("c3", SumTerms.of(1, x, 1, y), ComparisonOperator.LE, 75));

		return problem;
	}

	/**
	 * @return a problem as in {@link #getIntOneFourThree()} with a supplementary
	 *         constraint: x ≤ 16.
	 */
	public static MPBuilder getIntOneFourThreeLowX() {
		final MPBuilder problem = getIntOneFourThree();
		final Variable x = problem.getVariable("x").get();
		problem.getConstraints().add(Constraint.of("low x", SumTerms.of(1, x), ComparisonOperator.LE, 16d));
		return problem;
	}

	/**
	 * Retrieves the optimal solution of the problem.
	 *
	 * @return the solution.
	 */
	public static Solution getIntOneFourThreeSolution() {
		final MPBuilder mp = getIntOneFourThree();
		final Variable x = mp.getVariable("x").get();
		final Variable y = mp.getVariable("y").get();

		final Map<Variable, Double> values = Maps.newLinkedHashMap();
		final double obj = 6266d;
		values.put(x, 22d);
		values.put(y, 52d);
		return Solution.of(mp, obj, values);
	}

	/**
	 * Retrieves the optimal solution of the problem.
	 *
	 * @return the solution.
	 */
	public static Solution getIntOneFourThreeLowXSolution() {
		final MPBuilder mp = getIntOneFourThreeLowX();
		final Variable x = mp.getVariable("x").get();
		final Variable y = mp.getVariable("y").get();
	
		final Map<Variable, Double> values = Maps.newLinkedHashMap();
		final double obj = 5828d;
		values.put(x, 16d);
		values.put(y, 59d);
		return Solution.of(mp, obj, values);
	}
}
