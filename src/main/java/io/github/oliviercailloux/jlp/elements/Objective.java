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
 * The objective may be {@link Objective#ZERO}, meaning that its objective
 * function is an empty sum of terms. This may be used to indicate the absence
 * of an objective function, thus, that any solution is looked for. (This
 * implements the Null object pattern.)
 * </p>
 * <p>
 * If an objective has an empty sum of terms, then it has sense
 * {@link Sense#MAX}. This ensures there is only one kind of “empty” objective.
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
	 * Returns an objective with optimization sense {@link Sense#MIN}.
	 *
	 * @param objectiveFunction not <code>null</code>, not empty.
	 */
	public static Objective min(SumTerms objectiveFunction) {
		checkArgument(!objectiveFunction.isEmpty());
		return new Objective(objectiveFunction, Sense.MIN);
	}

	/**
	 * Returns an objective with optimization sense {@link Sense#MAX}. Returns the
	 * objective {@link Objective#ZERO} iff the given objectiveFunction is empty.
	 *
	 * @param objectiveFunction not <code>null</code>, may be empty.
	 */
	public static Objective max(SumTerms objectiveFunction) {
		return new Objective(objectiveFunction, MAX);
	}

	/**
	 * Returns an objective with the given function and optimization sense.
	 *
	 * @param objectiveFunction not <code>null</code>, not empty.
	 * @param sense             not <code>null</code>.
	 */
	public static Objective of(SumTerms objectiveFunction, Sense sense) {
		checkArgument(!objectiveFunction.isEmpty());
		return new Objective(objectiveFunction, sense);
	}

	/**
	 * Not <code>null</code>.
	 */
	private final SumTerms objectiveFunction;

	/**
	 * Not <code>null</code>.
	 */
	private final Sense sense;

	private Objective(SumTerms objectiveFunction, Sense direction) {
		this.objectiveFunction = requireNonNull(objectiveFunction);
		this.sense = requireNonNull(direction);
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

	/**
	 * Tests whether this objective is zero, equivalently, has an empty objective
	 * function.
	 *
	 * @return <code>true</code> iff the objective function is an empty sum.
	 */
	public boolean isZero() {
		return getFunction().isEmpty();
	}

	/**
	 * Two objectives are equal iff they have equal functions and senses.
	 *
	 */
	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Objective)) {
			return false;
		}
		Objective obj2 = (Objective) o2;
		return (o2 == this) || (sense.equals(obj2.sense) && objectiveFunction.equals(obj2.objectiveFunction));
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(sense, objectiveFunction);
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
		helper.addValue(objectiveFunction);
		return helper.toString();
	}
}
