package org.decision_deck.jlp.cplex;

import static com.google.common.base.Preconditions.checkNotNull;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;

import java.util.Iterator;
import java.util.Map.Entry;

class VariablesIterator<V> implements Iterator<V> {
    private final Iterator<Entry<V, IloNumVar>> m_delegate;
    private Entry<V, IloNumVar> m_current;
    private final SolverExtCPLEX<V> m_solver;

    public VariablesIterator(SolverExtCPLEX<V> solver, Iterator<Entry<V, IloNumVar>> delegate) {
	checkNotNull(delegate);
	m_solver = solver;
	m_delegate = delegate;
	m_current = null;
    }

    @Override
    public boolean hasNext() {
	return m_delegate.hasNext();
    }

    @Override
    public V next() {
	m_current = m_delegate.next();
	return m_current.getKey();
    }

    @Override
    public void remove() {
	if (m_current == null) {
	    throw new IllegalStateException("No current entry.");
	}
	try {
	    m_solver.getUnderlying().remove(m_current.getValue());
	} catch (IloException exc) {
	    throw new IllegalStateException(exc);
	}
	m_delegate.remove();
	m_current = null;
    }
}