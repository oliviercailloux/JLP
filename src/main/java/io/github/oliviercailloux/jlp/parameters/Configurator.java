package io.github.oliviercailloux.jlp.parameters;

import static com.google.common.base.Preconditions.checkArgument;

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
	/**
	 * Returns a configurator that is a copy of the given source, in the sense that
	 * it contains the same data, but is not linked to the source: modifying the
	 * resulting configurator will not change the source.
	 *
	 * @param source not <code>null</code>.
	 */
	public static Configurator copyOf(Configurator source) {
		final Configurator newConfigurator = new Configurator();
		newConfigurator.setForceDeterministic(source.getForceDeterministic());
		newConfigurator.setMaxCpuTime(source.getMaxCpuTime());
		newConfigurator.setMaxWallTime(source.getMaxWallTime());
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

	/**
	 * Returns an immutable configuration that contains the data currently in this
	 * configurator.
	 *
	 * @return not <code>null</code>.
	 */
	public Configuration build() {
		return new Configuration(copyOf(this));
	}

	/**
	 * Restores the default values.
	 */
	public void clear() {
		maxCpuTime = Configuration.DEFAULT_MAX_CPU_TIME;
		maxWallTime = Configuration.DEFAULT_MAX_WALL_TIME;
		forceDeterministic = Configuration.DEFAULT_FORCE_DETERMINISTIC;
	}

	/**
	 * Two Configurators are equal when they have equal values for all their
	 * parameters.
	 *
	 * @param o2 the reference object with which to compare.
	 * @return <code>true</code> iff this object is the same as the obj argument.
	 */
	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Configurator)) {
			return false;
		}
		final Configurator c2 = (Configurator) o2;
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
	 * <code>false</code>, lets the solver behave as it considers best.
	 *
	 * @param forceDeterministic <code>true</code> to force the solve to behave
	 *                           deterministically.
	 */
	public Configurator setForceDeterministic(boolean forceDeterministic) {
		this.forceDeterministic = forceDeterministic;
		return this;
	}

	/**
	 * Sets the maximal time that the cpu is allowed to spend for solving an mp.
	 *
	 * @param maxCpuTime must be non-negative.
	 */
	public Configurator setMaxCpuTime(Duration maxCpuTime) {
		checkArgument(!maxCpuTime.isNegative());
		this.maxCpuTime = maxCpuTime;
		return this;
	}

	/**
	 * Sets the maximal time that computation is allowed to take for solving an mp,
	 * measured in wall time.
	 *
	 * @param maxWallTime must be non-negative.
	 */
	public Configurator setMaxWallTime(Duration maxWallTime) {
		checkArgument(!maxWallTime.isNegative());
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
