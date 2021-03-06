package io.github.oliviercailloux.jlp.export;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.jlp.elements.RangeOfDouble;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.result.Solution;

public class Exporter {
	/**
	 * Returns a reasonably compact description of the given solution, useful for
	 * manual inspection. All variables are printed, with their defined range and
	 * description, and with their value given by this solution; and the objective
	 * value is printed as well. The lines are '\n' separated, and the last
	 * character is not a '\n'.
	 */
	public static String solutionToString(Solution solution) {
		final ImmutableMap<Variable, Double> variableValues = solution.getVariableValues();
		final Stream<String> strings = variableValues.entrySet().stream().map((e) -> format(e));
		final String values = strings.collect(Collectors.joining("\n"));
		return String.format("%s\nObjective value: %s", values, solution.getObjectiveValue());
	}

	private static String format(Entry<Variable, Double> e) {
		final Variable variable = e.getKey();
		final String str;
		switch (variable.getKind()) {
		case BOOL_KIND:
			str = String.format("%s BOOL: %g", variable.getDescription(), e.getValue());
			break;
		case INT_KIND:
			str = String.format("%s ∈ %s ∩ ℕ: %g", variable.getDescription(),
					RangeOfDouble.toString(variable.getBounds()), e.getValue());
			break;
		case REAL_KIND:
			str = String.format("%s ∈ %s: %g", variable.getDescription(), RangeOfDouble.toString(variable.getBounds()),
					e.getValue());
			break;
		default:
			throw new AssertionError();
		}
		return str;
	}
}
