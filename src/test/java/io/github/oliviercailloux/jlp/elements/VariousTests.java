package io.github.oliviercailloux.jlp.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;

class VariousTests {

	@Test
	void testRangeToString() {
		assertEquals("(−∞..+∞)", FiniteRange.toString(FiniteRange.ALL_FINITE));
		assertEquals("(-Infinity..Infinity)", FiniteRange.ALL_FINITE.toString());
		assertEquals("(-∞..+∞)", Range.all().toString());
	}

}
