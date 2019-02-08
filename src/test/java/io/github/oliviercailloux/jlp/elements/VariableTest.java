package io.github.oliviercailloux.jlp.elements;

import static io.github.oliviercailloux.jlp.elements.VariableDomain.INT_DOMAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

public class VariableTest {
	@Test
	void testBadBounds() throws Exception {
		assertThrows(IllegalArgumentException.class,
				() -> Variable.of("cat1", INT_DOMAIN, Range.closed(-0.2, -0.1), ImmutableList.of()));
	}

	@Test
	void testDefaultDescription() throws Exception {
		assertEquals("x", Variable.getDefaultDescription("x", ImmutableList.of()));
		assertEquals("x_1", Variable.getDefaultDescription("x", ImmutableList.of("1")));
	}

	@Test
	void testNullReferenceInside() throws Exception {
		Object[] refs = new Object[] { "ref1", null, "ref2" };
		assertThrows(NullPointerException.class, () -> Variable.real("cat1", refs));
	}

	@Test
	void testNullReferences() throws Exception {
		Object[] refs = null;
		assertThrows(NullPointerException.class, () -> Variable.real("cat1", refs));
	}

	@Test
	void testNullReferencesObj() throws Exception {
		Object refs = null;
		assertThrows(NullPointerException.class, () -> Variable.real("cat1", refs));
	}

	@Test
	void testRanges() throws Exception {
		final double min = -Double.MAX_VALUE;
		final double max = Double.MAX_VALUE;
		final Range<Double> allFinCl = Range.closed(min, max);
		final Range<Double> allFinOp = Range.open(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		assertTrue(allFinOp.encloses(allFinCl));
		assertFalse(allFinCl.encloses(allFinOp));
		assertFalse(allFinCl.contains(Double.NEGATIVE_INFINITY));
		assertFalse(allFinCl.contains(Double.POSITIVE_INFINITY));
		assertFalse(allFinCl.contains(Double.NaN));
		assertTrue(allFinCl.contains(min));
		assertTrue(allFinCl.contains(max));
		assertFalse(allFinOp.contains(Double.NEGATIVE_INFINITY));
		assertFalse(allFinOp.contains(Double.POSITIVE_INFINITY));
		assertFalse(allFinOp.contains(Double.NaN));
		assertTrue(allFinOp.contains(min));
		assertTrue(allFinOp.contains(max));

		final Range<Double> pos = Range.atLeast(0d);
		assertTrue(pos.contains(Double.POSITIVE_INFINITY));
		assertTrue(pos.contains(Double.NaN));

		final Range<Double> posFinite = RangeOfDouble.NON_NEGATIVE;
		assertFalse(posFinite.contains(Double.POSITIVE_INFINITY));
		assertFalse(posFinite.contains(Double.NaN));
		assertFalse(posFinite.contains(-0d));
		assertTrue(posFinite.contains(0d));
	}

	@Test
	void testRightBounds() throws Exception {
		final Variable var = Variable.of("cat1", INT_DOMAIN, RangeOfDouble.closed(-0.2, 0.1), ImmutableList.of());
		assertEquals("cat1", var.getCategoricalName());
		assertEquals(ImmutableList.of(), var.getReferences());
		assertEquals("cat1", var.getDescription());
	}
}
