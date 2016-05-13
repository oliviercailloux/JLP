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
package org.decision_deck.jlp.instanciation;

import org.decision_deck.jlp.LpSolverException;

public class LpSolverFactoryException extends LpSolverException {

    private static final long serialVersionUID = 1L;

    public LpSolverFactoryException() {
	super();
    }

    public LpSolverFactoryException(String message) {
	super(message);
    }

    public LpSolverFactoryException(String message, Throwable cause) {
	super(message, cause);
    }

    public LpSolverFactoryException(Throwable cause) {
	super(cause);
    }

}
