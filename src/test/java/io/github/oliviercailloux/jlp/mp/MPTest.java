package io.github.oliviercailloux.jlp.mp;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.Variable;

public class MPTest {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MPTest.class);

	@Test(expected = IllegalArgumentException.class)
	public void testDuplicateDescr() {
		final MP mp = MP.create();
		mp.addVariable(Variable.bool("b"));
		final Constraint intBEqZero = Constraint.of("int-b=0", SumTerms.of(1d, Variable.integer("b")),
				ComparisonOperator.EQ, 0d);
		try {
			mp.add(intBEqZero);
		} catch (IllegalArgumentException exc) {
			LOGGER.info("Adding constraint.", exc);
			throw exc;
		}
	}

}
