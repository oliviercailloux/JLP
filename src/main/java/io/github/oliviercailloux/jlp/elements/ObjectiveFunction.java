package io.github.oliviercailloux.jlp.elements;

import static io.github.oliviercailloux.jlp.elements.OptimizationDirection.MAX;
import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

/**
 * An objective function with optimization direction.
 *
 * The objective function may be zero, meaning an empty sum of terms. This may
 * be used to indicate that any solution is looked for.
 *
 * Immutable (provided variables are immutable).
 *
 * @author Olivier Cailloux
 *
 */
public class ObjectiveFunction {
	private static final ObjectiveFunction ZERO_MAX = new ObjectiveFunction(SumTerms.of(), MAX);

	/**
	 * Creates a new objective function with optimization direction
	 * {@link OptimizationDirection#MAX}.
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>.
	 */
	static public ObjectiveFunction max(SumTerms objectiveFunction) {
		return new ObjectiveFunction(objectiveFunction, MAX);
	}

	/**
	 * Creates a new objective function with optimization direction
	 * {@link OptimizationDirection#MIN}.
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>.
	 */
	static public ObjectiveFunction min(SumTerms objectiveFunction) {
		return new ObjectiveFunction(objectiveFunction, OptimizationDirection.MIN);
	}

	/**
	 * Creates a new objective function.
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>.
	 * @param direction
	 *            not <code>null</code>.
	 */
	static public ObjectiveFunction of(SumTerms objectiveFunction, OptimizationDirection direction) {
		return new ObjectiveFunction(objectiveFunction, direction);
	}

	/**
	 * Returns the objective function with an empty sum of terms and a
	 * {@link OptimizationDirection#MAX} optimization direction.
	 *
	 * @return a zero objective function.
	 */
	static public ObjectiveFunction zero() {
		return ZERO_MAX;
	}

	/**
	 * Not <code>null</code>.
	 */
	final private OptimizationDirection direction;

	/**
	 * Not <code>null</code>.
	 */
	final private SumTerms objectiveFunction;

	private ObjectiveFunction(SumTerms objectiveFunction, OptimizationDirection direction) {
		this.objectiveFunction = requireNonNull(objectiveFunction);
		this.direction = requireNonNull(direction);
	}

	/**
	 * Two objective functions are equal iff they have equal functions and
	 * directions.
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ObjectiveFunction)) {
			return false;
		}
		ObjectiveFunction obj2 = (ObjectiveFunction) obj;
		return (obj == this) || (direction.equals(obj2.direction) && objectiveFunction.equals(obj2.objectiveFunction));
	}

	/**
	 * Retrieves the optimization direction of the objective.
	 *
	 * @return not <code>null</code>.
	 */
	public OptimizationDirection getDirection() {
		return direction;
	}

	/**
	 * Retrieves the objective function stored in this object.
	 *
	 * @return not <code>null</code>, possibly zero.
	 */
	public SumTerms getFunction() {
		return objectiveFunction;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(direction, objectiveFunction);
	}

	/**
	 * Tests whether this objective is zero, i.e. has an empty function.
	 *
	 * @return <code>true</code> iff the function is an empty sum.
	 */
	public boolean isZero() {
		return getFunction().isEmpty();
	}

	/**
	 * Returns a string representation of this object. This should be used for debug
	 * purposes only as this method gives no control on the number of decimal digits
	 * shown in the function.
	 *
	 */
	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.addValue(direction);
		helper.add("function", objectiveFunction);
		return helper.toString();
	}
}
