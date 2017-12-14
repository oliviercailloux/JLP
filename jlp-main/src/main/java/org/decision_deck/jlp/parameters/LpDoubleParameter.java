package org.decision_deck.jlp.parameters;

import java.math.RoundingMode;

import org.decision_deck.jlp.LpSolverException;

/**
 * <p>
 * Contains the parameters accepting a real number as value.
 * </p>
 * <p>
 * Max cpu seconds and max wall seconds should <i>not</i> be both set. One or
 * zero of these two parameters may be set.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public enum LpDoubleParameter {
	MAX_CPU_SECONDS, MAX_TREE_SIZE_MB, MAX_MEMORY_MB,
	/**
	 * Caution must be exercised when using very small values for this parameter.
	 * The value will be rounded ({@link RoundingMode#HALF_UP}) if the underlying
	 * solver accepts only integer number of seconds. In that case and if the
	 * timeout is less than 0.5 seconds, it would round to zero seconds, and a
	 * {@link LpSolverException} would be raised when solving.
	 */
	MAX_WALL_SECONDS
}
