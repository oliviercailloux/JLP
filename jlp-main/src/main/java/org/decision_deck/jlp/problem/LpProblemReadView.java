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

import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpDirection;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpOperator;

import com.google.common.base.Function;

/**
 * A read-only view of an other problem.
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
public class LpProblemReadView<V> extends LpProblemForwarder<V> implements LpProblem<V> {

    public LpProblemReadView(LpProblem<V> delegate) {
	super(delegate);
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param constraint
     *            anything.
     * @return nothing.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public boolean add(LpConstraint<V> constraint) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param id
     *            anything.
     * @param lhs
     *            anything.
     * @param operator
     *            anything.
     * @param rhs
     *            anything.
     * @return nothing.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public boolean add(Object id, LpLinear<V> lhs, LpOperator operator, double rhs) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param namer
     *            anything.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public void setConstraintsNamer(Function<LpConstraint<V>, String> namer) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param variable
     *            anything.
     * @return nothing.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public boolean addVariable(V variable) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public void clear() {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param name
     *            anything.
     * @return nothing.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public boolean setName(String name) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param objective
     *            anything.
     * @param direction
     *            anything.
     * @return nothing.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public boolean setObjective(LpLinear<V> objective, LpDirection direction) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param direction
     *            anything.
     * @return nothing.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public boolean setObjectiveDirection(LpDirection direction) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param variable
     *            anything.
     * @param lowerBound
     *            anything.
     * @param upperBound
     *            anything.
     * @return nothing.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public boolean setVariableBounds(V variable, Number lowerBound, Number upperBound) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param namer
     *            anything.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public void setVariablesNamer(Function<? super V, String> namer) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    /**
     * Throws an exception as this object is a read-only view.
     * 
     * @param variable
     *            anything.
     * @param type
     *            anything.
     * @return nothing.
     * @throws UnsupportedOperationException
     *             always.
     */
    @Override
    public boolean setVariableType(V variable, LpVariableType type) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

}
