package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

/**
 * <p>
 * A linear expression consisting of a possibly empty sum of terms, where a term
 * is a coefficient multiplying a variable. A linear object
 * {@link #equals(Object)} an other one iff they contain the same terms in the
 * same order.
 * </p>
 * <p>
 * This object allows for zero coefficients. It might be useful to store the
 * structure a constraint of the kind ax+by, even though a might (on a given
 * instance) equal zero.
 * <p>
 * Immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class SumTerms extends ForwardingList<Term> {
	static public SumTermsBuilder builder() {
		return new SumTermsBuilder();
	}

	/**
	 * Returns a sum containing a single term.
	 *
	 * @param c1
	 *            a finite number.
	 * @param v1
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public SumTerms of(double c1, Variable v1) {
		return new SumTerms(ImmutableList.of(Term.of(c1, v1)));
	}

	/**
	 * Returns a sum containing two terms.
	 *
	 * @param c1
	 *            a finite number.
	 * @param v1
	 *            not <code>null</code>.
	 * @param c2
	 *            a finite number.
	 * @param v2
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public SumTerms of(double c1, Variable v1, double c2, Variable v2) {
		return new SumTerms(ImmutableList.of(Term.of(c1, v1), Term.of(c2, v2)));
	}

	/**
	 * Returns a sum containing three terms.
	 *
	 * @param c1
	 *            a finite number.
	 * @param v1
	 *            not <code>null</code>.
	 * @param c2
	 *            a finite number.
	 * @param v2
	 *            not <code>null</code>.
	 * @param c3
	 *            a finite number.
	 * @param v3
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public SumTerms of(double c1, Variable v1, double c2, Variable v2, double c3, Variable v3) {
		return new SumTerms(ImmutableList.of(Term.of(c1, v1), Term.of(c2, v2), Term.of(c3, v3)));
	}

	/**
	 * Returns a sum containing the given terms.
	 *
	 * @param terms
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public SumTerms of(Iterable<Term> terms) {
		requireNonNull(terms);
		if (terms instanceof SumTerms) {
			return (SumTerms) terms;
		}
		return new SumTerms(terms);
	}

	/**
	 * Returns a sum containing the given terms.
	 *
	 * @param terms
	 *            the terms.
	 * @return not <code>null</code>.
	 */
	static public SumTerms of(Term... terms) {
		return new SumTerms(Arrays.asList(terms));
	}

	private final ImmutableList<Term> delegate;

	private ImmutableList<Variable> variables;

	private SumTerms(Iterable<Term> terms) {
		delegate = ImmutableList.copyOf(terms);
		initVariables();
	}

	/**
	 * Returns the terms composing this object. The only reason to use this method
	 * is to get a reference to a list of terms that is explicitely typed as
	 * Immutable. The returned list contains the same information as that present in
	 * this object.
	 *
	 * @return not <code>null</code>.
	 */
	public ImmutableList<Term> asImmutableList() {
		return delegate;
	}

	/**
	 * Returns the variables used in this sum, with duplicates iff multiple terms
	 * use the same variable.
	 *
	 * @return a list that has the same size as this list.
	 *
	 */
	public ImmutableList<Variable> getVariables() {
		return variables;
	}

	/**
	 * Returns a string representation of this sum. This should be used for debug
	 * purposes only as this method gives no control on the number of decimal digits
	 * shown.
	 *
	 */
	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.addValue(Joiner.on(" + ").join(this));
		return helper.toString();
	}

	private void initVariables() {
		variables = this.stream().map(Term::getVariable)
				.collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
	}

	@Override
	protected List<Term> delegate() {
		return Collections.unmodifiableList(delegate);
	}
}
