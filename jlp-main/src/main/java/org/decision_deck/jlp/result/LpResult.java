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

import org.decision_deck.jlp.parameters.LpParameters;
import org.decision_deck.jlp.solver.experimental.LpResultTransient;

/**
 * <p>
 * A result of a problem solved by a solver, giving informations about the solve attempt result. This is different from
 * a solution: attempting to solve a problem always ends up producing a result object; whereas solving a problem does
 * not necessarily yield a solution to the problem: a solution object only exists if a feasible solution has been found.
 * If the solve attempt resulted in an error, a result object will be produced indicating that an error occurred, but no
 * solution will be produced.
 * </p>
 * <p>
 * Immutable.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
public interface LpResult<V> extends LpResultTransient<V> {

    /**
     * Retrieves one solution found to the problem solved. If the result of the solve is optimal, the returned solution
     * is an optimal solution. This method may <em>not</em> be called if no feasible solution to the problem has been
     * found.
     * 
     * @return not <code>null</code>. Immutable.
     * @see #getResultStatus()
     * @see LpResultStatus#foundFeasible()
     */
    @Override
    public LpSolution<V> getSolution();

    /**
     * Retrieves the parameters that have been used to obtain this result.
     * 
     * @return not <code>null</code>.
     */
    @Override
    public LpParameters getParameters();

}
