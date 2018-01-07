package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Optional;

/**
 * Immutable.
 *
 * @author Olivier Cailloux
 *
 */
public class ComputationTime {

	/**
	 * Returns a computation time containing the two given measures.
	 *
	 * @param cpuTime
	 *            a strictly positive duration.
	 * @param wallTime
	 *            a strictly positive duration.
	 */
	static public ComputationTime of(Duration cpuTime, Duration wallTime) {
		return new ComputationTime(Optional.of(cpuTime), wallTime);
	}

	/**
	 * Returns a computation time containing the given measure.
	 *
	 * @param wallTime
	 *            a strictly positive duration.
	 */
	static public ComputationTime ofWallTime(Duration wallTime) {
		return new ComputationTime(Optional.empty(), wallTime);
	}

	private Optional<Duration> cpuTime;

	private Duration wallTime;

	/**
	 * @param cpuTime
	 *            not <code>null</code>, a strictly positive duration or an empty
	 *            optional.
	 * @param wallTime
	 *            not <code>null</code>, a strictly positive duration.
	 */
	private ComputationTime(Optional<Duration> cpuTime, Duration wallTime) {
		this.cpuTime = requireNonNull(cpuTime);
		if (cpuTime.isPresent()) {
			checkArgument(cpuTime.get().negated().isNegative());
		}
		this.wallTime = requireNonNull(wallTime);
		checkArgument(wallTime.negated().isNegative());
	}

	/**
	 * Returns the CPU time, if available, taking into account all the threads the
	 * solver has used.
	 *
	 * @return a strictly positive duration, or an empty optional if this
	 *         information is not available.
	 */
	public Optional<Duration> getCpuTime() {
		return cpuTime;
	}

	/**
	 * Returns the wall, or real-time, duration of the computation.
	 *
	 * @return a strictly positive duration.
	 */
	public Duration getWallTime() {
		return wallTime;
	}

}
