package org.decision_deck.jlp.lpsolve;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.decision_deck.jlp.LpSolver;
import org.decision_deck.jlp.instanciation.LpSolverFactory;
import org.decision_deck.jlp.instanciation.LpSolverType;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.result.LpProblemExamples;
import org.decision_deck.jlp.result.LpResultStatus;
import org.decision_deck.jlp.result.LpSolution;
import org.decision_deck.jlp.result.LpSolutionImmutable;
import org.junit.Test;

// for missing javadoc.
@SuppressWarnings("all")
public class TestLpSolve {
	@Test
	// for missing javadoc.
	@SuppressWarnings("all")
	public void testIntOneFourThree() throws Exception {
		LpSolver<String> solver = LpSolverFactory.newSolver(LpSolverType.LP_SOLVE);

		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();

		solver.setProblem(problem);

		final LpResultStatus status = solver.solve();
		assertEquals(LpResultStatus.OPTIMAL, status);

		LpSolution<String> solution = solver.getSolution();
		assertEquals(LpProblemExamples.getIntOneFourThreeSolution(), solution);

		final LpProblem<String> problemLowX = LpProblemExamples.getIntOneFourThreeLowX();
		solver.setProblem(problemLowX);

		final LpResultStatus statusLowX = solver.solve();
		assertEquals(LpResultStatus.OPTIMAL, statusLowX);

		final LpSolution<String> solutionLowX = solver.getSolution();
		assertEquals(LpProblemExamples.getIntOneFourThreeLowXSolution(), solutionLowX);
	}

	@Test(expected = IllegalStateException.class)
	// for missing javadoc.
	@SuppressWarnings("all")
	public void testSolveDirNotSet() throws Exception {
		LpSolver<String> solver = LpSolverFactory.newSolver(LpSolverType.LP_SOLVE);

		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();
		problem.setObjectiveDirection(null);

		solver.setProblem(problem);
		solver.solve();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnusedVar() throws Exception {
		LpSolver<String> solver = LpSolverFactory.newSolver(LpSolverType.LP_SOLVE);

		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();

		solver.setProblem(problem);

		problem.addVariable("z");

		final LpResultStatus status = solver.solve();
		assertEquals(LpResultStatus.OPTIMAL, status);

		LpSolution<String> solutionAugmentedProblem = solver.getSolution();
		/** Should be different because they do not relate to the same problem. */
		assertFalse(LpProblemExamples.getIntOneFourThreeSolution().equals(solutionAugmentedProblem));

		@SuppressWarnings("unused")
		final LpSolutionImmutable<String> lpSolutionImmutable = new LpSolutionImmutable<String>(
				LpProblemExamples.getIntOneFourThree(), solutionAugmentedProblem);
	}

}