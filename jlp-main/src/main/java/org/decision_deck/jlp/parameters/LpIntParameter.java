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

public enum LpIntParameter {
    /**
     * 1 to force the solver to have a deterministic behavior: if run twice with the same input and parameters, will
     * find twice the same solution. The default value of 0 does not force the solver, which can enhance performances
     * especially in multi thread setting.
     */
    DETERMINISTIC,

    /**
     * <code>null</code> for no max (default), otherwise must be a strictly positive number. TODO document what happens
     * if the number is higher than the number of physical threads.
     */
    MAX_THREADS
}
