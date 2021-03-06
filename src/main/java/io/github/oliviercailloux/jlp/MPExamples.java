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
	 * Builds a new MP with integer variables:
	 * <ul>
	 * <li>Maximize: 143x+60y Subject to:</li>
	 * <li>120x+210y <= 15000</li>
	 * <li>110x+30y <= 4000</li>
	 * <li>x+y <= 75</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The MP is named after the coefficient of the x variable in the objective
	 * function.
	 * </p>
	 *
	 * @return a new MP.
	 */
	public static MPBuilder getIntOneFourThree() {
		MPBuilder mp = MP.builder();
		mp.setName("OneFourThree");
		final Variable x = Variable.integer("x");
		final Variable y = Variable.integer("y");
		mp.addVariable(x);
		mp.addVariable(y);

		mp.setObjective(Objective.max(SumTerms.of(143, x, 60, y)));
		mp.addConstraint(Constraint.LE(SumTerms.of(120, x, 210, y), 15000));
		mp.addConstraint(Constraint.LE(SumTerms.of(110, x, 30, y), 4000));
		mp.addConstraint(Constraint.LE(SumTerms.of(1, x, 1, y), 75));

		return mp;
	}

	/**
	 * @return an MP as in {@link #getIntOneFourThree()} with a supplementary
	 *         constraint: x ≤ 16.
	 */
	public static MPBuilder getIntOneFourThreeLowX() {
		final MPBuilder mp = getIntOneFourThree();
		final Variable x = mp.getVariable("x");
		mp.addConstraint(Constraint.of("low x", SumTerms.of(1, x), ComparisonOperator.LE, 16d));
		return mp;
	}

	/**
	 * Retrieves the optimal solution of the corresponding MP.
	 *
	 * @return the solution.
	 */
	public static Solution getIntOneFourThreeSolution() {
		final MPBuilder mp = getIntOneFourThree();
		final Variable x = mp.getVariable("x");
		final Variable y = mp.getVariable("y");

		final Map<Variable, Double> values = Maps.newLinkedHashMap();
		final double obj = 6266d;
		values.put(x, 22d);
		values.put(y, 52d);
		return Solution.of(mp, obj, values);
	}

	/**
	 * Retrieves the optimal solution of the corresponding mp.
	 *
	 * @return the solution.
	 */
	public static Solution getIntOneFourThreeLowXSolution() {
		final MPBuilder mp = getIntOneFourThreeLowX();
		final Variable x = mp.getVariable("x");
		final Variable y = mp.getVariable("y");

		final Map<Variable, Double> values = Maps.newLinkedHashMap();
		final double obj = 5828d;
		values.put(x, 16d);
		values.put(y, 59d);
		return Solution.of(mp, obj, values);
	}
}
