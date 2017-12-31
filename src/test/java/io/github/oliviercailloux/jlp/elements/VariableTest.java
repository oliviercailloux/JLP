package io.github.oliviercailloux.jlp.elements;

import static io.github.oliviercailloux.jlp.elements.VariableDomain.INT_DOMAIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Range;

public class VariableTest {
	@Test(expected = IllegalArgumentException.class)
	public void testBadBounds() throws Exception {
		Variable.newVariable("cat1", INT_DOMAIN, Range.closed(-0.2, -0.1));
	}

	@Test(expected = NullPointerException.class)
	public void testNullReferenceInside() throws Exception {
		Object[] refs = new Object[] { "ref1", null, "ref2" };
		Variable.newReal("cat1", refs);
	}

	@Test(expected = NullPointerException.class)
	public void testNullReferences() throws Exception {
		Object[] refs = null;
		Variable.newReal("cat1", refs);
	}

	@Test(expected = NullPointerException.class)
	public void testNullReferencesObj() throws Exception {
		Object refs = null;
		Variable.newReal("cat1", refs);
	}

	@Test
	public void testRanges() throws Exception {
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

		final Range<Double> posFinite = FiniteRange.NON_NEGATIVE;
		assertFalse(posFinite.contains(Double.POSITIVE_INFINITY));
		assertFalse(posFinite.contains(Double.NaN));
		assertFalse(posFinite.contains(-0d));
		assertTrue(posFinite.contains(0d));
	}

	@Test
	public void testRightBounds() throws Exception {
		final Variable var = Variable.newVariable("cat1", INT_DOMAIN, FiniteRange.closed(-0.2, 0.1));
		final String descr = var.toString();
		assertEquals("cat1", descr);
	}
}
