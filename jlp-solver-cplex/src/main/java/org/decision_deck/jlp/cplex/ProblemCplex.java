package org.decision_deck.jlp.cplex;

import static com.google.common.base.Preconditions.checkNotNull;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpDirection;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpObjective;
import org.decision_deck.jlp.LpOperator;
import org.decision_deck.jlp.problem.LpDimension;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.problem.LpVariableType;
import org.decision_deck.jlp.utils.LpSolverUtils;

import com.google.common.base.Function;

class ProblemCplex<V> implements LpProblem<V> {
	private final SolverExtCPLEX<V> m_solver;

	private final ConstraintsCplex<V> m_constraintsCplex;

	private final VariablesCplex<V> m_variablesCplex;

	public ProblemCplex(SolverExtCPLEX<V> underlyingSolver) {
		m_solver = underlyingSolver;
		m_constraintsCplex = new ConstraintsCplex<V>(m_solver);
		m_variablesCplex = new VariablesCplex<V>(m_solver);
	}

	@Override
	public boolean add(LpConstraint<V> constraint) {
		return m_constraintsCplex.add(constraint);
	}

	@Override
	public boolean add(Object id, LpLinear<V> lhs, LpOperator operator, double rhs) {
		return m_constraintsCplex.add(new LpConstraint<V>(id, lhs, operator, rhs));
	}

	@Override
	public boolean addVariable(V variable) {
		return m_variablesCplex.add(variable);
	}

	@Override
	public void clear() {
		m_constraintsCplex.wipe();
		m_variablesCplex.wipe();
	}

	@Override
	public ConstraintsCplex<V> getConstraints() {
		return m_constraintsCplex;
	}

	@Override
	public LpDimension getDimension() {
		return new LpDimension(m_variablesCplex.getNumberBinaries(), m_variablesCplex.getNumberIntegerNonBinaries(),
				m_variablesCplex.getNumberContinuous(), m_constraintsCplex.size());
	}

	@Override
	public String getName() {
		// TODO check what happens when there's no name.
		final String name = m_solver.getUnderlying().getName();
		checkNotNull(name);
		return name;
	}

	@Override
	public LpObjective<V> getObjective() {
		try {
			return getObjectiveThrowing();
		} catch (IloException exc) {
			throw new IllegalStateException(exc);
		}
	}

	LpObjective<V> getObjectiveThrowing() throws IloException {
		final IloCplex cplex = m_solver.getUnderlying();
		final IloObjective iloObj = cplex.getObjective();
		if (iloObj == null) {
			return new LpObjective<V>(null, null);
		}
		final IloNumExpr expr = iloObj.getExpr();
		if (!(expr instanceof IloLinearNumExpr)) {
			throw new IllegalStateException("Unsupported non linear objective: " + expr + ".");
		}
		final IloLinearNumExpr linearExpr = (IloLinearNumExpr) expr;
		final LpLinear<V> linear = m_solver.toLinear(linearExpr);

		final IloObjectiveSense iloSense = iloObj.getSense();
		final LpDirection dir = m_solver.toDirection(iloSense);
		return new LpObjective<V>(linear, dir);
	}

	@Override
	public VariablesCplex<V> getVariables() {
		return m_variablesCplex;
	}

	@Override
	public Number getVariableLowerBound(V variable) {
		return m_variablesCplex.getLowerBound(variable);
	}

	@Override
	public String getVariableName(V variable) {
		TODO();
	}

	@Override
	public void setConstraintsNamer(Function<LpConstraint<V>, String> namer) {
		TODO();
	}

	@Override
	public Function<LpConstraint<V>, String> getConstraintsNamer() {
		return m_constraintsCplex.getConstraintsNamer();
	}

	@Override
	public LpVariableType getVariableType(V variable) {
		TODO();
	}

	@Override
	public Number getVariableUpperBound(V variable) {
		TODO();
	}

	@Override
	public boolean setName(String name) {
		TODO();
	}

	@Override
	public boolean setObjective(LpLinear<V> objectiveFunction, LpDirection direction) {
		TODO();
	}

	@Override
	public boolean setObjectiveDirection(LpDirection dir) {
		TODO();
	}

	@Override
	public boolean setVariableBounds(V variable, Number lowerBound, Number upperBound) {
		TODO();
	}

	@Override
	public boolean setVariableType(V variable, LpVariableType type) {
		TODO();
	}

	@Override
	public Function<? super V, String> getVariablesNamer() {
		TODO();
	}

	@Override
	public void setVariablesNamer(Function<? super V, String> namer) {
		TODO();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LpProblem<?>)) {
			return false;
		}
		LpProblem<?> p2 = (LpProblem<?>) obj;
		return LpSolverUtils.equivalent(this, p2);
	}

	@Override
	public int hashCode() {
		// TODO impl equal and hashcode on dependant objects
		return LpSolverUtils.getProblemEquivalence().hash(this);
	}

}