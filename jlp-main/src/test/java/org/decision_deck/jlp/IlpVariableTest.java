package org.decision_deck.jlp;

import org.decision_deck.jlp.LpVariable;
import org.junit.Test;

public class IlpVariableTest {
    @Test(expected = NullPointerException.class)
    public void testNullReferenceInside() throws Exception {
	Object[] refs = new Object[] { "ref1", null, "ref2" };
	@SuppressWarnings("unused")
	final LpVariable<String> lpVariable = new LpVariable<String>("cat1", refs);
    }

    @Test(expected = NullPointerException.class)
    public void testNullReferences() throws Exception {
	Object[] refs = null;
	@SuppressWarnings("unused")
	final LpVariable<String> lpVariable = new LpVariable<String>("cat1", refs);
    }

    @Test(expected = NullPointerException.class)
    public void testNullReferencesObj() throws Exception {
	Object refs = null;
	@SuppressWarnings("unused")
	final LpVariable<String> lpVariable = new LpVariable<String>("cat1", refs);
    }
}
