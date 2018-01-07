package io.github.oliviercailloux.jlp.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.result.MPExamples;

public class MPTest {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MPTest.class);

	@Test
	public void testDuplicateDescr() {
		final MP mp = MP.create();
		mp.getVariables().add(Variable.bool("b"));
		final Constraint intBEqZero = Constraint.of("int-b=0", SumTerms.of(1d, Variable.integer("b")),
				ComparisonOperator.EQ, 0d);
		final IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> mp.add(intBEqZero));
		LOGGER.debug(exc.getMessage());
	}

	@Test
	public void testRemoveFromConstraint() throws Exception {
		final MP mp = MP.create();
		final Variable b1 = Variable.bool("b1");
		final Variable b2 = Variable.bool("b2");
		final Variable b3 = Variable.bool("b3");
		assertEquals(0, mp.getVariables().size());
		mp.getVariables().addAll(ImmutableList.of(b1, b2, b3));
		assertEquals(3, mp.getVariables().size());
		mp.setObjective(Objective.max(SumTerms.of(1d, b1)));
		mp.add(Constraint.of("c2", SumTerms.of(2d, b2), ComparisonOperator.LE, 2d));
		mp.add(Constraint.of("c3", SumTerms.of(3d, b3), ComparisonOperator.LE, 3d));
		final IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
				() -> mp.getVariables().remove(b3));
		LOGGER.debug(exc.getMessage());
	}

	@Test
	public void testRemoveFromObjective() throws Exception {
		final MP mp = MP.create();
		final Variable b = Variable.bool("b");
		assertEquals(0, mp.getVariables().size());
		mp.getVariables().add(b);
		assertEquals(1, mp.getVariables().size());
		mp.setObjective(Objective.max(SumTerms.of(1d, b)));
		final IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
				() -> mp.getVariables().remove(0));
		LOGGER.debug(exc.getMessage());
	}

	@Test
	public void testRetainAll() throws Exception {
		final MP mp = MP.create();
		final Variable b1 = Variable.bool("b1");
		final Variable b2 = Variable.bool("b2");
		final Variable b3 = Variable.bool("b3");
		final Variable b4 = Variable.bool("b4");
		mp.getVariables().addAll(ImmutableList.of(b1, b2, b3, b4));
		mp.setObjective(Objective.max(SumTerms.of(1d, b2)));
		mp.add(Constraint.of("c2", SumTerms.of(2d, b2), ComparisonOperator.LE, 2d));
		mp.add(Constraint.of("c3", SumTerms.of(3d, b2), ComparisonOperator.LE, 3d));
		mp.getVariables().retainAll(ImmutableList.of(b1, b2, b3, b4));
		mp.getVariables().retainAll(ImmutableList.of(b2, b4));
		assertEquals(2, mp.getVariables().size());
	}

	@Test
	public void testStr() throws Exception {
		final MP mp = MPExamples.getIntOneFourThree();
		final String descr = mp.toString();
		LOGGER.debug("Descr: {}.", descr);
		assertTrue(descr.length() < 200);
	}

}
