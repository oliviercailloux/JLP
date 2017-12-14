package org.decision_deck.jlp.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpLinearImmutable;
import org.decision_deck.jlp.LpLinearImpl;
import org.decision_deck.jlp.LpTerm;
import org.decision_deck.jlp.LpVariable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public class LpLinearUtils {
	/**
	 * Computes the result of the given linear expression with the given values
	 * assigned to the variables in the expression. The numbers used for the
	 * variable values are converted to double for the computation.
	 * 
	 * @param <V>
	 *            the type of the variables.
	 * @param linear
	 *            not <code>null</code>.
	 * @param values
	 *            not <code>null</code>, no <code>null</code> keys or values.
	 * @return <code>null</code> iff at least one of the variables used in the
	 *         linear expression has no associated value, zero if the given linear
	 *         is empty.
	 */
	static public <V> Double evaluate(LpLinear<V> linear, Map<V, Number> values) {
		Preconditions.checkNotNull(linear);
		Preconditions.checkNotNull(values);

		double expr = 0d;
		for (LpTerm<V> term : linear) {
			final V variable = term.getVariable();
			if (!values.containsKey(variable)) {
				return null;
			}
			final double value = values.get(variable).doubleValue();
			expr += term.getCoefficient() * value;
		}
		return Double.valueOf(expr);
	}

	/**
	 * Returns a string representation of the given linear expression as a
	 * mathematical representation where the variables string form (using
	 * {@link LpVariable#toString()}) is used.
	 * 
	 * @param linear
	 *            not <code>null</code>.
	 * @return not <code>null</code>, may be empty.
	 */
	static public <V> String getAsString(LpLinear<V> linear) {
		checkNotNull(linear);
		final Iterable<String> termsToStrings = Iterables.transform(linear, new Function<LpTerm<V>, String>() {
			@Override
			public String apply(LpTerm<V> term) {
				if (term.getCoefficient() == 1d) {
					return term.getVariable().toString();
				}
				if (term.getCoefficient() == -1d) {
					return "−" + term.getVariable().toString();
				}
				return term.getCoefficient() + "×" + term.getVariable().toString();
			}
		});

		return Joiner.on(" + ").join(termsToStrings);
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
	static public <V> LpLinear<V> newMult(double factor, Collection<LpTerm<V>> source) {
		checkNotNull(source);
		checkArgument(Doubles.isFinite(factor));
		final LpLinearImpl<V> result = new LpLinearImpl<V>();
		for (LpTerm<V> term : source) {
			result.addTerm(factor * term.getCoefficient(), term.getVariable());
		}
		return result;
	}

	/**
	 * Retrieves a linear, immutable object containing the given terms.
	 * 
	 * @param terms
	 *            not <code>null</code>.
	 * @return a linear object.
	 */
	static public <V> LpLinearImmutable<V> newImmutable(Collection<LpTerm<V>> terms) {
		checkNotNull(terms);
		if (terms instanceof LpLinearImmutable) {
			return (LpLinearImmutable<V>) terms;
		}
		return new LpLinearImmutable<V>(terms);
	}

	/**
	 * Retrieves a new, mutable object representing an empty linear expression.
	 * 
	 * @return a new linear object.
	 */
	static public <V> LpLinear<V> newLinear() {
		return new LpLinearImpl<V>();
	}

	/**
	 * Retrieves a linear, mutable object containing the given terms.
	 * 
	 * @param terms
	 *            not <code>null</code>.
	 * @return a linear object.
	 */
	static public <V> LpLinear<V> newLinear(Collection<LpTerm<V>> terms) {
		checkNotNull(terms);
		return new LpLinearImpl<V>(terms);
	}

	static public <V> LpLinearImmutable<V> newImmutable(LpTerm<V>... terms) {
		return new LpLinearImmutable<V>(Arrays.asList(terms));
	}

	static public <V> LpLinearImmutable<V> newImmutable(double c1, V v1, double c2, V v2) {
		final List<LpTerm<V>> asList = Lists.newLinkedList();
		asList.add(new LpTerm<V>(c1, v1));
		asList.add(new LpTerm<V>(c2, v2));
		return new LpLinearImmutable<V>(asList);
	}

	static public <V> LpLinearImmutable<V> newImmutable(double c1, V v1) {
		final List<LpTerm<V>> asList = Lists.newLinkedList();
		asList.add(new LpTerm<V>(c1, v1));
		return new LpLinearImmutable<V>(asList);
	}

	static public <V> LpLinearImmutable<V> newImmutable(double c1, V v1, double c2, V v2, double c3, V v3) {
		final List<LpTerm<V>> asList = Lists.newLinkedList();
		asList.add(new LpTerm<V>(c1, v1));
		asList.add(new LpTerm<V>(c2, v2));
		asList.add(new LpTerm<V>(c3, v3));
		return new LpLinearImmutable<V>(asList);
	}
}
