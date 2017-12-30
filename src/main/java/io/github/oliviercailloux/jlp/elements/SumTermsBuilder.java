package io.github.oliviercailloux.jlp.elements;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

/**
 * <p>
 * An object to build {@link SumTerms}.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class SumTermsBuilder extends ForwardingList<Term> implements List<Term> {

	private final List<Term> delegate;

	SumTermsBuilder() {
		delegate = Lists.newArrayList();
	}

	SumTermsBuilder(Iterable<Term> terms) {
		delegate = Lists.newArrayList(terms);
	}

	/**
	 * Adds a term to this linear expression.
	 *
	 * @param coefficient
	 *            a finite number.
	 * @param variable
	 *            not <code>null</code>.
	 */
	public SumTermsBuilder addTerm(double coefficient, Variable variable) {
		final Term term = Term.of(coefficient, variable);
		add(term);
		return this;
	}

	public SumTerms build() {
		return SumTerms.of(this);
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

	@Override
	protected List<Term> delegate() {
		return delegate;
	}

}
