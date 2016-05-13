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
package org.decision_deck.jlp.problem;

/**
 * A view of a problem providing its own problem name. This view delegates everything to the delegate problem except the
 * {@link #setName(String)} and {@link #getName()} methods as it provides its own name. The underlying problem name is
 * not used.
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
class LpProblemOwnName<V> extends LpProblemForwarder<V> implements LpProblem<V> {

    /**
     * not <code>null</code>, empty if not set.
     */
    private String m_name;

    public LpProblemOwnName(LpProblem<V> delegate) {
	super(delegate);
	m_name = "";
    }

    @Override
    public String getName() {
	return m_name;
    }

    @Override
    public boolean setName(String name) {
	final boolean eq = m_name.equals(name);
	if (eq) {
	    return false;
	}
	m_name = name == null ? "" : name;
	return true;
    }

}
