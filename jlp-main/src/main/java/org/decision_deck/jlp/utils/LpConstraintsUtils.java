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
package org.decision_deck.jlp.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jlp.LpOperator;

public class LpConstraintsUtils {
    static public void assertSatisfied(double lhs, LpOperator op, double rhs) {
	if (!satisfied(lhs, op, rhs)) {
	    throw new IllegalStateException("Unsatisfied: " + lhs + op + rhs + ".");
	}
    }

    static public void assertSatisfied(int lhs, LpOperator op, int rhs) {
	if (!satisfied(lhs, op, rhs)) {
	    throw new IllegalStateException("Unsatisfied: " + lhs + op + rhs + ".");
	}
    }

    /**
     * <p>
     * Tests whether the given comparison is <code>true</code>, or equivalently whether the constraints it represents is
     * satisfied.
     * </p>
     * <p>
     * Using this method requires particular caution when op is equality, because this method allows for no tolerance
     * and will thus consider two double values as being unequal even if they differ by a very small amount.
     * </p>
     * 
     * @param lhs
     *            a real number, or infinity.
     * @param op
     *            not <code>null</code>.
     * @param rhs
     *            a real number, or infinity.
     * @return <code>true</code> iff lhs compares to rhs in the way represented by the given operator.
     */
    static public boolean satisfied(double lhs, LpOperator op, double rhs) {
	checkNotNull(op);
	switch (op) {
	case EQ:
	    return lhs == rhs;
	case GE:
	    return lhs >= rhs;
	case LE:
	    return lhs <= rhs;
	}
	throw new IllegalStateException("Unknown operator.");
    }

    static public boolean satisfied(int lhs, LpOperator op, int rhs) {
	checkNotNull(op);
	switch (op) {
	case EQ:
	    return lhs == rhs;
	case GE:
	    return lhs >= rhs;
	case LE:
	    return lhs <= rhs;
	}
	throw new IllegalStateException("Unknown operator.");
    }
}
