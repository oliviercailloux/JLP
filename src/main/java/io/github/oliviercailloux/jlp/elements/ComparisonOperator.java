package io.github.oliviercailloux.jlp.elements;

/**
 * A boolean operator that can be used to relate the left hand side and the
 * right hand side in a constraint.
 *
 * @author Olivier Cailloux
 *
 */
public enum ComparisonOperator {

	/**
	 * Less or equal.
	 */
	LE,
	/**
	 * Equal.
	 */
	EQ,
	/**
	 * Greater or equal.
	 */
	GE;

	/**
	 * Returns a representation of this operator as a string in Ascii: “<=”, “=” or
	 * “>=”.
	 *
	 * @return not <code>null</code>.
	 * @see #toString()
	 */
	public String toAsciiString() {
		switch (this) {
		case LE:
			return "<=";
		case GE:
			return ">=";
		case EQ:
			return "=";
		default:
			throw new IllegalStateException("Unknown operator.");
		}
	}

	/**
	 * Returns a representation of this operator as a one-character string: “≤”, “=”
	 * or “≥”.
	 *
	 * @return not <code>null</code>.
	 * @see #toAsciiString()
	 */
	@Override
	public String toString() {
		switch (this) {
		case LE:
			return "≤";
		case GE:
			return "≥";
		case EQ:
			return "=";
		default:
			throw new IllegalStateException("Unknown operator.");
		}
	}

}
