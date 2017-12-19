package io.github.oliviercailloux.jlp.elements;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
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

	private static final long serialVersionUID = 1L;

	private final List<Term> delegate;

	SumTermsBuilder() {
		/** Public no argument constructor. */
		delegate = Lists.newLinkedList();
	}

	SumTermsBuilder(Collection<Term> terms) {
		delegate = Lists.newLinkedList(terms);
	}

	/**
	 * Adds a term to this linear expression.
	 *
	 * @param coefficient
	 *            a valid double.
	 * @param variable
	 *            not <code>null</code>.
	 */
	public SumTermsBuilder addTerm(double coefficient, Variable variable) {
		final Term term = new Term(coefficient, variable);
		add(term);
		return this;
	}

	public SumTerms build() {
		return SumTerms.of(this);
	}

	/**
	 * Returns a string representation of the given linear expression.
	 *
	 * @param linear
	 *            not <code>null</code>
	 * @return not <code>null</code>, may be empty
	 */
	@Override
	public String toString() {
		return Joiner.on(" + ").join(this);
	}

	@Override
	protected List<Term> delegate() {
		return delegate;
	}

}
