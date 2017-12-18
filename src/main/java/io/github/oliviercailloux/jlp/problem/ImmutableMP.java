package io.github.oliviercailloux.jlp.problem;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.google.common.base.Function;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.ObjectiveFunction;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableType;

/**
 * An immutable problem. To obtain such a problem, use {@link MPs#newProblem()},
 * populate the problem, then use {@link MPs#newImmutable(IMP)}.
 *
 * @author Olivier Cailloux
 *
 */
public class ImmutableMP implements IMP {

	/**
	 * Returns an immutable MP containing the data in the given source.
	 *
	 * @param source
	 *            not <code>null</code>.
	 */
	static public ImmutableMP copyOf(IMP source) {
		if (source instanceof ImmutableMP) {
			return (ImmutableMP) source;
		}
		return new ImmutableMP(new MP(source));
	}

	/**
	 * Private (not shared).
	 */
	private IMP delegate;

	private ImmutableMP(IMP delegate) {
		this.delegate = requireNonNull(delegate);
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

}
