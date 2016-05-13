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
import java.util.LinkedList;

import org.decision_deck.jlp.utils.LpLinearUtils;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * <p>
 * A simple mutable implementation of {@link LpLinear} based on a {@link LinkedList}.
 * </p>
 * <p>
 * It shouldn't be necessary to access this class directly. It is recommanded to create linear expressions through the
 * methods available in {@link LpLinearUtils}.
 * </p>
 * 
 * @param <V>
 *            the type of the variables.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpLinearImpl<V> extends LinkedList<LpTerm<V>> implements LpLinear<V> {

    private static final long serialVersionUID = 1L;

    public LpLinearImpl() {
	/** Public no argument constructor. */
    }

    public LpLinearImpl(Collection<LpTerm<V>> terms) {
	super(terms);
    }

    @Override
    public void addTerm(double coefficient, V variable) {
	LpTerm<V> term = new LpTerm<V>(coefficient, variable);
	add(term);
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.add("size", "" + size());
	helper.add("expr", LpLinearUtils.getAsString(this));
	return helper.toString();
    }

}
