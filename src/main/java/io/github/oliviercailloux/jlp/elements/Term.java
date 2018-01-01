package io.github.oliviercailloux.jlp.elements;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.math.MathContext;

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
		final String formattedCoef;

		if (getCoefficient() == 1d) {
			formattedCoef = "";
		} else if (getCoefficient() == -1d) {
			formattedCoef = "-";
		} else {
			/**
			 * Using DecimalFormat I get a suffix of E0 when the decimal exponent is zero,
			 * which I do not want.
			 */
//		final DecimalFormat formatter = new DecimalFormat("0.##E0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
//		formatter.applyPattern("#.##E0");
//		final String formattedCoef = formatter.format(coefficient);

			/**
			 * TODO think about a better representation. Use space efficiently, 3 digits
			 * precision is enough. We want to print 0.5d as 0.5 and 0.50000001d as 0.500.
			 * We want to use E notation only if necessary or clearer (smaller than 0.1,
			 * scale 2 or more; ≥ 1E6). Use thousands separator for big numbers. Better
			 * write 100 001 than 1.00E+5 (both taking 7 chars anyway). Write 1E+6 instead
			 * of 1.00E+6 when value is exactly 1E+6.
			 */
			final MathContext c = new MathContext(3);
			final BigDecimal cD = new BigDecimal(coefficient);
			formattedCoef = cD.round(c).toString() + ' ';
		}

		return formattedCoef + variable.toString();
	}
}
