package io.github.oliviercailloux.jlp.problem;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The dimension of a problem, in numbers of variables and constraints.
 * Immutable.
 *
 * @author Olivier Cailloux
 *
 */
public class MPDimension {
	private final int binaries;

	private final int constraints;

	private final int continuous;

	private final int integersNonBinary;

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
	public MPDimension(int binaries, int integersNonBinary, int continuous, int constraints) {
		if (binaries < 0 || integersNonBinary < 0 || continuous < 0 || constraints < 0) {
			throw new IllegalArgumentException(
					"Must be positive or null:" + binaries + integersNonBinary + continuous + constraints + ".");
		}
		this.binaries = binaries;
		this.integersNonBinary = integersNonBinary;
		this.continuous = continuous;
		this.constraints = constraints;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MPDimension)) {
			return false;
		}
		MPDimension d2 = (MPDimension) obj;
		if (binaries != d2.binaries) {
			return false;
		}
		if (integersNonBinary != d2.integersNonBinary) {
			return false;
		}
		if (continuous != d2.continuous) {
			return false;
		}
		if (constraints != d2.constraints) {
			return false;
		}
		return true;
	}

	public int getBinaries() {
		return binaries;
	}

	public int getConstraints() {
		return constraints;
	}

	public int getContinuous() {
		return continuous;
	}

	public int getIntegers() {
		return integersNonBinary + binaries;
	}

	public int getIntegersNonBinary() {
		return integersNonBinary;
	}

	/**
	 * Retrieves the number of variables in the problem: binaries, integers non
	 * binaries, and continous.
	 *
	 * @return at least zero.
	 */
	public int getVariables() {
		return integersNonBinary + binaries + continuous;
	}

	@Override
	public int hashCode() {
		final int prime = 67;
		int result = 1;
		result = prime * result + binaries;
		result = prime * result + integersNonBinary;
		result = prime * result + continuous;
		result = prime * result + constraints;
		return result;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("binaries", Integer.valueOf(binaries));
		helper.add("integers", Integer.valueOf(integersNonBinary));
		helper.add("continuous", Integer.valueOf(continuous));
		helper.add("constraints", Integer.valueOf(constraints));
		return helper.toString();
	}
}
