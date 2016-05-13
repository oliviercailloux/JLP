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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An objective function with optimization direction (possibly <code>null</code>s). Immutable.
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
public class LpObjective<V> {
    /**
     * May be <code>null</code>.
     */
    final private LpDirection m_direction;

    /**
     * May be <code>null</code>.
     */
    final private LpLinearImmutable<V> m_objectiveFunction;

    /**
     * Creates a new objective function with direction.
     * 
     * @param objectiveFunction
     *            may be <code>null</code>.
     * @param direction
     *            may be <code>null</code>.
     */
    public LpObjective(LpLinear<V> objectiveFunction, LpDirection direction) {
	m_objectiveFunction = objectiveFunction == null ? null : new LpLinearImmutable<V>(objectiveFunction);
	m_direction = direction;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}
	if (!(obj instanceof LpObjective)) {
	    return false;
	}
	LpObjective<?> obj2 = (LpObjective<?>) obj;
	if (!Objects.equal(getDirection(), obj2.getDirection())) {
	    return false;
	}
	if (!Objects.equal(getFunction(), obj2.getFunction())) {
	    return false;
	}
	return true;
    }

    /**
     * Retrieves the optimization direction of the objective function to be.
     * 
     * @return possibly <code>null</code>.
     */
    public LpDirection getDirection() {
	return m_direction;
    }

    /**
     * Retrieves the objective function stored in this object.
     * 
     * @return possibly <code>null</code>. Is a copy or is immutable.
     */
    public LpLinear<V> getFunction() {
	return m_objectiveFunction;
    }

    @Override
    public int hashCode() {
	return Objects.hashCode(m_direction, m_objectiveFunction);
    }

    /**
     * Tests whether this objective is fully specified, i.e. has a function and a direction set.
     * 
     * @return <code>true</code> iff both the objective function and the direction are non <code>null</code>.
     */
    public boolean isComplete() {
	return m_objectiveFunction != null && m_direction != null;
    }

    /**
     * Tests whether this objective is empty, i.e. has neither a function nor a direction set.
     * 
     * @return <code>true</code> iff both the objective function and the direction are <code>null</code>.
     */
    public boolean isEmpty() {
	return m_objectiveFunction == null && m_direction == null;
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.add("Function", m_objectiveFunction);
	helper.add("Direction", m_direction);
	return helper.toString();
    }
}
