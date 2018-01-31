package io.github.oliviercailloux.jlp.elements;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.math.MathContext;

import com.google.common.base.MoreObjects;

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
		checkArgument(Double.isFinite(coefficient));
		this.coefficient = coefficient;
		this.variable = requireNonNull(variable);
	}

	/**
	 * A term equals another object <code>obj</code> iff <code>obj</code> is a term
	 * and they have equal coefficient and variable.
	 *
	 */
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
	 * Returns a string representation of this term (useful for debug).
	 *
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).addValue(coefficient).addValue(variable.getDescription()).toString();
	}
}
