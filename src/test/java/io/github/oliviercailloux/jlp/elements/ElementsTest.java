package io.github.oliviercailloux.jlp.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ElementsTest {
	@Test
	public void testConstraintStr() throws Exception {
		final SumTerms t1 = SumTerms.of(1d / 3, Variable.bool("x"), 1d, Variable.integer("y"));
		final Constraint c = Constraint.of("d", t1, ComparisonOperator.GE, 3d);
		assertEquals("Constraint{description=d, expression=SumTerms{0.333 x + y} ≥ 3.0}", c.toString());
	}

	@Test
	public void testSumStr() throws Exception {
		final SumTerms t1 = SumTerms.of(1d / 3, Variable.bool("x"), 1d, Variable.integer("y"));
		assertEquals("SumTerms{0.333 x + y}", t1.toString());
	}
}
