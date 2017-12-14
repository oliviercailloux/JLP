package io.github.oliviercailloux.jlp;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingList;

public class LpLinearImmutable<V> extends ForwardingList<LpTerm<V>> implements LpLinear<V> {
	private final LpLinear<V> m_delegate;

	public LpLinearImmutable(Collection<LpTerm<V>> terms) {
		m_delegate = new LpLinearImpl<V>(terms);
	}

	/**
	 * @param source
	 *            not <code>null</code>.
	 */
	public LpLinearImmutable(LpLinear<V> source) {
		Preconditions.checkNotNull(source);
		if (source instanceof LpLinearImmutable) {
			m_delegate = source;
		} else {
			m_delegate = new LpLinearImpl<V>(source);
		}
	}

	@Override
	public void addTerm(double coefficient, V variable) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	@Override
	protected List<LpTerm<V>> delegate() {
		return Collections.unmodifiableList(m_delegate);
	}
}
