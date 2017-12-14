package io.github.oliviercailloux.jlp;

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

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
 * @param <V>
 *            the type of the variables.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpLinearImpl<V> extends LinkedList<LpTerm<V>> implements LpLinear<V> {

	private static final long serialVersionUID = 1L;

	public LpLinearImpl() {
		/** Public no argument constructor. */
	}

	public LpLinearImpl(Collection<LpTerm<V>> terms) {
		super(terms);
	}

	@Override
	public void addTerm(double coefficient, V variable) {
		LpTerm<V> term = new LpTerm<V>(coefficient, variable);
		add(term);
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("size", "" + size());
		helper.add("expr", LpLinearUtils.getAsString(this));
		return helper.toString();
	}

}
