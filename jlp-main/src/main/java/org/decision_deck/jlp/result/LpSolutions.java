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

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jlp.problem.LpProblem;

/**
 * Utilities methods related to a {@link LpSolution}.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpSolutions {
    static public <V> LpSolution<V> newSolution(LpProblem<V> problem) {
	checkNotNull(problem);
	return new LpSolutionImpl<V>(problem);
    }

    /**
     * Returns a solution containing the same information as the source solution, and which is immutable.
     * 
     * @param <V>
     *            the type of variables.
     * @param source
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public <V> LpSolutionImmutable<V> newImmutable(LpSolution<V> source) {
	if (source instanceof LpSolutionImmutable<?>) {
	    return (LpSolutionImmutable<V>) source;
	}
	return new LpSolutionImmutable<V>(source);
    }
}
