package io.github.oliviercailloux.jlp.elements;

/**
 * Two variables should be equal, as determined by {@link #equals(Object)}, iff
 * they have the same description, as given by {@link #toString()}.
 *
 * It is suggested that {@link #toString()} returns a short description unique
 * to that variable (in problems in which the variable will appear). For
 * example, "c-p1" for a variable representing the cost of the product number 1.
 *
 * It is expected that this object be immutable. (It will be referred to in
 * solutions of problems.) Hence, the bounds (or type) of this variable should
 * be considered as a structural property of the variable, that will never
 * change.
 *
 * @author Olivier Cailloux
 *
 */
public interface Variable {
	/**
	 * Indicates whether the given object represents the same variable as this one.
	 *
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> iff this variable represents the same variable as
	 *         the obj argument.
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * @return minus infinity, for a lower bound equal to minus infinity, may not be
	 *         positive infinity.
	 */
	public Number getLowerBound();

	public VariableType getType();

	/**
	 * @return positive infinity, for an upper bound equal to positive infinity, may
	 *         not be negative infinity, may not be smaller than the lower bound.
	 */
	public Number getUpperBound();

	/**
	 * Returns a description of this variable.
	 *
	 * @return not <code>null</code>.
	 */
	@Override
	public String toString();
}
