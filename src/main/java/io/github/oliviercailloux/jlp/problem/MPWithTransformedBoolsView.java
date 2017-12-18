package io.github.oliviercailloux.jlp.problem;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableType;
import io.github.oliviercailloux.jlp.utils.SolverUtils;

/**
 * <p>
 * This object may be used to get rid of the boolean types in a problem and view
 * them as integers. It implements a view of a {@link MP} that views
 * every variables defined as {@link VariableType#BOOL} rather as
 * {@link VariableType#INT} type with possibly modified bounds. Consider a
 * variable defined in the delegate problem having the type
 * {@link VariableType#BOOL} and lower and upper bounds <em>&lt;l, u></em>.
 * This view sees it as a variable of type {@link VariableType#INT} with as
 * lower bound the integer 0 if <em>l</em>.doubleValue() is lower than zero or
 * if <em>l</em> is negative infinity, and <em>l</em> otherwise; and as upper
 * bound the integer 1 if <em>u</em>.doubleValue() is greater than one or
 * <em>u</em> is positive infinity, and <em>u</em> otherwise. Thus a
 * {@link VariableType#BOOL} variable with bounds of <-1, 0.5> become, through
 * the view, an {@link VariableType#INT} variable with bounds of <0, 0.5>. The
 * rest of the data is viewed unmodified.
 * </p>
 * <p>
 * The view writes to the delegated objects. Written data are not modified by
 * the view: writing a {@link VariableType#BOOL} variable ends up as a
 * {@link VariableType#BOOL} variable in the delegate and then will be viewed
 * as an {@link VariableType#INT} .
 * </p>
 * <p>
 * Methods that provide the transformations described here only on the bounds,
 * on demand, are also available, see
 * {@link SolverUtils#getVarLowerBoundBounded(MP, Object)}.
 * </p>
 * <p>
 * This object does not change iteration order of set of constraints and the set
 * of variables: iteration order of these sets is the order used by the
 * delegate.
 *
 * @author Olivier Cailloux
 *
 */
public class MPWithTransformedBoolsView extends MPForwarder implements MP {

	/**
	 * Creates a view that delegates to the given object.
	 *
	 * @param delegate
	 *            not <code>null</code>.
	 */
	public MPWithTransformedBoolsView(MP delegate) {
		super(delegate);
	}

	@Override
	public boolean add(Constraint constraint) {
		return delegate().add(constraint);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MP)) {
			return false;
		}
		MP p2 = (MP) obj;
		return SolverUtils.equivalent(this, p2);
	}

	@Override
	public Number getVariableLowerBound(Variable variable) {
		return SolverUtils.getVarLowerBoundBounded(delegate(), variable);
	}

	@Override
	public VariableType getVariableType(Variable variable) {
		final VariableType type = delegate().getVariableType(variable);
		switch (type) {
		case BOOL:
		case INT:
			return VariableType.INT;
		case REAL:
			return VariableType.REAL;
		default:
			throw new IllegalStateException("Unknown type.");
		}
	}

	@Override
	public int hashCode() {
		return SolverUtils.getProblemEquivalence().hash(this);
	}

	@Override
	public String toString() {
		return SolverUtils.getAsString(this);
	}

}
