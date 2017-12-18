package io.github.oliviercailloux.jlp.problem;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.OptimizationDirection;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.ObjectiveFunction;
import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableType;

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
public class MPForwarder implements MP {

	private final MP delegate;

	/**
	 * @param delegate
	 *            not <code>null</code>.
	 */
	public MPForwarder(MP delegate) {
		Preconditions.checkNotNull(delegate);
		this.delegate = delegate;
	}

	@Override
	public boolean add(Constraint constraint) {
		return delegate.add(constraint);
	}

	@Override
	public boolean add(String id, SumTerms lhs, ComparisonOperator operator, double rhs) {
		return delegate.add(id, lhs, operator, rhs);
	}

	@Override
	public boolean addVariable(Variable variable) {
		return delegate.addVariable(variable);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	@Override
	public Set<Constraint> getConstraints() {
		return delegate.getConstraints();
	}

	@Override
	public Function<Constraint, String> getConstraintsNamer() {
		return delegate.getConstraintsNamer();
	}

	@Override
	public MPDimension getDimension() {
		return delegate.getDimension();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public ObjectiveFunction getObjective() {
		return delegate.getObjective();
	}

	@Override
	public Number getVariableLowerBound(Variable variable) {
		return delegate.getVariableLowerBound(variable);
	}

	@Override
	public String getVariableName(Variable variable) {
		return delegate.getVariableName(variable);
	}

	@Override
	public Set<Variable> getVariables() {
		return delegate.getVariables();
	}

	@Override
	public Function<Variable, String> getVariablesNamer() {
		return delegate.getVariablesNamer();
	}

	@Override
	public VariableType getVariableType(Variable variable) {
		return delegate.getVariableType(variable);
	}

	@Override
	public Number getVariableUpperBound(Variable variable) {
		return delegate.getVariableUpperBound(variable);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public void setConstraintsNamer(Function<Constraint, String> namer) {
		delegate.setConstraintsNamer(namer);
	}

	@Override
	public boolean setName(String name) {
		return delegate.setName(name);
	}

	@Override
	public boolean setObjective(SumTerms objective, OptimizationDirection direction) {
		return delegate.setObjective(objective, direction);
	}

	@Override
	public boolean setObjectiveDirection(OptimizationDirection dir) {
		return delegate.setObjectiveDirection(dir);
	}

	@Override
	public boolean setVariableBounds(Variable variable, Number lowerBound, Number upperBound) {
		return delegate.setVariableBounds(variable, lowerBound, upperBound);
	}

	@Override
	public void setVariablesNamer(Function<Variable, String> namer) {
		delegate.setVariablesNamer(namer);
	}

	@Override
	public boolean setVariableType(Variable variable, VariableType type) {
		return delegate.setVariableType(variable, type);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	protected MP delegate() {
		return delegate;
	}

}
