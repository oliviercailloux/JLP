package io.github.oliviercailloux.jlp.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TermTest {
	@Test
	public void testTermStr0() throws Exception {
		final Term t = Term.of(0d, Variable.integer("i"));
		assertEquals("0 i", t.toString());
	}

	@Test
	public void testTermStr1() throws Exception {
		final Term t = Term.of(1.0d, Variable.integer("i"));
		assertEquals("i", t.toString());
	}

	@Test
	public void testTermStrDec() throws Exception {
		final Term t = Term.of(1.2345d, Variable.integer("i"));
		assertEquals("1.23 i", t.toString());
	}

	@Test
	public void testTermStrE() throws Exception {
		final Term t = Term.of(12345d, Variable.integer("i"));
		assertEquals("1.23E+4 i", t.toString());
	}

	@Test
	public void testTermStrHalf() throws Exception {
		final Term t = Term.of(0.5d, Variable.integer("i"));
		assertEquals("0.5 i", t.toString());
	}

	@Test
	public void testTermStrOneThird() throws Exception {
		final Term t = Term.of(1.0d / 3d, Variable.integer("i"));
		assertEquals("0.333 i", t.toString());
	}
}
