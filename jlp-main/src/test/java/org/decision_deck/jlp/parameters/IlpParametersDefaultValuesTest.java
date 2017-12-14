package org.decision_deck.jlp.parameters;

import static org.junit.Assert.assertTrue;

import org.decision_deck.jlp.parameters.LpDoubleParameter;
import org.decision_deck.jlp.parameters.LpIntParameter;
import org.decision_deck.jlp.parameters.LpObjectParameter;
import org.decision_deck.jlp.parameters.LpParametersDefaultValues;
import org.decision_deck.jlp.parameters.LpStringParameter;
import org.junit.Test;

public class IlpParametersDefaultValuesTest {
	@Test
	public void testInit() throws Exception {
		assertTrue(LpParametersDefaultValues.getDefaultDoubleValues().size() == LpDoubleParameter.values().length);
		assertTrue(LpParametersDefaultValues.getDefaultIntValues().size() == LpIntParameter.values().length);
		assertTrue(LpParametersDefaultValues.getDefaultStringValues().size() == LpStringParameter.values().length);
		assertTrue(LpParametersDefaultValues.getDefaultObjectValues().size() == LpObjectParameter.values().length);
	}
}
