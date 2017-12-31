package io.github.oliviercailloux.jlp.elements;

import static io.github.oliviercailloux.jlp.elements.VariableDomain.INT_DOMAIN;
import static io.github.oliviercailloux.jlp.elements.VariableDomain.REAL_DOMAIN;

/**
 * Associated to a {@link Variable}, indicates which kind of variable it is.
 *
 * @author Olivier Cailloux
 *
 */
public enum VariableKind {

	/**
	 * Boolean
	 */
	BOOL_KIND,
	/**
	 * Integer non boolean
	 */
	INT_KIND,
	/**
	 * Real
	 */
	REAL_KIND;

	public VariableDomain getDomain() {
		switch (this) {
		case BOOL_KIND:
			return INT_DOMAIN;
		case INT_KIND:
			return INT_DOMAIN;
		case REAL_KIND:
			return REAL_DOMAIN;
		default:
			throw new IllegalStateException();
		}
	}
}
