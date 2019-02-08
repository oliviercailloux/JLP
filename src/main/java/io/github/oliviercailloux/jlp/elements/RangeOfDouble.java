package io.github.oliviercailloux.jlp.elements;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Range;

/**
 * <p>
 * This class provides constructors for obtaining ranges containing finite
 * values only. In other words, all ranges provided by this class exclude NaN
 * and infinite values.
 * </p>
 * <p>
 * Ranges in this library are expected to hold finite values only. Thus, it is
 * advised to use the constructors of this class rather than the ones provided
 * by {@link Range}. For example, {@link Range#all()} is not considered a valid
 * range by {@link Variable#of} because that range includes infinite double
 * values and NaN. Rather use {@link RangeOfDouble#ALL_FINITE} if you want to
 * indicate that all finite values are allowed.
 * </p>
 * <p>
 * The provided ranges are not necessarily bounded, for example,
 * {@link #ALL_FINITE} is (conceptually) not bounded.
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
 * There are two ways of representing all finite double values as a range: (-∞,
 * +∞) or [-max, max], where max = {@link Double#MAX_VALUE}. This library favors
 * the first one, for clarity and in order to ensure uniqueness of
 * representation.
 * </p>
 * <p>
 * This class permits to create closed ranges only, in the mathematical sense
 * that they contain the limits of internal sequences, but not in the Guava
 * sense of the range including both its endpoints. For example,
 * {@link #ALL_FINITE} is a range that is closed mathematically, but that does
 * not enclose its endpoints, which are infinite values.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class RangeOfDouble {
	private RangeOfDouble() {
		/** Non instantiable. */
	}

	/**
	 * The open range (−∞, +∞) containing all finite double values.
	 *
	 */
	public static final Range<Double> ALL_FINITE = Range.open(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

	/**
	 * The range [0, +∞) containing all finite positive double values, including
	 * (positive) zero.
	 */
	public static final Range<Double> NON_NEGATIVE = atLeast(0d);

	/**
	 * The closed [0, 1] range.
	 */
	public static final Range<Double> ZERO_ONE_RANGE = closed(0d, 1d);

	/**
	 * Returns a range that contains all finite values equal to or greater than
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
	 * Returns a range that is partly open, open, or closed, depending on the
	 * arguments.
	 *
	 * @param lower {@link Double#NEGATIVE_INFINITY} or a finite number.
	 * @param upper {@link Double#POSITIVE_INFINITY} or a finite number.
	 * @return the corresponding range.
	 */
	public static Range<Double> using(double lower, double upper) {
		if (lower == Double.NEGATIVE_INFINITY && upper == Double.POSITIVE_INFINITY) {
			return ALL_FINITE;
		}
		if (lower == Double.NEGATIVE_INFINITY && Double.isFinite(upper)) {
			return atMost(upper);
		}
		if (Double.isFinite(lower) && upper == Double.POSITIVE_INFINITY) {
			return atLeast(lower);
		}
		if (Double.isFinite(lower) && Double.isFinite(upper)) {
			return closed(lower, upper);
		}
		throw new IllegalArgumentException();
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

	/**
	 * <p>
	 * A string representation of this range as a range of finite values adopting
	 * the conventions of this class, rather than the conventions of
	 * <a href= "https://github.com/google/guava/wiki/RangesExplained">Guava</a>
	 * (for the latter, use {@link Range#toString()}). The difference is that for
	 * this method, the symbol ∞ represents a double infinite value.
	 * </p>
	 * <p>
	 * For example, given {@link #ALL_FINITE}, this method returns “(−∞..+∞)”
	 * whereas {@link Range#toString()} returns “(-Infinity..Infinity)”.
	 * </p>
	 *
	 * @param range a range as produced by this class.
	 * @return a string representation of the given range.
	 */
	public static String toString(Range<Double> range) {
		assert range.hasLowerBound();
		assert range.hasUpperBound();
		assert range.lowerEndpoint().equals(Double.NEGATIVE_INFINITY) || Double.isFinite(range.lowerEndpoint());
		assert range.upperEndpoint().equals(Double.POSITIVE_INFINITY) || Double.isFinite(range.upperEndpoint());

		final String start;
		switch (range.lowerBoundType()) {
		case CLOSED:
			start = "[";
			break;
		case OPEN:
			start = "(";
			break;
		default:
			throw new AssertionError();
		}

		final Double dbl1 = range.lowerEndpoint();
		final String val1 = dbl1.equals(Double.NEGATIVE_INFINITY) ? "−∞" : dbl1.toString();
		final Double dbl2 = range.upperEndpoint();
		final String val2 = dbl2.equals(Double.POSITIVE_INFINITY) ? "+∞" : dbl2.toString();

		final String end;
		switch (range.upperBoundType()) {
		case CLOSED:
			end = "]";
			break;
		case OPEN:
			end = ")";
			break;
		default:
			throw new AssertionError();
		}

		return String.format("%s%s..%s%s", start, val1, val2, end);
	}
}
