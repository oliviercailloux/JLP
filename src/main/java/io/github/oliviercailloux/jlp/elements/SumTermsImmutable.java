package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

public class SumTermsImmutable extends ForwardingList<Term> implements SumTerms {
	/**
	 * Retrieves a linear, immutable object containing the given terms.
	 *
	 * @param terms
	 *            not <code>null</code>.
	 * @return a linear object.
	 */
	static public SumTermsImmutable of(Collection<Term> terms) {
		requireNonNull(terms);
		if (terms instanceof SumTermsImmutable) {
			return (SumTermsImmutable) terms;
		}
		return new SumTermsImmutable(terms);
	}

	static public SumTermsImmutable of(double c1, Variable v1) {
		final List<Term> asList = Lists.newLinkedList();
		asList.add(new Term(c1, v1));
		return new SumTermsImmutable(asList);
	}

	static public SumTermsImmutable of(double c1, Variable v1, double c2, Variable v2) {
		final List<Term> asList = Lists.newLinkedList();
		asList.add(new Term(c1, v1));
		asList.add(new Term(c2, v2));
		return new SumTermsImmutable(asList);
	}

	static public SumTermsImmutable of(double c1, Variable v1, double c2, Variable v2, double c3, Variable v3) {
		final List<Term> asList = Lists.newLinkedList();
		asList.add(new Term(c1, v1));
		asList.add(new Term(c2, v2));
		asList.add(new Term(c3, v3));
		return new SumTermsImmutable(asList);
	}

	static public SumTermsImmutable of(Term... terms) {
		/**
		 * TODO Inverse: immutable should be the default; add a builder for convenience.
		 */
		return new SumTermsImmutable(Arrays.asList(terms));
	}

	private final SumTerms delegate;

	public SumTermsImmutable(Collection<Term> terms) {
		delegate = new SumTermsImpl(terms);
	}

	/**
	 * @param source
	 *            not <code>null</code>.
	 */
	public SumTermsImmutable(SumTerms source) {
		Preconditions.checkNotNull(source);
		if (source instanceof SumTermsImmutable) {
			delegate = source;
		} else {
			delegate = new SumTermsImpl(source);
		}
	}

	@Override
	public void addTerm(double coefficient, Variable variable) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	@Override
	protected List<Term> delegate() {
		return Collections.unmodifiableList(delegate);
	}
}
