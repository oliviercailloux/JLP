package org.decision_deck.jlp;

import org.decision_deck.jlp.utils.LpLinearUtils;
import org.decision_deck.jlp.utils.LpSolverUtils;

import com.google.common.base.Preconditions;

/**
 * <p>
 * A constraint in a linear program, or mixed linear program, consisting of a linear expression on the left hand side
 * (lhs) and a constant on the right hand side (rhs), and an equality or inequality sign (as an {@link LpOperator}) in
 * between.
 * </p>
 * <p>
 * Such a constraint may have a id that may be used to describe or identify it, e.g. its string form may be used when
 * writing a mathematical problem to a file. Although this object will accept any id, when dealing with several
 * constraints, it is strongly recommanded to either use <code>null</code> ids (thus to not use ids) or to use unique
 * ids each identifying with no ambiguity one constraint, as objects using these constraints might rely on their ids
 * being truly unique identifiers. Thus, it is suggested to ensure that the following relation holds on a set of
 * constraints C: for any two constraints c1, c2 in C whose ids are both non <code>null</code>, c1 id must
 * {@link #equals} c2 id if and only if c1 {@link #equals} c2.
 * </p>
 * <p>
 * Objects of this class are immutable.
 * </p>
 * <p>
 * A constraint {@link #equals(Object)} an other constraint iff they have the same left hand side, operator, and right
 * hand side. The id is not considered.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
public class LpConstraint<V> {
    private final LpLinearImmutable<V> m_lhs;

    /**
     * <code>null</code> if not set.
     */
    private final Object m_id;

    private final LpOperator m_op;

    private final double m_rhs;

    /**
     * @param id
     *            <code>null</code> for no id.
     * @param lhs
     *            not <code>null</code>, not empty.
     * @param op
     *            not <code>null</code>.
     * @param rhs
     *            a valid number (not infinite, not NaN).
     */
    public LpConstraint(Object id, LpLinear<V> lhs, LpOperator op, double rhs) {
	Preconditions.checkNotNull(lhs);
	Preconditions.checkNotNull(op);
	Preconditions.checkArgument(!Double.isInfinite(rhs));
	Preconditions.checkArgument(!Double.isNaN(rhs));
	Preconditions.checkArgument(lhs.size() >= 1);
	m_id = id == null ? "" : id;
	m_lhs = LpLinearUtils.newImmutable(lhs);
	m_op = op;
	m_rhs = rhs;
    }

    /**
     * Tests whether the given object is also a constraint and if it has same left hand side, operator, and right hand
     * side as this object. The id is not considered.
     */
    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof LpConstraint)) {
	    return false;
	}
	LpConstraint<?> c2 = (LpConstraint<?>) obj;
	return LpSolverUtils.equivalent(this, c2);
    }

    /**
     * @return not <code>null</code>, not empty.
     */
    public LpLinear<V> getLhs() {
	return m_lhs;
    }

    /**
     * Retrieves the constraint id, if it is set.
     * 
     * @return <code>null</code> if not set.
     */
    public Object getId() {
	return m_id;
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

    @Override
    public String toString() {
	return LpSolverUtils.getAsString(this);
    }

    /**
     * Retrieves a string identifier for this constraint, if possible. This is the string form of this constraint id, as
     * per {@link #toString()}, or the empty string if no id is set.
     * 
     * @return never <code>null</code>, empty if no id is set.
     */
    public String getIdAsString() {
	return m_id == null ? "" : m_id.toString();
    }
}
