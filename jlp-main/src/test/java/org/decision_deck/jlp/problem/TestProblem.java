package org.decision_deck.jlp.problem;

import org.decision_deck.jlp.LpDirection;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpLinearImpl;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.problem.LpProblemImpl;
import org.junit.Test;

public class TestProblem {

	@Test(expected = IllegalArgumentException.class)
	public void testProblemMissingVar() throws Exception {
		LpProblem<String> problem = new LpProblemImpl<String>();

		LpLinear<String> linear = new LpLinearImpl<String>();
		linear.addTerm(1, "x");

		problem.setObjective(linear, LpDirection.MAX);

	}

}
