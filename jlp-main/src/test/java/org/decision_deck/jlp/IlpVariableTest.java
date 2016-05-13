/**
 * Copyright © 2010-2012 Olivier Cailloux
 *
 * 	This file is part of JLP.
 *
 * 	JLP is free software: you can redistribute it and/or modify it under the
 * 	terms of the GNU Lesser General Public License version 3 as published by
 * 	the Free Software Foundation.
 *
 * 	JLP is distributed in the hope that it will be useful, but WITHOUT ANY
 * 	WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * 	FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * 	more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public License
 * 	along with JLP. If not, see <http://www.gnu.org/licenses/>.
 */
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
