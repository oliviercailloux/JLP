package io.github.oliviercailloux.jlp.problem;

import io.github.oliviercailloux.jlp.LpConstraint;
import io.github.oliviercailloux.jlp.utils.LpSolverUtils;

/**
 * <p>
 * This object may be used to get rid of the boolean types in a problem and view
 * them as integers. It implements a view of a {@link LpProblem} that views
 * every variables defined as {@link LpVariableType#BOOL} rather as
 * {@link LpVariableType#INT} type with possibly modified bounds. Consider a
 * variable defined in the delegate problem having the type
 * {@link LpVariableType#BOOL} and lower and upper bounds <em>&lt;l, u></em>.
 * This view sees it as a variable of type {@link LpVariableType#INT} with as
 * lower bound the integer 0 if <em>l</em>.doubleValue() is lower than zero or
 * if <em>l</em> is negative infinity, and <em>l</em> otherwise; and as upper
 * bound the integer 1 if <em>u</em>.doubleValue() is greater than one or
 * <em>u</em> is positive infinity, and <em>u</em> otherwise. Thus a
 * {@link LpVariableType#BOOL} variable with bounds of <-1, 0.5> become, through
 * the view, an {@link LpVariableType#INT} variable with bounds of <0, 0.5>. The
 * rest of the data is viewed unmodified.
 * </p>
 * <p>
 * The view writes to the delegated objects. Written data are not modified by
 * the view: writing a {@link LpVariableType#BOOL} variable ends up as a
 * {@link LpVariableType#BOOL} variable in the delegate and then will be viewed
 * as an {@link LpVariableType#INT} .
 * </p>
 * <p>
 * Methods that provide the transformations described here only on the bounds,
 * on demand, are also available, see
 * {@link LpSolverUtils#getVarLowerBoundBounded(LpProblem, Object)}.
 * </p>
 * <p>
 * This object does not change iteration order of set of constraints and the set
 * of variables: iteration order of these sets is the order used by the
 * delegate.
 *
 * @param <V>
 *            the type of the variables objects.
 *
 * @author Olivier Cailloux
 *
 */
public class LpProblemWithTransformedBoolsView<V> extends LpProblemForwarder<V> implements LpProblem<V> {

	/**
	 * Creates a view that delegates to the given object.
	 * 
	 * @param delegate
	 *            not <code>null</code>.
	 */
	public LpProblemWithTransformedBoolsView(LpProblem<V> delegate) {
		super(delegate);
	}

	@Override
	public boolean add(LpConstraint<V> constraint) {
		return delegate().add(constraint);
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
	public Number getVariableLowerBound(V variable) {
		return LpSolverUtils.getVarLowerBoundBounded(delegate(), variable);
	}

	@Override
	public LpVariableType getVariableType(V variable) {
		final LpVariableType type = delegate().getVariableType(variable);
		switch (type) {
		case BOOL:
		case INT:
			return LpVariableType.INT;
		case REAL:
			return LpVariableType.REAL;
		default:
			throw new IllegalStateException("Unknown type.");
		}
	}

	@Override
	public int hashCode() {
		return LpSolverUtils.getProblemEquivalence().hash(this);
	}

	@Override
	public String toString() {
		return LpSolverUtils.getAsString(this);
	}

}
