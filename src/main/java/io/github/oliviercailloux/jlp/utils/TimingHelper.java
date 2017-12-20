package io.github.oliviercailloux.jlp.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

import io.github.oliviercailloux.jlp.parameters.TimingType;
import io.github.oliviercailloux.jlp.result.SolverDuration;

public class TimingHelper {
	private Long cpuEnd_ns;

	private Long cpuStart_ns;

	private boolean ended;

	/**
	 * <code>null</code> iff cpu timing is not supported.
	 */
	private final ThreadMXBean mgmt;

	private Double solverCpuEnd_ms;

	private Double solverCpuStart_ms;

	private final Map<TimingType, Long> solverDurations_ms = new HashMap<>();

	private Double solverWallEnd_ms;

	private Double solverWallStart_ms;

	private long wallEnd_ns;

	private long wallStart_ns;

	public TimingHelper() {
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		if (threadMXBean.isCurrentThreadCpuTimeSupported()) {
			mgmt = threadMXBean;
		} else {
			mgmt = null;
		}

		wallStart_ns = -1;
		cpuStart_ns = null;
		solverWallStart_ms = null;
		solverCpuStart_ms = null;

		wallEnd_ns = -1;
		cpuEnd_ns = null;
		solverWallEnd_ms = null;
		solverCpuEnd_ms = null;

		ended = false;
	}

	public Long getCpuDuration_ms() {
		assertEndCalled();
		return mgmt == null ? null : Long.valueOf(Math.round((cpuEnd_ns.longValue() - cpuStart_ns.longValue()) / 1e6d));
	}

	public SolverDuration getDuration() {
		assertEndCalled();
		return SolverDuration.of(Long.valueOf(getWallDuration_ms()), getCpuDuration_ms(), getSolverWallDuration_ms(),
				getSolverCpuDuration_ms());
	}

	public Long getSolverCpuDuration_ms() {
		assertEndCalled();
		return solverDurations_ms.get(TimingType.CPU_TIMING);
	}

	public Double getSolverCpuEnd_ms() {
		return solverCpuEnd_ms;
	}

	public Double getSolverCpuStart_ms() {
		return solverCpuStart_ms;
	}

	public Long getSolverWallDuration_ms() {
		assertEndCalled();
		return solverDurations_ms.get(TimingType.WALL_TIMING);
	}

	public Double getSolverWallEnd_ms() {
		return solverWallEnd_ms;
	}

	public Double getSolverWallStart_ms() {
		return solverWallStart_ms;
	}

	public long getWallDuration_ms() {
		assertEndCalled();
		return Math.round((wallEnd_ns - wallStart_ns) / 1e6d);
	}

	/**
	 * Tests whether the Java virtual machine supports CPU time measurement. This
	 * assumes that this object is used in a single thread. If this method is called
	 * in a different thread than the thread that has created the object, or than
	 * the thread that has been used to call other methods on this object, the
	 * result is not guaranteed to be correct.
	 *
	 * @return <code>true</code> if the Java virtual machine supports CPU time
	 *         measurement for current thread; <code>false</code> otherwise.
	 */
	public boolean isCpuTimingSupported() {
		return mgmt != null;
	}

	public void setSolverCpuEnd_ms(double solverCpuEnd_ms) {
		setSolverEnd_ms(TimingType.CPU_TIMING, solverCpuEnd_ms);
	}

	public void setSolverCpuStart_ms(double solverCpuStart_ms) {
		setSolverStart_ms(TimingType.CPU_TIMING, solverCpuStart_ms);
	}

	public void setSolverDuration_ms(TimingType timingType, double solverDuration_ms) {
		if (timingType == null) {
			throw new NullPointerException("" + timingType + solverDuration_ms);
		}
		solverDurations_ms.put(timingType, Long.valueOf(Math.round(solverDuration_ms)));
	}

	public void setSolverEnd_ms(TimingType type, double solverEnd_ms) {
		final Long duration_ms;
		switch (type) {
		case WALL_TIMING:
			solverWallEnd_ms = Double.valueOf(solverEnd_ms);
			duration_ms = (solverWallStart_ms == null || solverWallEnd_ms == null) ? null
					: Long.valueOf(Math.round((solverWallEnd_ms.doubleValue() - solverWallStart_ms.doubleValue())));
			break;
		case CPU_TIMING:
			solverCpuEnd_ms = Double.valueOf(solverEnd_ms);
			duration_ms = (solverCpuStart_ms == null || solverCpuEnd_ms == null) ? null
					: Long.valueOf(Math.round((solverCpuEnd_ms.doubleValue() - solverCpuStart_ms.doubleValue())));
			break;
		default:
			throw new IllegalStateException("Unknown timing type.");
		}
		solverDurations_ms.put(type, duration_ms);
	}

	public void setSolverStart_ms(TimingType type, double solverStart_ms) {
		final Long duration_ms;
		switch (type) {
		case WALL_TIMING:
			solverWallStart_ms = Double.valueOf(solverStart_ms);
			duration_ms = (solverWallStart_ms == null || solverWallEnd_ms == null) ? null
					: Long.valueOf(Math.round((solverWallEnd_ms.doubleValue() - solverWallStart_ms.doubleValue())));
			break;
		case CPU_TIMING:
			solverCpuStart_ms = Double.valueOf(solverStart_ms);
			duration_ms = (solverCpuStart_ms == null || solverCpuEnd_ms == null) ? null
					: Long.valueOf(Math.round((solverCpuEnd_ms.doubleValue() - solverCpuStart_ms.doubleValue())));
			break;
		default:
			throw new IllegalStateException("Unknown timing type.");
		}
		solverDurations_ms.put(type, duration_ms);
	}

	public void setSolverWallEnd_ms(double solverWallEnd_ms) {
		setSolverEnd_ms(TimingType.WALL_TIMING, solverWallEnd_ms);
	}

	public void setSolverWallStart_ms(double solverWallStart_ms) {
		setSolverStart_ms(TimingType.WALL_TIMING, solverWallStart_ms);
	}

	public void start() {
		cpuStart_ns = mgmt == null ? null : Long.valueOf(mgmt.getCurrentThreadCpuTime());
		wallStart_ns = System.nanoTime();
	}

	public void stop() {
		wallEnd_ns = System.nanoTime();
		cpuEnd_ns = mgmt == null ? null : Long.valueOf(mgmt.getCurrentThreadCpuTime());
		ended = true;
	}

	private void assertEndCalled() {
		if (!ended) {
			throw new IllegalStateException("End has not been called before duration is asked.");
		}
	}
}
