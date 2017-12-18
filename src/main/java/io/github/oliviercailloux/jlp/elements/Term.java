package io.github.oliviercailloux.jlp.elements;

import com.google.common.base.Preconditions;

/**
 * <p>
 * A term in a linear expression in a linear program or mixed integer program,
 * consisting of a coefficient multiplying a variable.
 * </p>
 * <p>
 * Such terms should be used with immutable variables, and should be considered
 * as immutable. An object of this class is indeed immutable iff the variable is
 * immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Term {
	private final double coefficient;

	private final Variable variable;

	/**
	 * @param coefficient
	 *            a valid number.
	 * @param variable
	 *            not <code>null</code>.
	 */
	public Term(double coefficient, Variable variable) {
		Preconditions.checkNotNull(variable);
		Preconditions.checkArgument(!Double.isInfinite(coefficient));
		Preconditions.checkArgument(!Double.isNaN(coefficient));
		this.coefficient = coefficient;
		this.variable = variable;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Term)) {
			return false;
		}

		Term t2 = (Term) obj;
		if (coefficient != t2.coefficient) {
			return false;
		}
		if (!variable.equals(t2.variable)) {
			return false;
		}

		return true;
	}

	/**
	 * Retrieves the coefficient that multiplies the variable in this term.
	 *
	 * @return a valid number.
	 */
	public double getCoefficient() {
		return coefficient;
	}

	/**
	 * Retrieves the variable of this term.
	 *
	 * @return not <code>null</code>.
	 */
	public Variable getVariable() {
		return variable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(coefficient);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + variable.hashCode();
		return result;
	}

	/**
	 * Returns a string representation of this term.
	 *
	 * @return not <code>null</code>, not empty
	 */
	@Override
	public String toString() {
		if (getCoefficient() == 1d) {
			return getVariable().toString();
		}
		if (getCoefficient() == -1d) {
			return "−" + getVariable().toString();
		}
		return getCoefficient() + "×" + getVariable().toString();
	}
}
