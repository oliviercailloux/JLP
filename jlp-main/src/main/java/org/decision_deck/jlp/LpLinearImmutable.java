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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingList;

public class LpLinearImmutable<V> extends ForwardingList<LpTerm<V>> implements LpLinear<V> {
    private final LpLinear<V> m_delegate;

    public LpLinearImmutable(Collection<LpTerm<V>> terms) {
	m_delegate = new LpLinearImpl<V>(terms);
    }

    /**
     * @param source
     *            not <code>null</code>.
     */
    public LpLinearImmutable(LpLinear<V> source) {
	Preconditions.checkNotNull(source);
	if (source instanceof LpLinearImmutable) {
	    m_delegate = source;
	} else {
	    m_delegate = new LpLinearImpl<V>(source);
	}
    }

    @Override
    public void addTerm(double coefficient, V variable) {
	throw new UnsupportedOperationException("This object is immutable.");
    }

    @Override
    protected List<LpTerm<V>> delegate() {
	return Collections.unmodifiableList(m_delegate);
    }
}
