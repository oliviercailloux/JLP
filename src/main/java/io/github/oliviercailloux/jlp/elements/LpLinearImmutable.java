package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

public class LpLinearImmutable extends ForwardingList<LpTerm> implements LpLinear {
	/**
	 * Retrieves a linear, immutable object containing the given terms.
	 *
	 * @param terms
	 *            not <code>null</code>.
	 * @return a linear object.
	 */
	static public LpLinearImmutable of(Collection<LpTerm> terms) {
		requireNonNull(terms);
		if (terms instanceof LpLinearImmutable) {
			return (LpLinearImmutable) terms;
		}
		return new LpLinearImmutable(terms);
	}

	static public LpLinearImmutable of(double c1, Variable v1) {
		final List<LpTerm> asList = Lists.newLinkedList();
		asList.add(new LpTerm(c1, v1));
		return new LpLinearImmutable(asList);
	}

	static public LpLinearImmutable of(double c1, Variable v1, double c2, Variable v2) {
		final List<LpTerm> asList = Lists.newLinkedList();
		asList.add(new LpTerm(c1, v1));
		asList.add(new LpTerm(c2, v2));
		return new LpLinearImmutable(asList);
	}

	static public LpLinearImmutable of(double c1, Variable v1, double c2, Variable v2, double c3, Variable v3) {
		final List<LpTerm> asList = Lists.newLinkedList();
		asList.add(new LpTerm(c1, v1));
		asList.add(new LpTerm(c2, v2));
		asList.add(new LpTerm(c3, v3));
		return new LpLinearImmutable(asList);
	}

	static public LpLinearImmutable of(LpTerm... terms) {
		/**
		 * TODO Inverse: immutable should be the default; add a builder for convenience.
		 */
		return new LpLinearImmutable(Arrays.asList(terms));
	}

	private final LpLinear m_delegate;

	public LpLinearImmutable(Collection<LpTerm> terms) {
		m_delegate = new LpLinearImpl(terms);
	}

	/**
	 * @param source
	 *            not <code>null</code>.
	 */
	public LpLinearImmutable(LpLinear source) {
		Preconditions.checkNotNull(source);
		if (source instanceof LpLinearImmutable) {
			m_delegate = source;
		} else {
			m_delegate = new LpLinearImpl(source);
		}
	}

	@Override
	public void addTerm(double coefficient, Variable variable) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	@Override
	protected List<LpTerm> delegate() {
		return Collections.unmodifiableList(m_delegate);
	}
}
