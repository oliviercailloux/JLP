package io.github.oliviercailloux.jlp.cplex;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import io.github.oliviercailloux.jlp.problem.LpProblem;
import io.github.oliviercailloux.jlp.problem.LpVariableType;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class VariablesCplex<V> extends AbstractSet<V> implements Set<V> {

	private final SolverExtCPLEX<V> m_solver;

	private final BiMap<V, IloNumVar> m_variablesToCplex = HashBiMap.create();

	private Function<? super V, String> m_variableNamer;

	IloNumVar getIloVariable(V variable) {
		final IloNumVar iloNumVar = m_variablesToCplex.get(variable);
		checkArgument(iloNumVar != null);
		return iloNumVar;
	}

	public VariablesCplex(SolverExtCPLEX<V> solver) {
		checkNotNull(solver);
		m_solver = solver;
		m_variableNamer = LpProblem.TO_STRING_NAMER;
	}

	@Override
	public int size() {
		return m_solver.getUnderlying().getNcols();
	}

	int getNumberBinaries() {
		return m_solver.getUnderlying().getNbinVars();
	}

	int getNumberContinuous() {
		return m_solver.getUnderlying().getNcols() - getNumberBinaries() - getNumberIntegerNonBinaries();
	}

	int getNumberIntegerNonBinaries() {
		// TODO check that this does not include binaries.
		return m_solver.getUnderlying().getNintVars();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return m_variablesToCplex.containsKey(o);
	}

	@Override
	public Iterator<V> iterator() {
		return new VariablesIterator<V>(m_solver, m_variablesToCplex.entrySet().iterator());
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
	public boolean add(V variable) {
		try {
			return addVariable(variable, IloNumVarType.Float, 0, Double.POSITIVE_INFINITY);
		} catch (IloException exc) {
			throw new IllegalStateException(exc);
		}
	}

	boolean addVariable(V variable, IloNumVarType type, double lb, double ub) throws IloException {
		if (m_variablesToCplex.containsKey(variable)) {
			return false;
		}
		final IloNumVar num;
		final IloCplex cplex = m_solver.getUnderlying();
		final String varName = m_variableNamer.apply(variable);
		if (varName.isEmpty()) {
			num = cplex.numVar(lb, ub, type);
		} else {
			num = cplex.numVar(lb, ub, type, varName);
		}

		cplex.add(num);
		s_logger.debug("Set variable {} with bounds " + lb + ", " + ub + ", type " + type + ", name " + varName + ".",
				variable);
		m_variablesToCplex.put(variable, num);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		final IloNumVar toRemove = m_variablesToCplex.remove(o);
		if (toRemove == null) {
			return false;
		}
		try {
			m_solver.getUnderlying().remove(toRemove);
		} catch (IloException exc) {
			throw new IllegalStateException(exc);
		}
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return m_variablesToCplex.keySet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		return super.addAll(c);
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
		assert m_variablesToCplex.isEmpty();
	}

	public IloNumVarType setVariable(V variable, LpVariableType varType, double lb, double ub) throws IloException {
		final IloCplex cplex = m_solver.getUnderlying();
		final String varName = m_variableNamer.apply(variable);

		final IloNumVar num;
		final IloNumVarType type;
		switch (varType) {
		case BOOL:
			type = IloNumVarType.Bool;
			break;
		case INT:
			type = IloNumVarType.Int;
			break;
		case REAL:
			type = IloNumVarType.Float;
			break;
		default:
			throw new IllegalStateException("Unexpected type.");
		}
		if (varName.isEmpty()) {
			num = cplex.numVar(lb, ub, type);
		} else {
			num = cplex.numVar(lb, ub, type, varName);
		}
		cplex.add(num);
		s_logger.debug("Set variable {} with bounds " + lb + ", " + ub + ", type " + type + ", name " + varName + ".",
				variable);

		m_variablesToCplex.put(variable, num);
		return type;
	}

	/**
	 * Clears this set and reinitializes it as if it was created fresh. Note that
	 * the {@link #clear()} method only clears the set contents, as if everything
	 * was removed. This method has a stronger effect.
	 */
	void wipe() {
		clear();
		m_variableNamer = LpProblem.TO_STRING_NAMER;
	}

	private static final Logger s_logger = LoggerFactory.getLogger(VariablesCplex.class);

	Number getLowerBound(V variable) {
		return m_solver.getUnderlying().getLowerBound(variable);
	}

}
