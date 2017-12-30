package io.github.oliviercailloux.jlp.problem;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import com.google.common.collect.ImmutableList;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.ObjectiveFunction;
import io.github.oliviercailloux.jlp.elements.Variable;

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
		return new ImmutableMP(MP.copyOf(source));
	}

	private final ImmutableList<Constraint> constraintsImmutable;

	/**
	 * Private (not shared).
	 */
	private IMP delegate;

	private final ImmutableList<Variable> variablesImmutable;

	private ImmutableMP(IMP delegate) {
		this.delegate = requireNonNull(delegate);
		constraintsImmutable = ImmutableList.copyOf(delegate.getConstraints());
		variablesImmutable = ImmutableList.copyOf(delegate.getVariables());
	}

	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	@Override
	public ImmutableList<Constraint> getConstraints() {
		return constraintsImmutable;
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
	public Optional<Variable> getVariable(String description) {
		return delegate.getVariable(description);
	}

	@Override
	public ImmutableList<Variable> getVariables() {
		return variablesImmutable;
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

}
