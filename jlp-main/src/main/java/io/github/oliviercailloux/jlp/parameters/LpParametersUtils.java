package io.github.oliviercailloux.jlp.parameters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import io.github.oliviercailloux.jlp.LpSolverException;
import io.github.oliviercailloux.jlp.utils.TimingHelper;

public class LpParametersUtils {

	/**
	 * Retrieves all the parameters, including those that have a default value, as a
	 * list of properties, using a reasonable format for export (with English locale
	 * for numbers). Values that are <code>null</code> are transformed to the string
	 * "null".
	 *
	 * @param parameters
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public Properties getAsProperties(LpParameters parameters) {
		final Properties properties = new Properties();

		for (LpStringParameter parameter : LpParametersDefaultValues.getDefaultStringValues().keySet()) {
			final String value = parameters.getValue(parameter);
			properties.setProperty(parameter.toString(), value == null ? "null" : value);
		}

		final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.ENGLISH);

		formatter.setMinimumFractionDigits(3);
		formatter.setMaximumFractionDigits(3);
		for (LpDoubleParameter parameter : LpParametersDefaultValues.getDefaultDoubleValues().keySet()) {
			final Double value = parameters.getValue(parameter);
			properties.setProperty(parameter.toString(), value == null ? "null" : formatter.format(value));
		}

		formatter.setMinimumFractionDigits(0);
		formatter.setMaximumFractionDigits(0);
		for (LpIntParameter parameter : LpParametersDefaultValues.getDefaultIntValues().keySet()) {
			final Integer value = parameters.getValue(parameter);
			properties.setProperty(parameter.toString(), value == null ? "null" : formatter.format(value));
		}

		for (LpObjectParameter parameter : LpParametersDefaultValues.getDefaultObjectValues().keySet()) {
			final Object value = parameters.getValue(parameter);
			properties.setProperty(parameter.toString(), value == null ? "null" : value.toString());
		}

		return properties;
	}

	public static Set<Enum<?>> getParameters() {
		final Set<Enum<?>> all = Sets.newLinkedHashSet();
		all.addAll(LpParametersDefaultValues.getDefaultDoubleValues().keySet());
		all.addAll(LpParametersDefaultValues.getDefaultIntValues().keySet());
		all.addAll(LpParametersDefaultValues.getDefaultStringValues().keySet());
		all.addAll(LpParametersDefaultValues.getDefaultObjectValues().keySet());
		return all;
		// final SetView<Enum<?>> doublesAndStrings =
		// Sets.union(LpParametersDefaultValues.getDefaultDoubleValues()
		// .keySet(),
		// LpParametersDefaultValues.getDefaultStringValues().keySet());
		//
		// return
		// Sets.union(LpParametersDefaultValues.getDefaultIntValues().keySet(),
		// doublesAndStrings);
	}

	/**
	 * Retrieves the preferred timing type according to the given parameters values
	 * and the Java virtual machine capabilities.
	 * </p>
	 * <p>
	 * This method considers the {@link LpDoubleParameter#MAX_WALL_SECONDS} and
	 * {@link LpDoubleParameter#MAX_CPU_SECONDS} parameter values: each of these
	 * parameters may be either set, meaning that they are associated with a
	 * non-<code>null</code> value, or not set (the contrary case). The method also
	 * considers whether CPU timing is supported by the Java virtual machine for the
	 * current thread.
	 * </p>
	 *
	 * @param parameters
	 *            not <code>null</code>.
	 *
	 * @return not <code>null</code>.
	 *         <p>
	 *         The return value is either {@link LpTimingType#WALL_TIMING},
	 *         {@link LpTimingType#CPU_TIMING}, or an exception. It is computed as
	 *         follow.
	 *         <table>
	 *         <thead>
	 *         <tr>
	 *         <th>MAX_CPU_SECONDS</th>
	 *         <th>set, supported</th>
	 *         <th>set, unsupported</th>
	 *         <th>not set, supported</th>
	 *         <th>not set, unsupported</th>
	 *         </tr>
	 *         <tr>
	 *         <th>MAX_WALL_SECONDS</th>
	 *         </tr>
	 *         </thead> <tbody>
	 *         <tr>
	 *         <th>set</th>
	 *         <td>exc</td>
	 *         <td>exc</td>
	 *         <td>WALL_TIMING</td>
	 *         <td>WALL_TIMING</td>
	 *         </tr>
	 *         <tr>
	 *         <th>not set</th>
	 *         <td>CPU_TIMING</td>
	 *         <td>exc</td>
	 *         <td>CPU_TIMING</td>
	 *         <td>WALL_TIMING</td>
	 *         </tr>
	 *         </tbody>
	 *         </table>
	 *         <p>
	 *         Explanation of the rules are the following. If both the max wall time
	 *         and max cpu time parameters are set, an exception is thrown. If the
	 *         max cpu time parameter is set but cpu timing is not supported by the
	 *         Java virtual machine, an exception is thrown. Otherwise, this method
	 *         returns the timing type for which a time limit has been set as a
	 *         parameter, or if none has been set, returns cpu timing if it is
	 *         supported and wall timing otherwise.
	 *         </p>
	 * @throws LpSolverException
	 *             if both cpu and wall time limit parameters have a value; or if
	 *             cpu time limit parameter is set but cpu timing is not supported
	 *             by the Java virtual machine.
	 */
	static public LpTimingType getPreferredTimingType(LpParameters parameters) throws LpSolverException {
		final boolean hasMaxWall = parameters.getValue(LpDoubleParameter.MAX_WALL_SECONDS) != null;
		final boolean hasMaxCpu = parameters.getValue(LpDoubleParameter.MAX_CPU_SECONDS) != null;
		if (hasMaxCpu && hasMaxWall) {
			throw new LpSolverException("Can't have both CPU time limit and Wall time limit.");
		}
		final LpTimingType timingType;
		if (hasMaxWall) {
			timingType = LpTimingType.WALL_TIMING;
		} else if (hasMaxCpu) {
			if (!new TimingHelper().isCpuTimingSupported()) {
				throw new LpSolverException("Cpu timing not supported but max cpu time is set.");
			}
			timingType = LpTimingType.CPU_TIMING;
		} else {
			if (new TimingHelper().isCpuTimingSupported()) {
				timingType = LpTimingType.CPU_TIMING;
			} else {
				timingType = LpTimingType.WALL_TIMING;
			}
		}
		return timingType;
	}

