package org.decision_deck.jlp.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jlp.LpOperator;

public class LpConstraintsUtils {
	static public void assertSatisfied(double lhs, LpOperator op, double rhs) {
		if (!satisfied(lhs, op, rhs)) {
			throw new IllegalStateException("Unsatisfied: " + lhs + op + rhs + ".");
		}
	}

	static public void assertSatisfied(int lhs, LpOperator op, int rhs) {
		if (!satisfied(lhs, op, rhs)) {
			throw new IllegalStateException("Unsatisfied: " + lhs + op + rhs + ".");
		}
	}

	/**
	 * <p>
	 * Tests whether the given comparison is <code>true</code>, or equivalently
	 * whether the constraints it represents is satisfied.
	 * </p>
	 * <p>
	 * Using this method requires particular caution when op is equality, because
	 * this method allows for no tolerance and will thus consider two double values
	 * as being unequal even if they differ by a very small amount.
	 * </p>
	 *
	 * @param lhs
	 *            a real number, or infinity.
	 * @param op
	 *            not <code>null</code>.
	 * @param rhs
	 *            a real number, or infinity.
	 * @return <code>true</code> iff lhs compares to rhs in the way represented by
	 *         the given operator.
	 */
	static public boolean satisfied(double lhs, LpOperator op, double rhs) {
		checkNotNull(op);
		switch (op) {
		case EQ:
			return lhs == rhs;
		case GE:
			return lhs >= rhs;
		case LE:
			return lhs <= rhs;
		default:
			throw new IllegalStateException("Unknown operator.");
		}
	}

	static public boolean satisfied(int lhs, LpOperator op, int rhs) {
		checkNotNull(op);
		switch (op) {
		case EQ:
			return lhs == rhs;
		case GE:
			return lhs >= rhs;
		case LE:
			return lhs <= rhs;
		default:
			throw new IllegalStateException("Unknown operator.");
		}
	}
}
