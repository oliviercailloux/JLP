package io.github.oliviercailloux.jlp.result;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Immutable.
 *
 * @author Olivier Cailloux
 *
 */
public class LpSolverDuration {

	private final Long solverCpuDuration_ms;

	private final Long solverWallDuration_ms;

	private final Long threadDuration_ms;

	private final Long wallDuration_ms;

	/**
	 * One of the parameters at least must be different than <code>null</code>.
	 *
	 * @param wallDuration_ms
	 *            the wall, or real-time, duration, as computed from the JVM.
	 * @param threadDuration_ms
	 *            the CPU duration, as computed from the JVM, taking only into
	 *            account the thread that started the solver and waited for it.
	 * @param solverWallDuration_ms
	 *            the wall, or real-time, duration, as computed from the solver.
	 * @param solverCpuDuration_ms
	 *            the CPU duration, as computed from the solver (thus takes into
	 *            account all the threads the solver uses).
	 */
	public LpSolverDuration(Long wallDuration_ms, Long threadDuration_ms, Long solverWallDuration_ms,
			Long solverCpuDuration_ms) {
		if (wallDuration_ms == null && threadDuration_ms == null && solverWallDuration_ms == null
				&& solverCpuDuration_ms == null) {
			throw new IllegalArgumentException("All arguments are null - can't be.");
		}
		this.wallDuration_ms = wallDuration_ms;
		this.threadDuration_ms = threadDuration_ms;
		this.solverWallDuration_ms = solverWallDuration_ms;
		this.solverCpuDuration_ms = solverCpuDuration_ms;
	}

	/**
	 * @return the solver CPU duration, if available, otherwise the solver wall
	 *         duration, if available, otherwise the wall duration, if available,
	 *         otherwise the thread duration.
	 */
	public long getDuration_ms() {
		if (solverCpuDuration_ms != null) {
			return solverCpuDuration_ms.longValue();
		}
		if (solverWallDuration_ms != null) {
			return solverWallDuration_ms.longValue();
		}
		if (wallDuration_ms != null) {
			return wallDuration_ms.longValue();
		}
		if (threadDuration_ms != null) {
			return threadDuration_ms.longValue();
		}
		throw new IllegalStateException("Should have at least one non-null value.");
	}

	/**
	 * @return the duration in CPU time as computed by the solver, or
	 *         <code>null</code> if not set.
	 */
	public Long getSolverCpuDuration_ms() {
		return solverCpuDuration_ms;
	}

	/**
	 * @return the duration in wall clock time as computed by the solver, or
	 *         <code>null</code> if not set.
	 */
	public Long getSolverWallDuration_ms() {
		return solverWallDuration_ms;
	}

	/**
	 * @return the duration in CPU time of the thread that started the solver, or
	 *         <code>null</code> if not set.
	 */
	public Long getThreadDuration_ms() {
		return threadDuration_ms;
	}

	/**
	 * @return the duration in wall clock time, or <code>null</code> if not set.
	 */
	public Long getWallDuration_ms() {
		return wallDuration_ms;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("wall (ms)", wallDuration_ms);
		helper.add("thread cpu (ms)", threadDuration_ms);
		helper.add("solver wall (ms)", solverWallDuration_ms);
		helper.add("solver cpu (ms)", solverCpuDuration_ms);
		return helper.toString();
		// StringBuilder builder = new StringBuilder();
		// builder.append("Duration [");
		// boolean comma = false;
		// if (m_wallDuration_ms != null) {
		// builder.append("wall=");
		// builder.append(m_wallDuration_ms);
		// builder.append(" ms");
		// comma = true;
		// }
		// if (m_threadDuration_ms != null) {
		// if (comma) {
		// builder.append(", ");
		// }
		// builder.append("thread=");
		// builder.append(m_threadDuration_ms);
		// builder.append(" ms");
		// comma = true;
		// }
		// if (m_solverWallDuration_ms != null) {
		// if (comma) {
		// builder.append(", ");
		// }
		// builder.append("solver wall=");
		// builder.append(m_solverWallDuration_ms);
		// builder.append(" ms");
		// comma = true;
		// }
		// if (m_solverCpuDuration_ms != null) {
		// if (comma) {
		// builder.append(", ");
		// }
		// builder.append("solver cpu=");
		// builder.append(m_solverCpuDuration_ms);
		// builder.append(" ms");
		// }
		// builder.append("]");
		// return builder.toString();
	}

}
