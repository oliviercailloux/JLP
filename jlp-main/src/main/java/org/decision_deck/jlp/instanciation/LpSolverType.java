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
