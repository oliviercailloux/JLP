package io.github.oliviercailloux.jlp.elements;

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Joiner;

import io.github.oliviercailloux.jlp.utils.LpLinearUtils;

/**
 * <p>
 * A simple mutable implementation of {@link LpLinear} based on a
 * {@link LinkedList}.
 * </p>
 * <p>
 * It shouldn't be necessary to access this class directly. It is recommanded to
 * create linear expressions through the methods available in
 * {@link LpLinearUtils}.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class LpLinearImpl extends LinkedList<LpTerm> implements LpLinear {

	private static final long serialVersionUID = 1L;

	public LpLinearImpl() {
		/** Public no argument constructor. */
	}

	public LpLinearImpl(Collection<LpTerm> terms) {
		super(terms);
	}

	@Override
	public void addTerm(double coefficient, Variable variable) {
		LpTerm term = new LpTerm(coefficient, variable);
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
