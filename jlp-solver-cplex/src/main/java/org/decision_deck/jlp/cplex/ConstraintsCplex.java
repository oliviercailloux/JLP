package org.decision_deck.jlp.cplex;

import static com.google.common.base.Preconditions.checkNotNull;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpOperator;
import org.decision_deck.jlp.LpTerm;
import org.decision_deck.jlp.parameters.LpObjectParameter;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.problem.LpProblems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;

public class ConstraintsCplex<V> extends AbstractSet<LpConstraint<V>> implements Set<LpConstraint<V>> {

	private final SolverExtCPLEX<V> m_solverExt;

	private final BiMap<Object, IloRange> m_constraintIdsToCplex = HashBiMap.create();

	/**
	 * Never <code>null</code>.
	 */
	private Function<LpConstraint<V>, String> m_constraintsNamer;

	Function<LpConstraint<V>, String> getConstraintsNamer() {
		assert m_constraintsNamer != null;
		return m_constraintsNamer;
	}

	/**
	 * <p>
	 * Sets the namer function that is used to associate names to constraints. If
	 * the given namer is <code>null</code>, the namer function is set back to the
	 * default function. The function is never given a <code>null</code> constraint;
	 * however the constraint id may be <code>null</code>.
	 * </p>
	 * 
	 * @param constraintsNamer
	 *            <code>null</code> to reset default behavior.
	 * @see #getConstraintsNamer()
	 * @see LpObjectParameter#NAMER_CONSTRAINTS
	 */
	void setConstraintsNamer(Function<LpConstraint<V>, String> constraintsNamer) {
		if (constraintsNamer == null) {
			m_constraintsNamer = new LpProblems.DefaultConstraintsNamer<V>();
		} else {
			m_constraintsNamer = constraintsNamer;
		}
	}

	public ConstraintsCplex(SolverExtCPLEX<V> solverExt) {
		m_solverExt = solverExt;
		m_constraintsNamer = new LpProblems.DefaultConstraintsNamer<V>();
	}

	@Override
	public Iterator<LpConstraint<V>> iterator() {
		final IloCplex cplex = m_solverExt.getUnderlying();
		final Iterator<?> rangeIterator = cplex.rangeIterator();
		return Iterators.transform(rangeIterator, new Function<Object, LpConstraint<V>>() {
			@Override
			public LpConstraint<V> apply(Object input) {
				final IloRange range = (IloRange) input;
				LpConstraint<V> constraint;
				try {
					constraint = toConstraint(range);
				} catch (IloException exc) {
					throw new IllegalStateException(exc);
				}
				return constraint;
			}
		});
	}

	@Override
	public int size() {
		return m_solverExt.getUnderlying().getNrows();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (!(o instanceof LpConstraint<?>)) {
			return false;
		}
		LpConstraint<?> constraint = (LpConstraint<?>) o;
		final Object id = constraint.getId();
		if (id == null) {
			throw new UnsupportedOperationException("Unsupported: checking for existency of a constraint with no id.");
		}
		return m_constraintIdsToCplex.containsKey(constraint);
	}

	@Override
	public Object[] toArray() {
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return super.toArray(a);
	}

	@Override
	public boolean add(LpConstraint<V> constraint) {
		try {
			return addThrowing(constraint);
		} catch (IloException exc) {
			throw new IllegalStateException(exc);
		}
	}

