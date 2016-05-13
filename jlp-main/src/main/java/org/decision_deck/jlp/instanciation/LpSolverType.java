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

public enum LpSolverType {
    /**
     * The <a href="http://publib.boulder.ibm.com/infocenter/cosinfoc/v12r3/index.jsp">IBM ILOG CPLEX</a> solver.
     */
    CPLEX, /**
     * The <a href="http://lpsolve.sourceforge.net/">lp_solve</a> solver.
     */
    LP_SOLVE,

    /**
     * The COIN-OR <a href="https://projects.coin-or.org/Cbc">Cbc</a> solver.
     */
    CBC
}
