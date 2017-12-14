package org.decision_deck.jlp.problem;

import java.util.Set;

import org.decision_deck.jlp.LpConstraint;
import org.decision_deck.jlp.LpDirection;
import org.decision_deck.jlp.LpLinear;
import org.decision_deck.jlp.LpObjective;
import org.decision_deck.jlp.LpOperator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * A problem which forwards all its method calls to another problem. Subclasses should override one or more methods to
 * modify the behavior of the backing set as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
public class LpProblemForwarder<V> implements LpProblem<V> {

    private final LpProblem<V> m_delegate;

    /**
     * @param delegate
     *            not <code>null</code>.
     */
    public LpProblemForwarder(LpProblem<V> delegate) {
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
    }

    @Override
    public boolean add(LpConstraint<V> constraint) {
	return m_delegate.add(constraint);
    }

    @Override
    public boolean add(Object id, LpLinear<V> lhs, LpOperator operator, double rhs) {
	return m_delegate.add(id, lhs, operator, rhs);
    }

    @Override
    public boolean addVariable(V variable) {
	return m_delegate.addVariable(variable);
    }

    @Override
    public void clear() {
	m_delegate.clear();
    }

    protected LpProblem<V> delegate() {
	return m_delegate;
    }

    @Override
    public boolean equals(Object obj) {
	return m_delegate.equals(obj);
    }

    @Override
    public Set<LpConstraint<V>> getConstraints() {
	return m_delegate.getConstraints();
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
    public LpObjective<V> getObjective() {
	return m_delegate.getObjective();
    }

    @Override
    public Set<V> getVariables() {
	return m_delegate.getVariables();
    }

    @Override
    public Number getVariableLowerBound(V variable) {
	return m_delegate.getVariableLowerBound(variable);
    }

    @Override
    public String getVariableName(V variable) {
	return m_delegate.getVariableName(variable);
    }

    @Override
    public LpVariableType getVariableType(V variable) {
	return m_delegate.getVariableType(variable);
    }

    @Override
    public Number getVariableUpperBound(V variable) {
	return m_delegate.getVariableUpperBound(variable);
    }

    @Override
    public int hashCode() {
	return m_delegate.hashCode();
    }

    @Override
    public boolean setName(String name) {
	return m_delegate.setName(name);
    }

    @Override
    public boolean setObjective(LpLinear<V> objective, LpDirection direction) {
	return m_delegate.setObjective(objective, direction);
    }

    @Override
    public boolean setObjectiveDirection(LpDirection dir) {
	return m_delegate.setObjectiveDirection(dir);
    }

    @Override
    public boolean setVariableBounds(V variable, Number lowerBound, Number upperBound) {
	return m_delegate.setVariableBounds(variable, lowerBound, upperBound);
    }

    @Override
    public boolean setVariableType(V variable, LpVariableType type) {
	return m_delegate.setVariableType(variable, type);
    }

    @Override
    public String toString() {
	return m_delegate.toString();
    }

    @Override
    public void setVariablesNamer(Function<? super V, String> namer) {
	m_delegate.setVariablesNamer(namer);
    }

    @Override
    public Function<? super V, String> getVariablesNamer() {
	return m_delegate.getVariablesNamer();
    }

    @Override
    public void setConstraintsNamer(Function<LpConstraint<V>, String> namer) {
	m_delegate.setConstraintsNamer(namer);
    }

    @Override
    public Function<LpConstraint<V>, String> getConstraintsNamer() {
	return m_delegate.getConstraintsNamer();
    }

}
