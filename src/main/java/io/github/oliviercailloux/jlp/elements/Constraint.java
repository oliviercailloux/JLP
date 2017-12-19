package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.google.common.base.Preconditions;

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
 * constraint. Thus, two constraints should be equal, as judged per
 * {@link #equals(Object)}, iff they have the same descriptions. (This object
 * guarantees anyway that equality implies same description, the other direction
 * is up to the user.)
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
 * “structural x”: x < y. Constraint “conjonctural x”: x < y. The second
 * constraint might be perceived as different than the first one, and the user
 * might genuinely want both of them to appear in the MP, because the second
 * constraint might have a general form x < C×y, with a value C depending on
 * some data of the problem, and C=1 being one possibility. In such a case, only
 * the description would permit to distinguish both constraints.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Constraint {
	/**
	 * Not <code>null</code>.
	 */
	private final String descr;

	private final SumTerms lhs;

	private final ComparisonOperator op;

	private final double rhs;

	/**
	 * @param descr
	 *            not <code>null</code>.
	 * @param lhs
	 *            not <code>null</code>, not empty.
	 * @param op
	 *            not <code>null</code>.
	 * @param rhs
	 *            a valid number (not infinite, not NaN).
	 */
	public Constraint(String descr, SumTerms lhs, ComparisonOperator op, double rhs) {
		Preconditions.checkNotNull(lhs);
		Preconditions.checkNotNull(op);
		Preconditions.checkArgument(!Double.isInfinite(rhs));
		Preconditions.checkArgument(!Double.isNaN(rhs));
		Preconditions.checkArgument(lhs.size() >= 1);
		this.descr = requireNonNull(descr);
		this.lhs = lhs;
		this.op = op;
		this.rhs = rhs;
	}

	/**
	 * Tests whether the given object is also a constraint and if it has same left
	 * hand side, operator, and right hand side as this object. The id is not
	 * considered.
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
		return toString().equals(c2.toString()) && lhs.equals(c2.lhs) && op.equals(c2.op) && rhs == c2.rhs;
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

	@Override
	public int hashCode() {
		return Objects.hash(descr, lhs, op, rhs);
	}

	/**
	 * Retrieves the constraint description.
	 *
	 * @return not <code>null</code>.
	 */
	@Override
	public String toString() {
		return descr;
	}
}
