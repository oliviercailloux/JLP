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
package org.decision_deck.jlp.lpsolve;

import lpsolve.LogListener;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper around the LP solve logger that can be used to redirect logging information to the SLF logger embedded in
 * this class.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpSolveLogger implements LogListener {
    private static final Logger s_logger = LoggerFactory.getLogger(LpSolveLogger.class);

    @Override
    public void logfunc(LpSolve problem, Object userhandle, String buf) throws LpSolveException {
	final String logStr = buf.replace('\n', ' ').trim();
	if (logStr.length() > 0) {
	    s_logger.info(logStr);
	}
    }
}