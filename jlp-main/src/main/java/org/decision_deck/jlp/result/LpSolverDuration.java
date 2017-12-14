package org.decision_deck.jlp.result;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Immutable.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpSolverDuration {

    private final Long m_solverCpuDuration_ms;

    private final Long m_solverWallDuration_ms;

    private final Long m_threadDuration_ms;

    private final Long m_wallDuration_ms;

    /**
     * One of the parameters at least must be different than <code>null</code>.
     * 
     * @param wallDuration_ms
     *            the wall, or real-time, duration, as computed from the JVM.
     * @param threadDuration_ms
     *            the CPU duration, as computed from the JVM, taking only into account the thread that started the
     *            solver and waited for it.
     * @param solverWallDuration_ms
     *            the wall, or real-time, duration, as computed from the solver.
     * @param solverCpuDuration_ms
     *            the CPU duration, as computed from the solver (thus takes into account all the threads the solver
     *            uses).
     */
    public LpSolverDuration(Long wallDuration_ms, Long threadDuration_ms, Long solverWallDuration_ms,
	    Long solverCpuDuration_ms) {
	if (wallDuration_ms == null && threadDuration_ms == null && solverWallDuration_ms == null
		&& solverCpuDuration_ms == null) {
	    throw new IllegalArgumentException("All arguments are null - can't be.");
	}
	m_wallDuration_ms = wallDuration_ms;
	m_threadDuration_ms = threadDuration_ms;
	m_solverWallDuration_ms = solverWallDuration_ms;
	m_solverCpuDuration_ms = solverCpuDuration_ms;
    }

    /**
     * @return the solver CPU duration, if available, otherwise the solver wall duration, if available, otherwise the
     *         wall duration, if available, otherwise the thread duration.
     */
    public long getDuration_ms() {
	if (m_solverCpuDuration_ms != null) {
	    return m_solverCpuDuration_ms.longValue();
	}
	if (m_solverWallDuration_ms != null) {
	    return m_solverWallDuration_ms.longValue();
	}
	if (m_wallDuration_ms != null) {
	    return m_wallDuration_ms.longValue();
	}
	if (m_threadDuration_ms != null) {
	    return m_threadDuration_ms.longValue();
	}
	throw new IllegalStateException("Should have at least one non-null value.");
    }

    /**
     * @return the duration in CPU time as computed by the solver, or <code>null</code> if not set.
     */
    public Long getSolverCpuDuration_ms() {
	return m_solverCpuDuration_ms;
    }

    /**
     * @return the duration in wall clock time as computed by the solver, or <code>null</code> if not set.
     */
    public Long getSolverWallDuration_ms() {
	return m_solverWallDuration_ms;
    }

    /**
     * @return the duration in CPU time of the thread that started the solver, or <code>null</code> if not set.
     */
    public Long getThreadDuration_ms() {
	return m_threadDuration_ms;
    }

    /**
     * @return the duration in wall clock time, or <code>null</code> if not set.
     */
    public Long getWallDuration_ms() {
	return m_wallDuration_ms;
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.add("wall (ms)", m_wallDuration_ms);
	helper.add("thread cpu (ms)", m_threadDuration_ms);
	helper.add("solver wall (ms)", m_solverWallDuration_ms);
	helper.add("solver cpu (ms)", m_solverCpuDuration_ms);
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
