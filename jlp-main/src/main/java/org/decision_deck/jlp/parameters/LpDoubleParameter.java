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
package org.decision_deck.jlp.parameters;

import java.math.RoundingMode;

import org.decision_deck.jlp.LpSolverException;

/**
 * <p>
 * Contains the parameters accepting a real number as value.
 * </p>
 * <p>
 * Max cpu seconds and max wall seconds should <i>not</i> be both set. One or zero of these two parameters may be set.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public enum LpDoubleParameter {
    MAX_CPU_SECONDS, MAX_TREE_SIZE_MB, MAX_MEMORY_MB, /**
     * Caution must be exercised when using very small values for this
     * parameter. The value will be rounded ({@link RoundingMode#HALF_UP}) if the underlying solver accepts only integer
     * number of seconds. In that case and if the timeout is less than 0.5 seconds, it would round to zero seconds, and
     * a {@link LpSolverException} would be raised when solving.
     */
    MAX_WALL_SECONDS
}
