package io.github.oliviercailloux.jlp.problem;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import io.github.oliviercailloux.jlp.elements.LpConstraint;
import io.github.oliviercailloux.jlp.elements.LpDirection;
import io.github.oliviercailloux.jlp.elements.LpLinear;
import io.github.oliviercailloux.jlp.elements.LpObjective;
import io.github.oliviercailloux.jlp.elements.LpOperator;
import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * A problem which forwards all its method calls to another problem. Subclasses
 * should override one or more methods to modify the behavior of the backing set
 * as desired per the
 * <a href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator
 * pattern</a>.
 *
 * @author Olivier Cailloux
 *
 */
public class LpProblemForwarder implements LpProblem {

	private final LpProblem m_delegate;

	/**
	 * @param delegate
	 *            not <code>null</code>.
	 */
	public LpProblemForwarder(LpProblem delegate) {
		Preconditions.checkNotNull(delegate);
		m_delegate = delegate;
	}

	@Override
	public boolean add(LpConstraint constraint) {
		return m_delegate.add(constraint);
	}

	@Override
	public boolean add(String id, LpLinear lhs, LpOperator operator, double rhs) {
		return m_delegate.add(id, lhs, operator, rhs);
	}

	@Override
	public boolean addVariable(Variable variable) {
		return m_delegate.addVariable(variable);
	}

	@Override
	public void clear() {
		m_delegate.clear();
	}

	@Override
	public boolean equals(Object obj) {
		return m_delegate.equals(obj);
	}

	@Override
	public Set<LpConstraint> getConstraints() {
		return m_delegate.getConstraints();
	}

	@Override
	public Function<LpConstraint, String> getConstraintsNamer() {
		return m_delegate.getConstraintsNamer();
	}

	@Override
	public LpDimension getDimension() {
		return m_delegate.getDimension();
	}

	@Override
	public String getName() {
		return m_delegate.getName();
	}

	@Override
	public LpObjective getObjective() {
		return m_delegate.getObjective();
	}

	@Override
	public Number getVariableLowerBound(Variable variable) {
		return m_delegate.getVariableLowerBound(variable);
	}

	@Override
	public String getVariableName(Variable variable) {
		return m_delegate.getVariableName(variable);
	}

	@Override
	public Set<Variable> getVariables() {
		return m_delegate.getVariables();
	}

	@Override
	public Function<Variable, String> getVariablesNamer() {
		return m_delegate.getVariablesNamer();
	}

	@Override
	public LpVariableType getVariableType(Variable variable) {
		return m_delegate.getVariableType(variable);
	}

	@Override
	public Number getVariableUpperBound(Variable variable) {
		return m_delegate.getVariableUpperBound(variable);
	}

	@Override
	public int hashCode() {
		return m_delegate.hashCode();
	}

	@Override
	public void setConstraintsNamer(Function<LpConstraint, String> namer) {
		m_delegate.setConstraintsNamer(namer);
	}

	@Override
	public boolean setName(String name) {
		return m_delegate.setName(name);
	}

	@Override
	public boolean setObjective(LpLinear objective, LpDirection direction) {
		return m_delegate.setObjective(objective, direction);
	}

	@Override
	public boolean setObjectiveDirection(LpDirection dir) {
		return m_delegate.setObjectiveDirection(dir);
	}

	@Override
	public boolean setVariableBounds(Variable variable, Number lowerBound, Number upperBound) {
		return m_delegate.setVariableBounds(variable, lowerBound, upperBound);
	}

	@Override
	public void setVariablesNamer(Function<Variable, String> namer) {
		m_delegate.setVariablesNamer(namer);
	}

	@Override
	public boolean setVariableType(Variable variable, LpVariableType type) {
		return m_delegate.setVariableType(variable, type);
	}

	@Override
	public String toString() {
		return m_delegate.toString();
	}

	protected LpProblem delegate() {
		return m_delegate;
	}

}
