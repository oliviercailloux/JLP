package io.github.oliviercailloux.jlp.parameters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.oliviercailloux.jlp.parameters.SolverParameterDouble;
import io.github.oliviercailloux.jlp.parameters.SolverParameterInt;
import io.github.oliviercailloux.jlp.parameters.SolverParameterObject;
import io.github.oliviercailloux.jlp.parameters.SolverParametersDefaultValues;
import io.github.oliviercailloux.jlp.parameters.SolverParameterString;

public class IlpParametersDefaultValuesTest {
	@Test
	public void testInit() throws Exception {
		assertTrue(SolverParametersDefaultValues.getDefaultDoubleValues().size() == SolverParameterDouble.values().length);
		assertTrue(SolverParametersDefaultValues.getDefaultIntValues().size() == SolverParameterInt.values().length);
		assertTrue(SolverParametersDefaultValues.getDefaultStringValues().size() == SolverParameterString.values().length);
		assertTrue(SolverParametersDefaultValues.getDefaultObjectValues().size() == SolverParameterObject.values().length);
	}
}
