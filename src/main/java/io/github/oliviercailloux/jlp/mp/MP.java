package io.github.oliviercailloux.jlp.mp;

import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * <p>
 * An immutable MP.
 * </p>
 * <p>
 * To obtain such an MP, use {@link MP#builder()}, populate the MP, then use
 * {@link MPBuilder#build()}.
 * </p>
 * <p>
 * Immutability is only guaranteed if variables are immutable.
 * </p>
 *
 * @author Olivier Cailloux
 * @see IMP
 * @see Variable
 */
public class MP implements IMP {

	/**
	 * Returns a new empty writeable MP with an empty name and the
	 * {@link Objective#ZERO ZERO} objective.
	 *
	 * @return a new writable MP.
	 */
	public static MPBuilder builder() {
		return MPBuilder.create();
	}

	/**
	 * Returns an immutable MP containing the data in the given source.
	 *
	 * @param source not <code>null</code>.
	 */
	public static MP copyOf(IMP source) {
		requireNonNull(source);
		if (source instanceof MP) {
			return (MP) source;
		}
		return new MP(MPBuilder.copyOf(source));
	}

	private final ImmutableList<Constraint> constraintsImmutable;

	/**
	 * Private (not shared).
	 */
	private final IMP delegate;

	private final ImmutableList<Variable> variablesImmutable;

	private MP(IMP delegate) {
		this.delegate = requireNonNull(delegate);
		constraintsImmutable = ImmutableList.copyOf(delegate.getConstraints());
		variablesImmutable = ImmutableList.copyOf(delegate.getVariables());
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public boolean containsVariable(String description) {
		return delegate.containsVariable(description);
	}

	@Override
	public Variable getVariable(String description) {
		return delegate.getVariable(description);
	}

	@Override
	public ImmutableList<Variable> getVariables() {
		return variablesImmutable;
	}

	@Override
	public ImmutableList<Constraint> getConstraints() {
		return constraintsImmutable;
	}

	@Override
	public Objective getObjective() {
		return delegate.getObjective();
	}

	@Override
	public MPDimension getDimension() {
		return delegate.getDimension();
	}

	@Override
	public boolean equals(Object o2) {
		return delegate.equals(o2);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.add("name", getName());
		helper.addValue(getObjective());
		helper.addValue(getDimension());
		return helper.toString();
	}

}
