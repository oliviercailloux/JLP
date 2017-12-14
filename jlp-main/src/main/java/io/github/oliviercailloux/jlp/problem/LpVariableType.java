package io.github.oliviercailloux.jlp.problem;

/**
 * Associated with a variable, defines the range of values the variable may
 * take.
 *
 * @author Olivier Cailloux
 *
 */
public enum LpVariableType {

	/**
	 * Boolean: a zero or one value.
	 */
	BOOL,
	/**
	 * Integer
	 */
	INT,
	/**
	 * Real
	 */
	REAL;

	/**
	 * Indicates whether this type represents an integer type <em>in the large
	 * sense</em>.
	 * 
	 * @return <code>true</code> iff this type is {@link #BOOL} or {@link #INT}.
	 */
	public boolean isInt() {
		switch (this) {
		case BOOL:
		case INT:
			return true;
		case REAL:
			return false;
		default:
			throw new IllegalStateException("Unknown type.");
		}
	}
}
