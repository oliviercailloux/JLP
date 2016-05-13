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
package org.decision_deck.jlp.result;

public enum LpResultStatus {
    ERROR_NO_SOLUTION, /**
     * 
     * 
     * 
     * 
     * 
     * An error happened, or an unknown status was returned from the solver, but a feasible, non
     * necessarily optimal, solution has been found.
     */
    ERROR_WITH_SOLUTION, FEASIBLE, INFEASIBLE, INFEASIBLE_OR_UNBOUNDED, MEMORY_LIMIT_REACHED_WITH_SOLUTION, /**
     * Because
     * of user set limit or out-of-memory status.
     */
    MEMORY_LIMIT_REACHED_NO_SOLUTION, /**
     * Optimal or optimal to the allowed imprecision.
     */
    OPTIMAL, TIME_LIMIT_REACHED_NO_SOLUTION, TIME_LIMIT_REACHED_WITH_SOLUTION, UNBOUNDED_NO_SOLUTION;

    /**
     * Tests whether this return status implies that a feasible solution has been found.
     * 
     * @return <code>false</code> iff no feasible solution has been found, including in the case the problem is
     *         unbounded (which implies that feasible solutions do exist).
     */
    public boolean foundFeasible() {
	switch (this) {
	case OPTIMAL:
	case FEASIBLE:
	case TIME_LIMIT_REACHED_WITH_SOLUTION:
	case MEMORY_LIMIT_REACHED_WITH_SOLUTION:
	case ERROR_WITH_SOLUTION:
	    return true;
	case INFEASIBLE:
	case UNBOUNDED_NO_SOLUTION:
	case INFEASIBLE_OR_UNBOUNDED:
	case TIME_LIMIT_REACHED_NO_SOLUTION:
	case MEMORY_LIMIT_REACHED_NO_SOLUTION:
	case ERROR_NO_SOLUTION:
	    return false;
	}
	throw new IllegalStateException();
    }
}
