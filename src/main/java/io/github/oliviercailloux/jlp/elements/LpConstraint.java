package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Preconditions;

import io.github.oliviercailloux.jlp.utils.LpSolverUtils;

/**
 * <p>
 * A constraint in a linear program, or mixed linear program, consisting of a
 * linear expression on the left hand side (lhs) and a constant on the right
 * hand side (rhs), and an equality or inequality sign (as an
 * {@link LpOperator}) in between.
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
public class LpConstraint {
	/**
	 * Not <code>null</code>.
	 */
	private final String descr;

	private final LpLinearImmutable m_lhs;

	private final LpOperator m_op;

	private final double m_rhs;

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
	public LpConstraint(String descr, LpLinear lhs, LpOperator op, double rhs) {
		Preconditions.checkNotNull(lhs);
		Preconditions.checkNotNull(op);
		Preconditions.checkArgument(!Double.isInfinite(rhs));
		Preconditions.checkArgument(!Double.isNaN(rhs));
		Preconditions.checkArgument(lhs.size() >= 1);
		this.descr = requireNonNull(descr);
		m_lhs = LpLinearImmutable.of(lhs);
		m_op = op;
		m_rhs = rhs;
	}

	/**
	 * Tests whether the given object is also a constraint and if it has same left
	 * hand side, operator, and right hand side as this object. The id is not
	 * considered.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LpConstraint)) {
			return false;
		}
		LpConstraint c2 = (LpConstraint) obj;
		return LpSolverUtils.equivalent(this, c2);
	}

	/**
	 * @return not <code>null</code>, not empty.
	 */
	public LpLinear getLhs() {
		return m_lhs;
	}

	/**
	 * @return the op
	 */
	public LpOperator getOperator() {
		return m_op;
	}

	/**
	 * @return the rhs
	 */
	public double getRhs() {
		return m_rhs;
	}

	@Override
	public int hashCode() {
		return LpSolverUtils.getConstraintEquivalence().hash(this);
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
