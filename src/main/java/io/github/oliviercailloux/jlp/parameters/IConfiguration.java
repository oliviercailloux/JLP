package io.github.oliviercailloux.jlp.parameters;

import java.math.RoundingMode;
import java.time.Duration;

/**
 *
 * Internal interface, used to avoid repeating javadoc.
 *
 * @author Olivier Cailloux
 *
 */
interface IConfiguration {

	/**
	 * Indicates whether the solver is forced to behave deterministically.
	 *
	 * @return <code>true</code> iff the solver is forced to behave
	 *         deterministically, default being <code>false</code>.
	 */
	public boolean getForceDeterministic();

	/**
	 * <p>
	 * Retrieves the maximal time that the cpu is allowed to spend for solving an
	 * mp.
	 * </p>
	 * <p>
	 * This may be rounded (using {@link RoundingMode#HALF_UP}) to the nearest
	 * second, depending on the solver, thus values such as 0.3 seconds may be
	 * rounded to zero, allowing no time for computation.
	 * </p>
	 * <p>
	 * If the value is zero (or is rounded to zero), any attempt to solve any
	 * problem will return immediately with a report of no solution found.
	 * </p>
	 *
	 * @return a non-negative duration, default being {@link Configuration#ENOUGH}.
	 */
	public Duration getMaxCpuTime();

	/**
	 * <p>
	 * Retrieves the maximal time that computation is allowed to take for solving an
	 * mp, measured in wall time.
	 * </p>
	 * <p>
	 * This may be rounded (using {@link RoundingMode#HALF_UP}) to the nearest
	 * second, depending on the solver, thus values such as 0.3 seconds may be
	 * rounded to zero, allowing no time for computation.
	 * </p>
	 * <p>
	 * If the value is zero (or is rounded to zero), any attempt to solve any
	 * problem will return immediately with a report of no solution found.
	 * </p>
	 *
	 * @return a non-negative duration, default being {@link Configuration#ENOUGH}.
	 */
	public Duration getMaxWallTime();

}