	static public Double getTimeLimit(LpParameters parameters, LpTimingType timingType) {
		switch (timingType) {
		case WALL_TIMING:
			return parameters.getValue(LpDoubleParameter.MAX_WALL_SECONDS);
		case CPU_TIMING:
			return parameters.getValue(LpDoubleParameter.MAX_CPU_SECONDS);
		default:
			throw new IllegalStateException("Unknown timing type.");
		}
	}

	static public Predicate<Double> getValidator(LpDoubleParameter parameter) {
		switch (parameter) {
		case MAX_CPU_SECONDS:
			return new Predicate<Double>() {
				@Override
				public boolean apply(Double value) {
					return value == null || value.doubleValue() > 0d;
				}
			};
		case MAX_MEMORY_MB:
		case MAX_TREE_SIZE_MB:
			return new Predicate<Double>() {
				@Override
				public boolean apply(Double value) {
					return value == null || value.doubleValue() > 0d;
				}
			};
		case MAX_WALL_SECONDS:
			return new Predicate<Double>() {
				@Override
				public boolean apply(Double value) {
					return value == null || value.doubleValue() > 0d;
				}
			};
		default:
			throw new IllegalStateException("Unknown parameter.");
		}
	}

	static public Predicate<Integer> getValidator(LpIntParameter parameter) {
		switch (parameter) {
		case MAX_THREADS:
			return new Predicate<Integer>() {
				@Override
				public boolean apply(Integer value) {
					return value == null || value.intValue() > 0;
				}
			};
		case DETERMINISTIC:
			return new Predicate<Integer>() {
				@Override
				public boolean apply(Integer value) {
					if (value == null) {
						return false;
					}
					final int pValue = value.intValue();
					return pValue == 0 || pValue == 1;
				}
			};
		default:
			throw new IllegalStateException("Unknown parameter.");
		}
	}

