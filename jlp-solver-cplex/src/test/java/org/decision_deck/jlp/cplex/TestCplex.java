package org.decision_deck.jlp.cplex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpDirection;
import org.decision_deck.jlp.LpFileFormat;
import org.decision_deck.jlp.LpLinearImmutable;
import org.decision_deck.jlp.LpOperator;
import org.decision_deck.jlp.LpSolver;
import org.decision_deck.jlp.instanciation.LpSolverFactory;
import org.decision_deck.jlp.instanciation.LpSolverType;
import org.decision_deck.jlp.parameters.LpObjectParameter;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.problem.LpProblems;
import org.decision_deck.jlp.result.LpProblemExamples;
import org.decision_deck.jlp.result.LpResultStatus;
import org.decision_deck.jlp.result.LpSolution;
import org.decision_deck.jlp.result.LpSolutionImmutable;
import org.decision_deck.jlp.utils.LpLinearUtils;
import org.decision_deck.jlp.utils.LpSolverUtils;
import org.decision_deck.jlp.utils.StringWriterSupplier;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

@SuppressWarnings("javadoc")
public class TestCplex {
	@Test
	public void testWriteMpsRenamed() throws Exception {
		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();

		LpSolver<String> solver = new LpSolverFactory(LpSolverType.CPLEX).newSolver();
		solver.setProblem(problem);
		solver.getParameters().setValue(LpObjectParameter.NAMER_VARIABLES, getRenamer());

		final Map<LpFileFormat, Function<LpConstraint<String>, String>> constraintsNamers = Maps
				.<LpFileFormat, Function<LpConstraint<String>, String>>newHashMap();
		constraintsNamers.put(LpFileFormat.MPS, getConstraintsNamer());
		solver.getParameters().setValue(LpObjectParameter.NAMER_CONSTRAINTS_BY_FORMAT, constraintsNamers);

		final StringWriter writer = new StringWriter();
		solver.writeProblem(LpFileFormat.MPS, new StringWriterSupplier(writer));
		final String written = writer.toString();

		final String expected = Resources.toString(
				getClass().getResource("OneFourThree - Renamed variables and constraints.mps"), Charsets.UTF_8);
		assertEquals(expected, written);
	}

