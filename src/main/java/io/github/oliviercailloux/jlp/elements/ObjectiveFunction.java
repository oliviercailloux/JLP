package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An objective function with optimization direction.
 *
 * The objective function may be zero, meaning an empty sum of terms. This may
 * be used to indicate that any solution is looked for.
 *
 * Immutable.
 *
 * @author Olivier Cailloux
 *
 */
public class ObjectiveFunction {
	private static final ObjectiveFunction ZERO_MAX = new ObjectiveFunction(SumTerms.of(), OptimizationDirection.MAX);

	/**
	 * Creates a new objective function with direction
	 * {@link OptimizationDirection#MAX}.
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>.
	 */
	static public ObjectiveFunction max(SumTerms objectiveFunction) {
		return new ObjectiveFunction(objectiveFunction, OptimizationDirection.MAX);
	}

	/**
	 * Creates a new objective function with direction
	 * {@link OptimizationDirection#MIN}.
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>.
	 */
	static public ObjectiveFunction min(SumTerms objectiveFunction) {
		return new ObjectiveFunction(objectiveFunction, OptimizationDirection.MIN);
	}

	/**
	 * Creates a new objective function with direction.
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>.
	 * @param direction
	 *            not <code>null</code>.
	 */
	static public ObjectiveFunction of(SumTerms objectiveFunction, OptimizationDirection direction) {
		return new ObjectiveFunction(objectiveFunction, direction);
	}

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
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ObjectiveFunction)) {
			return false;
		}
		ObjectiveFunction obj2 = (ObjectiveFunction) obj;
		if (!Objects.equal(getDirection(), obj2.getDirection())) {
			return false;
		}
		if (!Objects.equal(getFunction(), obj2.getFunction())) {
			return false;
		}
		return true;
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
	 * Tests whether this objective is fully specified, i.e. has a function and a
	 * direction set.
	 *
	 * @return <code>true</code> iff both the objective function and the direction
	 *         are non <code>null</code>.
	 */
	public boolean isComplete() {
		return objectiveFunction != null && direction != null;
	}

	/**
	 * Tests whether this objective is zero, i.e. has an empty function.
	 *
	 * @return <code>true</code> iff the function is an empty sum.
	 */
	public boolean isZero() {
		return getFunction().isEmpty();
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("Function", objectiveFunction);
		helper.add("Direction", direction);
		return helper.toString();
	}
}
