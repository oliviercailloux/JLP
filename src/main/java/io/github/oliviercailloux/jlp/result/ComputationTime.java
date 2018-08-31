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
	 * @param wallTime a non-negative duration.
	 * @param cpuTime  a non-negative duration.
	 */
	public static ComputationTime of(Duration wallTime, Duration cpuTime) {
		return new ComputationTime(wallTime, Optional.of(cpuTime));
	}

	/**
	 * Returns a computation time containing the given measure.
	 *
	 * @param wallTime a non-negative duration.
	 */
	public static ComputationTime ofWallTime(Duration wallTime) {
		return new ComputationTime(wallTime, Optional.empty());
	}

	private Optional<Duration> cpuTime;

	private Duration wallTime;

	/**
	 * @param wallTime not <code>null</code>, a non-negative duration.
	 * @param cpuTime  not <code>null</code>, a non-negative duration or an empty
	 *                 optional.
	 */
	private ComputationTime(Duration wallTime, Optional<Duration> cpuTime) {
		this.cpuTime = requireNonNull(cpuTime);
		if (cpuTime.isPresent()) {
			checkArgument(!cpuTime.get().isNegative());
		}
		this.wallTime = requireNonNull(wallTime);
		checkArgument(!wallTime.isNegative());
	}

	/**
	 * Returns the CPU time, if available, taking into account all the threads the
	 * solver has used.
	 *
	 * @return a non-negative duration, or an empty optional if this information is
	 *         not available.
	 */
	public Optional<Duration> getCpuTime() {
		return cpuTime;
	}

	/**
	 * Returns the wall, or real-time, duration of the computation.
	 *
	 * @return a non-negative duration.
	 */
	public Duration getWallTime() {
		return wallTime;
	}

}
