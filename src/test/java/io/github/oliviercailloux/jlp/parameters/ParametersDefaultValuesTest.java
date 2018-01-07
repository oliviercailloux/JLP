package io.github.oliviercailloux.jlp.parameters;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.oliviercailloux.jlp.result.parameters.SolverParameterDouble;
import io.github.oliviercailloux.jlp.result.parameters.SolverParameterInt;
import io.github.oliviercailloux.jlp.result.parameters.SolverParameterString;
import io.github.oliviercailloux.jlp.result.parameters.SolverParametersDefaultValues;

public class ParametersDefaultValuesTest {
	@Test
	public void testInit() throws Exception {
		assertTrue(
				SolverParametersDefaultValues.getDefaultDoubleValues().size() == SolverParameterDouble.values().length);
		assertTrue(SolverParametersDefaultValues.getDefaultIntValues().size() == SolverParameterInt.values().length);
		assertTrue(
				SolverParametersDefaultValues.getDefaultStringValues().size() == SolverParameterString.values().length);
	}
}
