package io.github.oliviercailloux.jlp.coin.cbc;

import java.io.StringWriter;

import net.sourceforge.swimp.coin.CoinPackedVector;
import net.sourceforge.swimp.coin.OsiSolverInterface;
import net.sourceforge.swimp.osicbc.OsiCbcSolverInterface;

import org.junit.Test;

import io.github.oliviercailloux.jlp.LpSolver;
import io.github.oliviercailloux.jlp.instanciation.LpSolverFactory;
import io.github.oliviercailloux.jlp.instanciation.LpSolverType;
import io.github.oliviercailloux.jlp.problem.LpProblem;
import io.github.oliviercailloux.jlp.result.LpProblemExamples;

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
		// solver.writeProblem(LpFileFormat.SOLVER_PREFERRED, new
		// StringWriterSupplier(writer));
		// final String written = writer.toString();
		//
		// final String expected =
		// Resources.toString(getClass().getResource("OneFourThree.lp"),
		// Charsets.UTF_8);
		// assertEquals(expected, written);
	}

}