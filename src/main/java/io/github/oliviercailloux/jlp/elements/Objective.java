package io.github.oliviercailloux.jlp.elements;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.oliviercailloux.jlp.elements.Sense.MAX;
import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

/**
 * <p>
 * An objective function with optimization sense.
 * </p>
 * <p>
 * The objective may be zero, meaning that its objective function is an empty
 * sum of terms. This may be used to indicate that any solution is looked for.
 * </p>
 * <p>
 * If an objective has an empty sum of terms, then it has sense
 * {@link Sense#MAX}. This ensures there is only one zero objective. (Thus
 * testing equality to ZERO is equivalent to asking whether an objective is
 * zero.)
 * </p>
 * <p>
 * Immutable (provided variables are immutable).
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Objective {
	/**
	 * The zero objective: the objective with an empty sum of terms as objective
	 * function and a {@link Sense#MAX} optimization sense.
	 */
	public static final Objective ZERO = new Objective(SumTerms.of(), MAX);

	/**
	 * Returns an objective with optimization sense {@link Sense#MAX}.
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>, not empty.
	 */
	static public Objective max(SumTerms objectiveFunction) {
		checkArgument(!objectiveFunction.isEmpty());
		return new Objective(objectiveFunction, MAX);
	}

	/**
	 * Returns an objective with optimization sense {@link Sense#MIN}.
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>, not empty.
	 */
	static public Objective min(SumTerms objectiveFunction) {
		checkArgument(!objectiveFunction.isEmpty());
		return new Objective(objectiveFunction, Sense.MIN);
	}

	/**
	 * Returns an objective with the given function and optimization sense
	 *
	 * @param objectiveFunction
	 *            not <code>null</code>, not empty.
	 * @param sense
	 *            not <code>null</code>.
	 */
	static public Objective of(SumTerms objectiveFunction, Sense sense) {
		checkArgument(!objectiveFunction.isEmpty());
		return new Objective(objectiveFunction, sense);
	}

	/**
	 * Not <code>null</code>.
	 */
	final private SumTerms objectiveFunction;

	/**
	 * Not <code>null</code>.
	 */
	final private Sense sense;

	private Objective(SumTerms objectiveFunction, Sense direction) {
		this.objectiveFunction = requireNonNull(objectiveFunction);
		this.sense = requireNonNull(direction);
	}

	/**
	 * Two objectives are equal iff they have equal functions and senses.
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Objective)) {
			return false;
		}
		Objective obj2 = (Objective) obj;
		return (obj == this) || (sense.equals(obj2.sense) && objectiveFunction.equals(obj2.objectiveFunction));
	}

	/**
	 * Retrieves the objective function of this objective.
	 *
	 * @return not <code>null</code>, empty iff this objective is {@link #ZERO}.
	 */
	public SumTerms getFunction() {
		return objectiveFunction;
	}

	/**
	 * Retrieves the optimization sense of this objective.
	 *
	 * @return not <code>null</code>.
	 */
	public Sense getSense() {
		return sense;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(sense, objectiveFunction);
	}

	/**
	 * Tests whether this objective is zero, i.e. has an empty objective function.
	 *
	 * @return <code>true</code> iff the objective function is an empty sum.
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
		helper.addValue(sense);
		helper.add("function", objectiveFunction);
		return helper.toString();
	}
}
