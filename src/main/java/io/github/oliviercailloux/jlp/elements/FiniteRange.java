package io.github.oliviercailloux.jlp.elements;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Range;

/**
 * <p>
 * This class provides constructors for obtaining ranges containing finite
 * values only. (The provided ranges are not necessarily bounded, however.)
 * </p>
 * <p>
 * Ranges in this library are expected to hold finite values only. Thus, it is
 * advised to use the constructors of this class rather than the ones provided
 * by {@link Range}. For example, {@link Range#all()} is not considered a valid
 * range by {@link Variable#of} because that range includes infinite double
 * values and NaN. Rather use {@link FiniteRange#ALL_FINITE} if you want to
 * indicate that all finite values are allowed.
 * </p>
 * <p>
 * All ranges provided by this class exclude NaN and infinite values.
 * </p>
 * <p>
 * Documentation in this package use the symbol ∞ to represent Double infinity.
 * Thus this represents a value strictly lower or strictly greater, depending on
 * the sign, than all <em>finite</em> double values, contrary to Guava <a href=
 * "https://github.com/google/guava/wiki/RangesExplained">RangesExplained</a>
 * which uses the symbol to indicate something lower or greater than all
 * possible values in a given type.
 * </p>
 * <p>
 * There are two ways of representing all finite values as a range: (-∞, +∞) or
 * [-max, max], where max = {@link Double#MAX_VALUE}. This library favors the
 * first one, for clarity and in order to ensure uniqueness of representation.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class FiniteRange {
	/**
	 * The open range (-∞, +∞) containing all finite double values.
	 *
	 */
	public static final Range<Double> ALL_FINITE = Range.open(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

	/**
	 * The range [0, +∞) containing all finite positive double values, including
	 * (positive) zero.
	 */
	public static final Range<Double> NON_NEGATIVE = atLeast(0d);

	/**
	 * The closed [0-1] range.
	 */
	public static final Range<Double> ZERO_ONE_RANGE = closed(0d, 1d);

	/**
	 * Returns a range that contains all finite values greater than or equal to
	 * <code>lower</code>.
	 *
	 * @param lower a finite number.
	 * @return the range [lower, +∞).
	 */
	public static Range<Double> atLeast(double lower) {
		checkArgument(Double.isFinite(lower));
		return Range.closedOpen(lower, Double.POSITIVE_INFINITY);
	}

	/**
	 * Returns a range that contains all finite values lower than or equal to
	 * <code>upper</code>.
	 *
	 * @param upper a finite number.
	 * @return the range (-∞, upper].
	 */
	public static Range<Double> atMost(double upper) {
		checkArgument(Double.isFinite(upper));
		return Range.openClosed(Double.NEGATIVE_INFINITY, upper);
	}

	/**
	 * Returns a range that contains all finite values at least <code>lower</code>
	 * and at most <code>upper</code>. This has the same effect than
	 * {@link Range#closed} and is provided for completeness (so that the
	 * constructors of this class permit to create all possible kinds of finite
	 * ranges).
	 *
	 * @param lower a finite number.
	 * @param upper a finite number, greater than or equal to <code>lower</code>.
	 * @return the range [lower, upper].
	 */
	public static Range<Double> closed(double lower, double upper) {
		checkArgument(Double.isFinite(lower));
		checkArgument(Double.isFinite(upper));
		return Range.closed(lower, upper);
	}
}
