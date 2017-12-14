package io.github.oliviercailloux.jlp.lpsolve;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import io.github.oliviercailloux.jlp.LpSolver;
import io.github.oliviercailloux.jlp.instanciation.LpSolverFactory;
import io.github.oliviercailloux.jlp.instanciation.LpSolverType;
import io.github.oliviercailloux.jlp.problem.LpProblem;
import io.github.oliviercailloux.jlp.result.LpProblemExamples;
import io.github.oliviercailloux.jlp.result.LpResultStatus;
import io.github.oliviercailloux.jlp.result.LpSolution;
import io.github.oliviercailloux.jlp.result.LpSolutionImmutable;

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