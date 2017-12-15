package io.github.oliviercailloux.jlp.problem;

import org.junit.Test;

import io.github.oliviercailloux.jlp.LpDirection;
import io.github.oliviercailloux.jlp.LpLinear;
import io.github.oliviercailloux.jlp.LpLinearImpl;
import io.github.oliviercailloux.jlp.problem.LpProblem;
import io.github.oliviercailloux.jlp.problem.LpProblemImpl;

public class TestProblem {

	@Test(expected = IllegalArgumentException.class)
	public void testProblemMissingVar() throws Exception {
		LpProblem<String> problem = new LpProblemImpl<String>();

		LpLinear<String> linear = new LpLinearImpl<String>();
		linear.addTerm(1, "x");

		problem.setObjective(linear, LpDirection.MAX);

	}

}