	static public Predicate<Object> getValidator(LpObjectParameter parameter) {
		switch (parameter) {
		case NAMER_VARIABLES:
			return new Predicate<Object>() {
				@Override
				public boolean apply(Object value) {
					return value == null || value instanceof Function<?, ?>;
				}
			};
		case NAMER_CONSTRAINTS:
			return new Predicate<Object>() {
				@Override
				public boolean apply(Object value) {
					return value == null || value instanceof Function<?, ?>;
				}
			};
		case NAMER_VARIABLES_BY_FORMAT:
			return new Predicate<Object>() {
				@Override
				public boolean apply(Object value) {
					return value == null || value instanceof Map<?, ?>;
				}
			};
		case NAMER_CONSTRAINTS_BY_FORMAT:
			return new Predicate<Object>() {
				@Override
				public boolean apply(Object value) {
					return value == null || value instanceof Map<?, ?>;
				}
			};
		default:
			throw new IllegalStateException("Unknown parameter.");
		}
	}

	static public Predicate<String> getValidator(LpStringParameter parameter) {
		switch (parameter) {
		case WORK_DIR:
			return new Predicate<String>() {
				@Override
				public boolean apply(String value) {
					return value == null || !value.isEmpty();
				}
			};
		default:
			throw new IllegalStateException("Unknown parameter.");
		}
	}

	static public LpParameters newParameters() {
		return new LpParametersImpl();
	}

	static public LpParameters newParameters(LpParameters source) {
		return new LpParametersImpl(source);
	}

	public static boolean removeAllValues(LpParameters parameters) {
		boolean modified = false;
		for (Enum<?> parameter : getParameters()) {
			final boolean changed = parameters.setValueAsObject(parameter,
					LpParametersDefaultValues.getDefaultValueObject(parameter));
			modified = modified || changed;
		}
		return modified;
	}

	/**
	 * Overrides all values in the target object with values in the source one,
	 * including those that have default values in the source object.
	 *
	 * @param target
	 *            not <code>null</code>.
	 * @param source
	 *            not <code>null</code>.
	 *
	 * @return <code>true</code> iff the state of the target object changed as a
	 *         result of this call. Equivalently, <code>false</code> iff the given
	 *         source equals the given target.
	 */
	public static boolean setAllValues(LpParameters target, LpParameters source) {
		checkNotNull(target);
		checkNotNull(source);
		boolean modified = false;
		for (Enum<?> parameter : getParameters()) {
			final boolean changed = target.setValueAsObject(parameter, source.getValueAsObject(parameter));
			modified = modified || changed;
		}
		return modified;
	}

	public static String toString(LpParameters parameters) {
		final ToStringHelper helper = Objects.toStringHelper(parameters);
		final MapJoiner mapFormatter = Joiner.on(", ").useForNull("null").withKeyValueSeparator("=");
		final String toStrInts = mapFormatter.join(parameters.getIntParameters());
		final String toStrDoubles = mapFormatter.join(parameters.getDoubleParameters());
		final String toStrStrings = mapFormatter.join(parameters.getStringParameters());
		final String toStrObjects = mapFormatter.join(parameters.getObjectParameters());

		final Joiner joiner = Joiner.on(", ");
		final Predicate<CharSequence> isNonEmpty = Predicates.contains(Pattern.compile(".+"));
		final Iterable<String> nonEmptyMaps = Iterables.filter(
				Arrays.asList(new String[] { toStrInts, toStrDoubles, toStrStrings, toStrObjects }), isNonEmpty);
		final String res = joiner.join(nonEmptyMaps);
		helper.addValue(res);
		return helper.toString();
		// final StringBuilder builder = new StringBuilder(helper.toString());
		// joiner.appendTo(builder, Strings.emptyToNull(toStrInts),
		// Strings.emptyToNull(toStrDoubles),
		// Strings.emptyToNull(toStrStrings));
		// mapFormatter.appendTo(builder, getIntParameters());
		// mapFormatter.appendTo(builder, getDoubleParameters());
		// mapFormatter.appendTo(builder, getStringParameters());
		// return res;
	}
}
