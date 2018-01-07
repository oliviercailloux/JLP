package io.github.oliviercailloux.jlp.parameters;

import java.time.Duration;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * <p>
 * Builds {@link Configuration} objects.
 * </p>
 * <p>
 * To reset a parameter to its default value, call the appropriate setter with
 * the appropriate default value found in {@link Configuration}.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Configurator implements IConfiguration {
	public static Configurator copyOf(Configurator configurator) {
		final Configurator newConfigurator = new Configurator();
		newConfigurator.setForceDeterministic(configurator.getForceDeterministic());
		newConfigurator.setMaxCpuTime(configurator.getMaxCpuTime());
		newConfigurator.setMaxWallTime(configurator.getMaxWallTime());
		return newConfigurator;
	}

	private boolean forceDeterministic;

	/**
	 * Not <code>null</code>.
	 */
	private Duration maxCpuTime;

	/**
	 * Not <code>null</code>.
	 */
	private Duration maxWallTime;

	Configurator() {
		clear();
	}

	public Configuration build() {
		return new Configuration(copyOf(this));
	}

	public void clear() {
		maxCpuTime = Configuration.ENOUGH;
		maxWallTime = Configuration.ENOUGH;
		forceDeterministic = Configuration.DEFAULT_FORCE_DETERMINISTIC;
	}

	/**
	 * Two Configurators are equal when they have equal values for all their
	 * parameters.
	 *
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> iff this object is the same as the obj argument.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Configurator)) {
			return false;
		}
		final Configurator c2 = (Configurator) obj;
		return this.forceDeterministic == c2.forceDeterministic && this.maxCpuTime.equals(c2.maxCpuTime)
				&& this.maxWallTime.equals(c2.maxWallTime);
	}

	@Override
	public boolean getForceDeterministic() {
		return forceDeterministic;
	}

	@Override
	public Duration getMaxCpuTime() {
		return maxCpuTime;
	}

	@Override
	public Duration getMaxWallTime() {
		return maxWallTime;
	}

	@Override
	public int hashCode() {
		return Objects.hash(forceDeterministic, maxCpuTime, maxWallTime);
	}

	/**
	 * If <code>true</code>, forces the solver to behave deterministically, if
	 * <code>false</code>, let the solver behaves as it considers best.
	 *
	 * @param forceDeterministic
	 *            <code>true</code> to force the solve to behave deterministically.
	 */
	public Configurator setForceDeterministic(boolean forceDeterministic) {
		this.forceDeterministic = forceDeterministic;
		return this;
	}

	/**
	 * Sets the maximal time that the cpu is allowed to spend for solving an mp.
	 *
	 * @param maxCpuTime
	 *            must be strictly positive.
	 */
	public Configurator setMaxCpuTime(Duration maxCpuTime) {
		this.maxCpuTime = maxCpuTime;
		return this;
	}

	/**
	 * Sets the maximal time that computation is allowed to take for solving an mp,
	 * measured in wall time.
	 *
	 * @param maxWallTime
	 *            must be strictly positive.
	 */
	public Configurator setMaxWallTime(Duration maxWallTime) {
		this.maxWallTime = maxWallTime;
		return this;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		debugTo(helper);
		return helper.toString();
	}

	void debugTo(ToStringHelper helper) {
		helper.add("force deterministic", forceDeterministic);
		helper.add("max cpu time", maxCpuTime);
		helper.add("max wall time", maxWallTime);
	}
}