	@Test(expected = IllegalStateException.class)
	public void testSolveDirNotSet() throws Exception {
		LpSolverFactory factory = new LpSolverFactory();
		factory.setImpl(LpSolverType.CPLEX);

		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();
		problem.setObjectiveDirection(null);

		LpSolver<String> solver = factory.newSolver();
		solver.setProblem(problem);
		solver.solve();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnusedVar() throws Exception {
		LpSolverFactory factory = new LpSolverFactory();
		factory.setImpl(LpSolverType.CPLEX);

		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();
		problem.addVariable("z");

		final LpSolver<String> solver = factory.newSolver();
		solver.setProblem(problem);

		final LpResultStatus status = solver.solve();
		assertEquals(LpResultStatus.OPTIMAL, status);

		final LpSolution<String> solutionAugmentedProblem = solver.getSolution();
		/** Should be different because they do not relate to the same problem. */
		assertFalse(LpProblemExamples.getIntOneFourThreeSolution().equals(solutionAugmentedProblem));

		@SuppressWarnings("unused")
		final LpSolutionImmutable<String> lpSolutionImmutable = new LpSolutionImmutable<String>(
				LpProblemExamples.getIntOneFourThree(), solutionAugmentedProblem);
	}

	@Test
	public void testIntOneFourThree() throws Exception {
		LpSolverFactory factory = new LpSolverFactory();
		factory.setImpl(LpSolverType.CPLEX);

		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();

		LpSolver<String> solver = factory.newSolver();
		solver.setProblem(problem);

		final LpResultStatus status = solver.solve();
		assertEquals(LpResultStatus.OPTIMAL, status);

		LpSolution<String> solution = solver.getSolution();
		assertTrue(LpSolverUtils.equivalent(solution, LpProblemExamples.getIntOneFourThreeSolution(), 1e-5));

		final LpProblem<String> problemLowX = LpProblemExamples.getIntOneFourThreeLowX();
		solver.setProblem(problemLowX);

		final LpResultStatus statusLowX = solver.solve();
		assertEquals(LpResultStatus.OPTIMAL, statusLowX);

		final LpSolution<String> solutionLowX = solver.getSolution();
		assertEquals(LpProblemExamples.getIntOneFourThreeLowXSolution(), solutionLowX);
	}

	@Test
	public void testWriteLpVariablesRenamed() throws Exception {
		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();

		LpSolver<String> solver = new LpSolverFactory(LpSolverType.CPLEX).newSolver();
		solver.setProblem(problem);
		final Map<LpFileFormat, Function<String, String>> variablesNamers = Maps
				.<LpFileFormat, Function<String, String>>newHashMap();
		variablesNamers.put(LpFileFormat.CPLEX_LP, getRenamer());
		solver.getParameters().setValue(LpObjectParameter.NAMER_VARIABLES_BY_FORMAT, variablesNamers);

		/** Constraints namers for a different format => should have no effect. */
		final Map<LpFileFormat, Function<LpConstraint<String>, String>> constraintsNamers = Maps
				.<LpFileFormat, Function<LpConstraint<String>, String>>newHashMap();
		constraintsNamers.put(LpFileFormat.CPLEX_SAV, getConstraintsNamer());
		solver.getParameters().setValue(LpObjectParameter.NAMER_CONSTRAINTS_BY_FORMAT, constraintsNamers);

		final StringWriter writer = new StringWriter();
		solver.writeProblem(LpFileFormat.CPLEX_LP, new StringWriterSupplier(writer));
		final String written = writer.toString();

		final String expected = Resources.toString(getClass().getResource("OneFourThree - Renamed variables.lp"),
				Charsets.UTF_8);
		assertEquals(expected, written);
	}

	private Function<LpConstraint<String>, String> getConstraintsNamer() {
		return new Function<LpConstraint<String>, String>() {
			@Override
			public String apply(LpConstraint<String> input) {
				return "cstr_" + input.getIdAsString();
			}
		};
	}

	@Test
	public void testWriteMps() throws Exception {
		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();

		LpSolver<String> solver = new LpSolverFactory(LpSolverType.CPLEX).newSolver();
		solver.setProblem(problem);
		final File temp = File.createTempFile("cplex-test", ".mps");
		temp.deleteOnExit();
		final StringWriter writer = new StringWriter();
		solver.writeProblem(LpFileFormat.MPS, new StringWriterSupplier(writer));
		final String written = writer.toString();

		final String expected = Resources.toString(getClass().getResource("OneFourThree.mps"), Charsets.UTF_8);
		assertEquals(expected, written);
	}

	@Test
	public void testWriteLpSpecial() throws Exception {
		final LpProblem<String> problem = LpProblems.newProblem();
		problem.addVariable("" + '\u03BB');

		LpSolver<String> solver = new LpSolverFactory(LpSolverType.CPLEX).newSolver();
		solver.setProblem(problem);

		final StringWriter stringWriter = new StringWriter();
		solver.writeProblem(LpFileFormat.CPLEX_LP, new StringWriterSupplier(stringWriter));
		final String written = stringWriter.toString();

		final String expected = Resources.toString(getClass().getResource("SpecialChar.lp"), Charsets.UTF_8);
		assertEquals(expected, written);
	}

	@Test
	public void testWriteLpConstraintsRenamedInSolver() throws Exception {
		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();
		final Function<LpConstraint<String>, String> namer = getConstraintsNamer();

		LpSolver<String> solver = new LpSolverFactory(LpSolverType.CPLEX).newSolver();
		solver.setProblem(problem);
		solver.getParameters().setValue(LpObjectParameter.NAMER_CONSTRAINTS, namer);
		final StringWriter writer = new StringWriter();
		solver.writeProblem(LpFileFormat.CPLEX_LP, new StringWriterSupplier(writer));
		final String written = writer.toString();

		final String expected = Resources.toString(getClass().getResource("OneFourThree - Renamed constraints.lp"),
				Charsets.UTF_8);
		assertEquals(expected, written);
	}

	private Function<String, String> getRenamer() {
		return new Function<String, String>() {
			@Override
			public String apply(String input) {
				return "var_" + input;
			}
		};
	}

	@Test
	public void testWriteLpConstraintsRenamedInProblem() throws Exception {
		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();
		final Function<LpConstraint<String>, String> namer = getConstraintsNamer();
		problem.setConstraintsNamer(namer);

		LpSolver<String> solver = new LpSolverFactory(LpSolverType.CPLEX).newSolver();
		solver.setProblem(problem);
		final StringWriter writer = new StringWriter();
		solver.writeProblem(LpFileFormat.CPLEX_LP, new StringWriterSupplier(writer));
		final String written = writer.toString();

		final String expected = Resources.toString(getClass().getResource("OneFourThree - Renamed constraints.lp"),
				Charsets.UTF_8);
		assertEquals(expected, written);
	}

	@Test
	public void testUnbounded() throws Exception {
		LpSolverFactory factory = new LpSolverFactory();
		factory.setImpl(LpSolverType.CPLEX);

		final LpProblem<String> problem = LpProblems.newProblem();

		LpSolver<String> solver = factory.newSolver();
		solver.setProblem(problem);

		{
			final LpResultStatus status = solver.solve();
			assertEquals(LpResultStatus.FEASIBLE, status);
		}

		problem.addVariable("x");
		final LpLinearImmutable<String> linX = LpLinearUtils.newImmutable(1, "x");
		problem.setObjective(linX, LpDirection.MAX);
		{
			final LpResultStatus status = solver.solve();
			assertEquals(LpResultStatus.INFEASIBLE_OR_UNBOUNDED, status);
		}

		problem.add("max x", linX, LpOperator.LE, 3);
		{
			final LpResultStatus status = solver.solve();
			assertEquals(LpResultStatus.OPTIMAL, status);
		}
	}

	@Test
	public void testWriteLp() throws Exception {
		final LpProblem<String> problem = LpProblemExamples.getIntOneFourThree();

		LpSolver<String> solver = new LpSolverFactory(LpSolverType.CPLEX).newSolver();
		solver.setProblem(problem);
		final Function<String, String> renamer = getRenamer();
		final HashMap<LpFileFormat, Function<String, String>> namers = Maps
				.<LpFileFormat, Function<String, String>>newHashMap();
		namers.put(LpFileFormat.CPLEX_SAV, renamer);
		solver.getParameters().setValue(LpObjectParameter.NAMER_VARIABLES_BY_FORMAT, namers);
		final StringWriter writer = new StringWriter();
		solver.writeProblem(LpFileFormat.CPLEX_LP, new StringWriterSupplier(writer));
		final String written = writer.toString();

		final String expected = Resources.toString(getClass().getResource("OneFourThree.lp"), Charsets.UTF_8);
		assertEquals(expected, written);
	}

}