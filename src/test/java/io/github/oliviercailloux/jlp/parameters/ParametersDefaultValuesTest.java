package io.github.oliviercailloux.jlp.parameters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
