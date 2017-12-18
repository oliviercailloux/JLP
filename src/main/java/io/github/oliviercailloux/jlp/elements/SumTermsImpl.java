package io.github.oliviercailloux.jlp.elements;

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Joiner;

import io.github.oliviercailloux.jlp.utils.SumTermUtils;

/**
 * <p>
 * A simple mutable implementation of {@link SumTerms} based on a
 * {@link LinkedList}.
 * </p>
 * <p>
 * It shouldn't be necessary to access this class directly. It is recommanded to
 * create linear expressions through the methods available in
 * {@link SumTermUtils}.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class SumTermsImpl extends LinkedList<Term> implements SumTerms {

	private static final long serialVersionUID = 1L;

	public SumTermsImpl() {
		/** Public no argument constructor. */
	}

	public SumTermsImpl(Collection<Term> terms) {
		super(terms);
	}

	@Override
	public void addTerm(double coefficient, Variable variable) {
		Term term = new Term(coefficient, variable);
		add(term);
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

}
