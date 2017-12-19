package io.github.oliviercailloux.jlp.elements;

import org.junit.Test;

import io.github.oliviercailloux.jlp.elements.Variable;

public class VariableTest {
	@Test(expected = NullPointerException.class)
	public void testNullReferenceInside() throws Exception {
		Object[] refs = new Object[] { "ref1", null, "ref2" };
		@SuppressWarnings("unused")
		final Variable lpVariable = Variable.newReal("cat1", refs);
	}

	@Test(expected = NullPointerException.class)
	public void testNullReferences() throws Exception {
		Object[] refs = null;
		@SuppressWarnings("unused")
		final Variable lpVariable = Variable.newReal("cat1", refs);
	}

	@Test(expected = NullPointerException.class)
	public void testNullReferencesObj() throws Exception {
		Object refs = null;
		@SuppressWarnings("unused")
		final Variable lpVariable = Variable.newReal("cat1", refs);
	}
}
