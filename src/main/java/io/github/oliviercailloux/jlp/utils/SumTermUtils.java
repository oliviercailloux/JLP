package io.github.oliviercailloux.jlp.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;

import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsImpl;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.jlp.elements.Variable;

public class SumTermUtils {
	/**
	 * Computes the result of the given linear expression with the given values
	 * assigned to the variables in the expression. The numbers used for the
	 * variable values are converted to double for the computation.
	 *
	 * @param linear
	 *            not <code>null</code>.
	 * @param values
	 *            not <code>null</code>, no <code>null</code> keys or values.
	 * @return <code>null</code> iff at least one of the variables used in the
	 *         linear expression has no associated value, zero if the given linear
	 *         is empty.
	 */
	static public Double evaluate(SumTerms linear, Map<Variable, Number> values) {
		Preconditions.checkNotNull(linear);
		Preconditions.checkNotNull(values);

		double expr = 0d;
		for (Term term : linear) {
			final Variable variable = term.getVariable();
			if (!values.containsKey(variable)) {
				return null;
			}
			/** TODO change to double. */
			final double value = values.get(variable).doubleValue();
			expr += term.getCoefficient() * value;
		}
		return Double.valueOf(expr);
	}

	/**
	 * Retrieves a new, mutable object representing an empty linear expression.
	 *
	 * @return a new linear object.
	 */
	static public SumTerms newLinear() {
		return new SumTermsImpl();
	}

	/**
	 * Retrieves a linear, mutable object containing the given terms.
	 *
	 * @param terms
	 *            not <code>null</code>.
	 * @return a linear object.
	 */
	static public SumTerms newLinear(Collection<Term> terms) {
		checkNotNull(terms);
		return new SumTermsImpl(terms);
	}

	/**
	 * <p>
	 * Returns a new linear expression, proportional to the given one. The new one
	 * equals the given factor times the source one, thus all of its terms have a
	 * coefficient that is equal to the source coefficient multiplied by the given
	 * factor. This implies that the returned object may contain terms with a
	 * coefficient equal to zero, either because of the given factor is zero,
	 * because the source contains terms with a coefficient of zero, or because of
	 * imprecision of the floating point multiplication.
	 * </p>
	 * <p>
	 * The returned object keeps no reference to the source collection.
	 * </p>
	 *
	 * @param factor
	 *            a real number, not infinite.
	 * @param source
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public SumTerms newMult(double factor, Collection<Term> source) {
		checkNotNull(source);
		checkArgument(Doubles.isFinite(factor));
		final SumTermsImpl result = new SumTermsImpl();
		for (Term term : source) {
			result.addTerm(factor * term.getCoefficient(), term.getVariable());
		}
		return result;
	}
}
