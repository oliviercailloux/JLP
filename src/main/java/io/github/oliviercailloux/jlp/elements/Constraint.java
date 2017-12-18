package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Preconditions;

import io.github.oliviercailloux.jlp.utils.SolverUtils;

/**
 * <p>
 * A constraint in a linear program, or mixed linear program, consisting of a
 * linear expression on the left hand side (lhs) and a constant on the right
 * hand side (rhs), and an equality or inequality sign (as an
 * {@link ComparisonOperator}) in between.
 * </p>
 * <p>
 * A constraint has a description. It is recommended to use descriptions that
 * identify with no ambiguity one constraint. Thus, two constraints should be
 * equal, as judged per {@link #equals(Object)}, iff they have the same
 * descriptions.
 * </p>
 * <p>
 * A constraint {@link #equals(Object)} an other constraint iff they have the
 * same left hand side, operator, and right hand side. The id is not considered.
 * </p>
 * <p>
 * Objects of this class are immutable.
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

	private final SumTermsImmutable lhs;

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
		this.lhs = SumTermsImmutable.of(lhs);
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
		Constraint c2 = (Constraint) obj;
		return SolverUtils.equivalent(this, c2);
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
		return SolverUtils.getConstraintEquivalence().hash(this);
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
