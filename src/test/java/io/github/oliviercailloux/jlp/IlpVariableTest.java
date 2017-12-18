package io.github.oliviercailloux.jlp;

import org.junit.Test;

import io.github.oliviercailloux.jlp.elements.NamedVariable;

public class IlpVariableTest {
	@Test(expected = NullPointerException.class)
	public void testNullReferenceInside() throws Exception {
		Object[] refs = new Object[] { "ref1", null, "ref2" };
		@SuppressWarnings("unused")
		final NamedVariable lpVariable = NamedVariable.newReal("cat1", refs);
	}

	@Test(expected = NullPointerException.class)
	public void testNullReferences() throws Exception {
		Object[] refs = null;
		@SuppressWarnings("unused")
		final NamedVariable lpVariable = NamedVariable.newReal("cat1", refs);
	}

	@Test(expected = NullPointerException.class)
	public void testNullReferencesObj() throws Exception {
		Object refs = null;
		@SuppressWarnings("unused")
		final NamedVariable lpVariable = NamedVariable.newReal("cat1", refs);
	}
}
