package org.decision_deck.jlp.coin.cbc;

import java.io.StringWriter;

import net.sourceforge.swimp.coin.CoinPackedVector;
import net.sourceforge.swimp.coin.OsiSolverInterface;
import net.sourceforge.swimp.osicbc.OsiCbcSolverInterface;

import org.decision_deck.jlp.LpSolver;
import org.decision_deck.jlp.instanciation.LpSolverFactory;
import org.decision_deck.jlp.instanciation.LpSolverType;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.result.LpProblemExamples;
import org.junit.Test;

public class TestCbc {

    @Test
    public void testStart() throws Exception {
	final OsiSolverInterface osiSolver = new OsiCbcSolverInterface();
	// osiSolver.setColBounds(0, 0, 3);
	try {
	    final CoinPackedVector v = new CoinPackedVector();
	    v.add(3);
	    osiSolver.addRow(v, 0, 1);
	    osiSolver.branchAndBound();
	    final double objValue = osiSolver.getObjValue();
	    final double[] solutions = osiSolver.getColSolutionVec();
	} finally {
	    osiSolver.delete();
	}
    }

    @Test
    public void testWriteLp() throws Exception {
        final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();
    
	final LpSolver<String> solver = new LpSolverFactory(LpSolverType.CBC).newSolver();
	solver.setProblem(problem);
	final StringWriter writer = new StringWriter();
	// solver.writeProblem(LpFileFormat.SOLVER_PREFERRED, new StringWriterSupplier(writer));
	// final String written = writer.toString();
	//
	// final String expected = Resources.toString(getClass().getResource("OneFourThree.lp"), Charsets.UTF_8);
	// assertEquals(expected, written);
    }

}