	private boolean addThrowing(LpConstraint<V> constraint) throws IloException {
		checkNotNull(constraint);
		final Object id = constraint.getId();
		if (id != null && m_constraintIdsToCplex.containsKey(id)) {
			return false;
		}
		/**
		 * TODO document that if id is null, no check for previously added constraint.
		 * Document that this object is not read-only!
		 */

		final String constraintName = m_constraintsNamer.apply(constraint);
		final IloRange added = addThrowing(constraint.getLhs(), constraint.getOperator(), constraint.getRhs(),
				constraintName);
		m_constraintIdsToCplex.put(id, added);
		s_logger.debug("Added constraint {}.", constraint);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (!(o instanceof LpConstraint<?>)) {
			return false;
		}
		final LpConstraint<?> constraint = (LpConstraint<?>) o;
		final Object id = constraint.getId();
		if (id == null) {
			throw new UnsupportedOperationException("Unsupported: removing a constraint with no id.");
		}
		if (!m_constraintIdsToCplex.containsKey(id)) {
			return false;
		}
		final IloRange toRemove = m_constraintIdsToCplex.remove(constraint);
		assert toRemove != null;
		try {
			m_solverExt.getUnderlying().remove(toRemove);
		} catch (IloException exc) {
			throw new IllegalStateException(exc);
		}
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return super.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends LpConstraint<V>> constraints) {
		return super.addAll(constraints);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return super.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return super.removeAll(c);
	}

	@Override
	public void clear() {
		super.clear();
	}

	/**
	 * Clears this set and reinitializes it as if it was created fresh. Note that
	 * the {@link #clear()} method only clears the set contents, as if everything
	 * was removed. This method has a stronger effect.
	 */
	void wipe() {
		clear();
		m_constraintsNamer = new LpProblems.DefaultConstraintsNamer<V>();
	}

	private IloRange addThrowing(LpLinear<V> lhs, LpOperator operator, double rhs, String constraintName)
			throws IloException {
		final IloCplex cplex = m_solverExt.getUnderlying();
		final IloLinearNumExpr lin = getAsCplex(lhs);

		final IloRange added;
		switch (operator) {
		case EQ:
			if (constraintName.isEmpty()) {
				added = cplex.addEq(lin, rhs);
			} else {
				added = cplex.addEq(lin, rhs, constraintName);
			}
			break;
		case GE:
			if (constraintName.isEmpty()) {
				added = cplex.addGe(lin, rhs);
			} else {
				added = cplex.addGe(lin, rhs, constraintName);
			}
			break;
		case LE:
			if (constraintName.isEmpty()) {
				added = cplex.addLe(lin, rhs);
			} else {
				added = cplex.addLe(lin, rhs, constraintName);
			}
			break;
		default:
			throw new IllegalStateException("Unknown operator.");
		}
		assert added != null;
		return added;
	}

	private static final Logger s_logger = LoggerFactory.getLogger(ConstraintsCplex.class);

	LpConstraint<V> toConstraint(final IloRange range) throws IloException {
		final double lb = range.getLB();
		final double ub = range.getUB();
		final IloNumExpr expr = range.getExpr();
		if (!(expr instanceof IloLinearNumExpr)) {
			throw new IllegalStateException("Unsupported non linear range: " + range + ".");
		}
		final IloLinearNumExpr linearExpr = (IloLinearNumExpr) expr;
		final LpLinear<V> linear = m_solverExt.toLinear(linearExpr);

		final LpConstraint<V> constraint;
		if (Double.isInfinite(lb) && !Double.isInfinite(ub)) {
			constraint = new LpConstraint<V>(null, linear, LpOperator.LE, ub);
		} else if (!Double.isInfinite(lb) && Double.isInfinite(ub)) {
			constraint = new LpConstraint<V>(null, linear, LpOperator.GE, lb);
		} else {
			throw new IllegalStateException("Unsupported constraint: " + range + ".");
		}
		return constraint;
	}

	IloLinearNumExpr getAsCplex(LpLinear<V> linear) throws IloException {
		IloLinearNumExpr lin = m_solverExt.getUnderlying().linearNumExpr();
		for (LpTerm<V> term : linear) {
			lin.addTerm(term.getCoefficient(), m_solverExt.getMP().getVariables().getIloVariable(term.getVariable()));
		}
		return lin;
	}

	void setConstraints(LpProblem<V> problem) throws IloException {
		for (LpConstraint<V> constraint : problem.getConstraints()) {
			addThrowing(constraint);
		}
	}

}
