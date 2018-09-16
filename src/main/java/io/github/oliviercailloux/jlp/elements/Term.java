package io.github.oliviercailloux.jlp.elements;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

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
	/**
	 * Returns a term.
	 * 
	 * @param coefficient a finite floating-point value.
	 * @param variable    not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	public static Term of(double coefficient, Variable variable) {
		return new Term(coefficient, variable);
	}

	private final double coefficient;

	private final Variable variable;

	/**
	 * @param coefficient a finite number.
	 * @param variable    not <code>null</code>.
	 */
	private Term(double coefficient, Variable variable) {
		checkArgument(Double.isFinite(coefficient));
		this.coefficient = coefficient;
		this.variable = requireNonNull(variable);
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

	/**
	 * A term equals another object <code>o2</code> iff <code>o2</code> is a term
	 * and they have equal coefficient and variable.
	 *
	 */
	@Override
	public boolean equals(Object o2) {
		if (this == o2) {
			return true;
		}
		if (!(o2 instanceof Term)) {
			return false;
		}

		Term t2 = (Term) o2;
		if (coefficient != t2.coefficient) {
			return false;
		}
		if (!variable.equals(t2.variable)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(coefficient, variable);
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
