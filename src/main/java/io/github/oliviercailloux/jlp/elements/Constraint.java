package io.github.oliviercailloux.jlp.elements;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * <p>
 * A constraint in a linear program, or mixed linear program, consisting of a
 * linear expression on the left hand side (lhs) and a constant on the right
 * hand side (rhs), and an equality or inequality sign (as a
 * {@link ComparisonOperator}) in between.
 * </p>
 * <p>
 * A constraint has a description. It is recommended for clarity (but not
 * mandatory) to use descriptions that identify with no ambiguity one
 * constraint. Thus, it is recommended that two constraints be equal, as judged
 * per {@link #equals(Object)}, iff they have the same descriptions. (This
 * object guarantees anyway that equality implies same description, the other
 * direction is up to the user.)
 * </p>
 * <p>
 * A constraint {@link #equals(Object)} an other constraint iff they have the
 * same description, left hand side, operator, and right hand side.
 * </p>
 * <p>
 * Objects of this class are immutable (provided variables are).
 * </p>
 * <p>
 * Rationale for equality: we want to allow that the user does not use
 * descriptions, by choosing (for example) an empty description for every
 * constraints. Hence, equality has to rely on the rest of the attributes at
 * least. But we also want to permit for the following case. Constraint
 * “structural x”: x ≤ y. Constraint “conjonctural x”: x ≤ y. The second
 * constraint might be perceived as different than the first one, and the user
 * might genuinely want both of them to appear in the MP, because the second
 * constraint might have a general form x ≤ C×y, with a value C depending on
 * some data of the MP, with C=1 being one possibility. In such a case, only the
 * description would permit to distinguish both constraints.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Constraint {
	/**
	 * Returns a constraint with the given data.
	 *
	 * @param description not <code>null</code>, may be empty.
	 * @param lhs         not <code>null</code>, not empty.
	 * @param op          not <code>null</code>.
	 * @param rhs         a finite number.
	 */
	public static Constraint of(String description, SumTerms lhs, ComparisonOperator op, double rhs) {
		return new Constraint(description, lhs, op, rhs);
	}

	/**
	 * Returns a constraint with the given data and an empty description.
	 *
	 * @param lhs not <code>null</code>, not empty.
	 * @param op  not <code>null</code>.
	 * @param rhs a finite number.
	 */
	public static Constraint of(SumTerms lhs, ComparisonOperator op, double rhs) {
		return new Constraint("", lhs, op, rhs);
	}

	/**
	 * Returns a constraint with the given data, the {@link ComparisonOperator#LE}
	 * operator and an empty description.
	 *
	 * @param lhs not <code>null</code>, not empty.
	 * @param rhs a finite number.
	 * @return the constraint <code>lhs</code> ≤ <code>rhs</code>.
	 */
	public static Constraint LE(SumTerms lhs, double rhs) {
		return new Constraint("", lhs, ComparisonOperator.LE, rhs);
	}

	/**
	 * Returns a constraint with the given data, the {@link ComparisonOperator#EQ}
	 * operator and an empty description.
	 *
	 * @param lhs not <code>null</code>, not empty.
	 * @param rhs a finite number.
	 * @return the constraint <code>lhs</code> = <code>rhs</code>.
	 */
	public static Constraint EQ(SumTerms lhs, double rhs) {
		return new Constraint("", lhs, ComparisonOperator.EQ, rhs);
	}

	/**
	 * Returns a constraint with the given data, the {@link ComparisonOperator#GE}
	 * operator and an empty description.
	 *
	 * @param lhs not <code>null</code>, not empty.
	 * @param rhs a finite number.
	 * @return the constraint <code>lhs</code> ≥ <code>rhs</code>.
	 */
	public static Constraint GE(SumTerms lhs, double rhs) {
		return new Constraint("", lhs, ComparisonOperator.GE, rhs);
	}

	/**
	 * Not <code>null</code>.
	 */
	private final String descr;

	private final SumTerms lhs;

	private final ComparisonOperator op;

	private final double rhs;

	/**
	 * @param description not <code>null</code>.
	 * @param lhs         not <code>null</code>, not empty.
	 * @param op          not <code>null</code>.
	 * @param rhs         a finite number.
	 */
	private Constraint(String description, SumTerms lhs, ComparisonOperator op, double rhs) {
		this.descr = requireNonNull(description);

		checkArgument(!lhs.isEmpty());
		this.lhs = requireNonNull(lhs);

		this.op = requireNonNull(op);

		checkArgument(Double.isFinite(rhs));
		this.rhs = rhs;
	}

	/**
	 * Retrieves the constraint description.
	 *
	 * @return not <code>null</code>.
	 */
	public String getDescription() {
		return descr;
	}

	/**
	 * @return not <code>null</code>, not empty.
	 */
	public SumTerms getLhs() {
		return lhs;
	}

	/**
	 * @return the op
	 */
	public ComparisonOperator getOperator() {
		return op;
	}

	/**
	 * @return the rhs
	 */
	public double getRhs() {
		return rhs;
	}

	/**
	 * Returns <code>true</code> iff the given object is also a constraint and has
	 * same description, left hand side, operator, and right hand side as this
	 * object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Constraint)) {
			return false;
		}
		final Constraint c2 = (Constraint) obj;
		if (c2 == this) {
			return true;
		}
		return descr.equals(c2.descr) && lhs.equals(c2.lhs) && op.equals(c2.op) && rhs == c2.rhs;
	}

	@Override
	public int hashCode() {
		return Objects.hash(descr, lhs, op, rhs);
	}

	/**
	 * Returns a string representation of this constraint (useful for debug).
	 *
	 */
	@Override
	public String toString() {
		final String expr = lhs + " " + op + " " + rhs;
		return MoreObjects.toStringHelper(this).add("description", descr).add("expression", expr).toString();
	}
}
