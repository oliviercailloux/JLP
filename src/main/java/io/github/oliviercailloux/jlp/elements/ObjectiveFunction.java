package io.github.oliviercailloux.jlp.elements;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An objective function with optimization direction (possibly
 * <code>null</code>s). Immutable.
 *
 * @author Olivier Cailloux
 *
 */
public class ObjectiveFunction {
	/**
	 * May be <code>null</code>. TODO check why this is allowed.
	 */
	final private OptimizationDirection direction;

	/**
	 * May be <code>null</code>.
	 */
	final private SumTerms objectiveFunction;

	/**
	 * Creates a new objective function with direction.
	 *
	 * @param objectiveFunction
	 *            may be <code>null</code>.
	 * @param direction
	 *            may be <code>null</code>.
	 */
	public ObjectiveFunction(SumTerms objectiveFunction, OptimizationDirection direction) {
		this.objectiveFunction = objectiveFunction;
		this.direction = direction;
	}

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
	 * Retrieves the optimization direction of the objective function to be.
	 *
	 * @return possibly <code>null</code>.
	 */
	public OptimizationDirection getDirection() {
		return direction;
	}

	/**
	 * Retrieves the objective function stored in this object.
	 *
	 * @return possibly <code>null</code>. Is a copy or is immutable.
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
	 * Tests whether this objective is empty, i.e. has neither a function nor a
	 * direction set.
	 *
	 * @return <code>true</code> iff both the objective function and the direction
	 *         are <code>null</code>.
	 */
	public boolean isEmpty() {
		return objectiveFunction == null && direction == null;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("Function", objectiveFunction);
		helper.add("Direction", direction);
		return helper.toString();
	}
}
