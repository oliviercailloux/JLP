package io.github.oliviercailloux.jlp.parameters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.oliviercailloux.jlp.parameters.LpDoubleParameter;
import io.github.oliviercailloux.jlp.parameters.LpIntParameter;
import io.github.oliviercailloux.jlp.parameters.LpObjectParameter;
import io.github.oliviercailloux.jlp.parameters.LpParametersDefaultValues;
import io.github.oliviercailloux.jlp.parameters.LpStringParameter;

public class IlpParametersDefaultValuesTest {
	@Test
	public void testInit() throws Exception {
		assertTrue(LpParametersDefaultValues.getDefaultDoubleValues().size() == LpDoubleParameter.values().length);
		assertTrue(LpParametersDefaultValues.getDefaultIntValues().size() == LpIntParameter.values().length);
		assertTrue(LpParametersDefaultValues.getDefaultStringValues().size() == LpStringParameter.values().length);
		assertTrue(LpParametersDefaultValues.getDefaultObjectValues().size() == LpObjectParameter.values().length);
	}
}
