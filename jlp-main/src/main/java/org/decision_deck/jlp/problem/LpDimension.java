package org.decision_deck.jlp.problem;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The dimension of a problem, in numbers of variables and constraints.
 * Immutable.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpDimension {
	private final int m_binaries;

	private final int m_constraints;

	private final int m_continuous;

	private final int m_integersNonBinary;

	/**
	 * @param binaries
	 *            at least zero.
	 * @param integersNonBinary
	 *            at least zero.
	 * @param continuous
	 *            at least zero.
	 * @param constraints
	 *            at least zero.
	 */
	public LpDimension(int binaries, int integersNonBinary, int continuous, int constraints) {
		if (binaries < 0 || integersNonBinary < 0 || continuous < 0 || constraints < 0) {
			throw new IllegalArgumentException(
					"Must be positive or null:" + binaries + integersNonBinary + continuous + constraints + ".");
		}
		m_binaries = binaries;
		m_integersNonBinary = integersNonBinary;
		m_continuous = continuous;
		m_constraints = constraints;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LpDimension)) {
			return false;
		}
		LpDimension d2 = (LpDimension) obj;
		if (m_binaries != d2.m_binaries) {
			return false;
		}
		if (m_integersNonBinary != d2.m_integersNonBinary) {
			return false;
		}
		if (m_continuous != d2.m_continuous) {
			return false;
		}
		if (m_constraints != d2.m_constraints) {
			return false;
		}
		return true;
	}

	public int getBinaries() {
		return m_binaries;
	}

	public int getConstraints() {
		return m_constraints;
	}

	public int getContinuous() {
		return m_continuous;
	}

	public int getIntegers() {
		return m_integersNonBinary + m_binaries;
	}

	public int getIntegersNonBinary() {
		return m_integersNonBinary;
	}

	/**
	 * Retrieves the number of variables in the problem: binaries, integers non
	 * binaries, and continous.
	 * 
	 * @return at least zero.
	 */
	public int getVariables() {
		return m_integersNonBinary + m_binaries + m_continuous;
	}

	@Override
	public int hashCode() {
		final int prime = 67;
		int result = 1;
		result = prime * result + m_binaries;
		result = prime * result + m_integersNonBinary;
		result = prime * result + m_continuous;
		result = prime * result + m_constraints;
		return result;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("binaries", Integer.valueOf(m_binaries));
		helper.add("integers", Integer.valueOf(m_integersNonBinary));
		helper.add("continuous", Integer.valueOf(m_continuous));
		helper.add("constraints", Integer.valueOf(m_constraints));
		return helper.toString();
	}
}
