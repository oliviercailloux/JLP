/**
 * Copyright © 2010-2012 Olivier Cailloux
 *
 * 	This file is part of JLP.
 *
 * 	JLP is free software: you can redistribute it and/or modify it under the
 * 	terms of the GNU Lesser General Public License version 3 as published by
 * 	the Free Software Foundation.
 *
 * 	JLP is distributed in the hope that it will be useful, but WITHOUT ANY
 * 	WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * 	FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * 	more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public License
 * 	along with JLP. If not, see <http://www.gnu.org/licenses/>.
 */
package org.decision_deck.jlp.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

import org.decision_deck.jlp.parameters.LpTimingType;
import org.decision_deck.jlp.result.LpSolverDuration;

public class TimingHelper {
    private Long m_cpuEnd_ns;
    private Long m_cpuStart_ns;
    private boolean m_ended;
    /**
     * <code>null</code> iff cpu timing is not supported.
     */
    private final ThreadMXBean m_mgmt;
    private Double m_solverCpuEnd_ms;
    private Double m_solverCpuStart_ms;
    private final Map<LpTimingType, Long> m_solverDurations_ms = new HashMap<LpTimingType, Long>();
    private Double m_solverWallEnd_ms;
    private Double m_solverWallStart_ms;
    private long m_wallEnd_ns;
    private long m_wallStart_ns;

    public TimingHelper() {
	final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	if (threadMXBean.isCurrentThreadCpuTimeSupported()) {
	    m_mgmt = threadMXBean;
	} else {
	    m_mgmt = null;
	}

	m_wallStart_ns = -1;
	m_cpuStart_ns = null;
	m_solverWallStart_ms = null;
	m_solverCpuStart_ms = null;

	m_wallEnd_ns = -1;
	m_cpuEnd_ns = null;
	m_solverWallEnd_ms = null;
	m_solverCpuEnd_ms = null;

	m_ended = false;
    }

    private void assertEndCalled() {
	if (!m_ended) {
	    throw new IllegalStateException("End has not been called before duration is asked.");
	}
    }

    public Long getCpuDuration_ms() {
	assertEndCalled();
	return m_mgmt == null ? null : Long
		.valueOf(Math.round((m_cpuEnd_ns.longValue() - m_cpuStart_ns.longValue()) / 1e6d));
    }

    public LpSolverDuration getDuration() {
	assertEndCalled();
	return new LpSolverDuration(Long.valueOf(getWallDuration_ms()), getCpuDuration_ms(),
		getSolverWallDuration_ms(), getSolverCpuDuration_ms());
    }

    public Long getSolverCpuDuration_ms() {
	assertEndCalled();
	return m_solverDurations_ms.get(LpTimingType.CPU_TIMING);
    }

    public Double getSolverCpuEnd_ms() {
	return m_solverCpuEnd_ms;
    }

    public Double getSolverCpuStart_ms() {
	return m_solverCpuStart_ms;
    }

    public Long getSolverWallDuration_ms() {
	assertEndCalled();
	return m_solverDurations_ms.get(LpTimingType.WALL_TIMING);
    }

    public Double getSolverWallEnd_ms() {
	return m_solverWallEnd_ms;
    }

    public Double getSolverWallStart_ms() {
	return m_solverWallStart_ms;
    }

    public long getWallDuration_ms() {
	assertEndCalled();
	return Math.round((m_wallEnd_ns - m_wallStart_ns) / 1e6d);
    }

    /**
     * Tests whether the Java virtual machine supports CPU time measurement. This assumes that this object is used in a
     * single thread. If this method is called in a different thread than the thread that has created the object, or
     * than the thread that has been used to call other methods on this object, the result is not guaranteed to be
     * correct.
     * 
     * @return <code>true</code> if the Java virtual machine supports CPU time measurement for current thread;
     *         <code>false</code> otherwise.
     */
    public boolean isCpuTimingSupported() {
	return m_mgmt != null;
    }

    public void setSolverCpuEnd_ms(double solverCpuEnd_ms) {
	setSolverEnd_ms(LpTimingType.CPU_TIMING, solverCpuEnd_ms);
    }

    public void setSolverCpuStart_ms(double solverCpuStart_ms) {
	setSolverStart_ms(LpTimingType.CPU_TIMING, solverCpuStart_ms);
    }

    public void setSolverDuration_ms(LpTimingType timingType, double solverDuration_ms) {
	if (timingType == null) {
	    throw new NullPointerException("" + timingType + solverDuration_ms);
	}
	m_solverDurations_ms.put(timingType, Long.valueOf(Math.round(solverDuration_ms)));
    }

    public void setSolverEnd_ms(LpTimingType type, double solverEnd_ms) {
	final Long duration_ms;
	switch (type) {
	case WALL_TIMING:
	    m_solverWallEnd_ms = Double.valueOf(solverEnd_ms);
	    duration_ms = (m_solverWallStart_ms == null || m_solverWallEnd_ms == null) ? null : Long.valueOf(Math
		    .round((m_solverWallEnd_ms.doubleValue() - m_solverWallStart_ms.doubleValue())));
	    break;
	case CPU_TIMING:
	    m_solverCpuEnd_ms = Double.valueOf(solverEnd_ms);
	    duration_ms = (m_solverCpuStart_ms == null || m_solverCpuEnd_ms == null) ? null : Long.valueOf(Math
		    .round((m_solverCpuEnd_ms.doubleValue() - m_solverCpuStart_ms.doubleValue())));
	    break;
	default:
	    throw new IllegalStateException("Unknown timing type.");
	}
	m_solverDurations_ms.put(type, duration_ms);
    }

    public void setSolverStart_ms(LpTimingType type, double solverStart_ms) {
	final Long duration_ms;
	switch (type) {
	case WALL_TIMING:
	    m_solverWallStart_ms = Double.valueOf(solverStart_ms);
	    duration_ms = (m_solverWallStart_ms == null || m_solverWallEnd_ms == null) ? null : Long.valueOf(Math
		    .round((m_solverWallEnd_ms.doubleValue() - m_solverWallStart_ms.doubleValue())));
	    break;
	case CPU_TIMING:
	    m_solverCpuStart_ms = Double.valueOf(solverStart_ms);
	    duration_ms = (m_solverCpuStart_ms == null || m_solverCpuEnd_ms == null) ? null : Long.valueOf(Math
		    .round((m_solverCpuEnd_ms.doubleValue() - m_solverCpuStart_ms.doubleValue())));
	    break;
	default:
	    throw new IllegalStateException("Unknown timing type.");
	}
	m_solverDurations_ms.put(type, duration_ms);
    }

    public void setSolverWallEnd_ms(double solverWallEnd_ms) {
	setSolverEnd_ms(LpTimingType.WALL_TIMING, solverWallEnd_ms);
    }

    public void setSolverWallStart_ms(double solverWallStart_ms) {
	setSolverStart_ms(LpTimingType.WALL_TIMING, solverWallStart_ms);
    }

    public void start() {
	m_cpuStart_ns = m_mgmt == null ? null : Long.valueOf(m_mgmt.getCurrentThreadCpuTime());
	m_wallStart_ns = System.nanoTime();
    }

    public void stop() {
	m_wallEnd_ns = System.nanoTime();
	m_cpuEnd_ns = m_mgmt == null ? null : Long.valueOf(m_mgmt.getCurrentThreadCpuTime());
	m_ended = true;
    }
}
