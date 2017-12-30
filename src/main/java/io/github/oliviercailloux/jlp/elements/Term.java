package io.github.oliviercailloux.jlp.elements;

import com.google.common.base.Preconditions;

/**
 * <p>
 * A term in a linear expression in a linear program or mixed integer program,
 * consisting in a coefficient multiplying a variable.
 * </p>
 * <p>
 * Terms are immutable (provided variables are).
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Term {
	static public Term of(double coefficient, Variable variable) {
		return new Term(coefficient, variable);
	}

	private final double coefficient;

	private final Variable variable;

	/**
	 * @param coefficient
	 *            a finite number.
	 * @param variable
	 *            not <code>null</code>.
	 */
	private Term(double coefficient, Variable variable) {
		Preconditions.checkNotNull(variable);
		Preconditions.checkArgument(Double.isFinite(coefficient));
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
	 * @return a finite number.
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
