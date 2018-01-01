package io.github.oliviercailloux.jlp.mp;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.result.MPExamples;

public class MPTest {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MPTest.class);

	@Test
	public void testAddLots() throws Exception {
		final MP mp = MP.create();
//		mp.setDebug(true);
		LOGGER.info("Start add.");
		for (int i = 0; i < 5000; ++i) {
			mp.addVariable(Variable.integer("" + i));
		}
		/**
		 * Without debug, adding 5000 variables takes 100 ms, with debug, it takes 17
		 * seconds. (Very rough measures.)
		 */
		LOGGER.info("End add.");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDuplicateDescr() {
		final MP mp = MP.create();
		mp.setDebug(true);
		mp.addVariable(Variable.bool("b"));
		final Constraint intBEqZero = Constraint.of("int-b=0", SumTerms.of(1d, Variable.integer("b")),
				ComparisonOperator.EQ, 0d);
		try {
			mp.add(intBEqZero);
		} catch (IllegalArgumentException exc) {
//			LOGGER.info("Adding constraint.", exc);
			throw exc;
		}
	}

	@Test
	public void testStr() throws Exception {
		final MP mp = MPExamples.getIntOneFourThree();
		final String descr = mp.toString();
		LOGGER.info("Descr: {}.", descr);
		assertTrue(descr.length() < 200);
	}

}
