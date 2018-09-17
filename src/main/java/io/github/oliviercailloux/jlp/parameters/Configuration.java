package io.github.oliviercailloux.jlp.parameters;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * <p>
 * A configuration is a set of parameter values associated to their respective
 * parameters. It can be used to tweak the way an MP will be solved. To obtain
 * such a configuration, use {@link Configuration#configurator()} (or
 * {@link Configuration#defaultConfiguration()} to obtain a default
 * configuration).
 * </p>
 * <p>
 * If several constraints are put to the solver via this object, all will be
 * enforced. For example, setting both max cpu time and max wall time instructs
 * the solver to stop computation as soon as either the cpu time is exhausted,
 * or the wall time has elapsed.
 * </p>
 * <p>
 * Immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Configuration implements IConfiguration {
	/**
	 * The maximum value that can be stored in a Duration. Greater than the age of
	 * the universe.
	 */
	public static final Duration ENOUGH = Duration.ofNanos(Long.MAX_VALUE);

	/**
	 * <code>false</code>.
	 */
	public static final boolean DEFAULT_FORCE_DETERMINISTIC = false;

	/**
	 * {@link #ENOUGH}.
	 */
	public static final Duration DEFAULT_MAX_WALL_TIME = ENOUGH;

	/**
	 * {@link #ENOUGH}.
	 */
	public static final Duration DEFAULT_MAX_CPU_TIME = ENOUGH;

	/**
	 * Retrieves the default configuration: the one that has all values set to the
	 * corresponding default for the parameter.
	 *
	 * @return the default configuration.
	 */
	public static Configuration defaultConfiguration() {
		return new Configurator().build();
	}

	/**
	 * Returns a configurator that can be used to build {@link Configuration}
	 * objects.
	 *
	 * @return a configurator initialized with default values.
	 */
	public static Configurator configurator() {
		return new Configurator();
	}

	private final Configurator delegate;

	Configuration(Configurator delegate) {
		this.delegate = requireNonNull(delegate);
	}

	@Override
	public boolean getForceDeterministic() {
		return delegate.getForceDeterministic();
	}

	@Override
	public Duration getMaxWallTime() {
		return delegate.getMaxWallTime();
	}

	@Override
	public Duration getMaxCpuTime() {
		return delegate.getMaxCpuTime();
	}

	/**
	 * Two {@link Configuration} objects are equal when they have equal values for
	 * all their parameters.
	 *
	 * @param obj the reference object with which to compare.
	 * @return <code>true</code> iff this object is the same as the obj argument.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Configuration)) {
			return false;
		}
		final Configuration c2 = (Configuration) obj;
		return this.delegate.equals(c2.delegate);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		delegate.debugTo(helper);
		return helper.toString();
	}
}
