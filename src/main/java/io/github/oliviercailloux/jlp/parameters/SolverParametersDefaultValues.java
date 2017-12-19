package io.github.oliviercailloux.jlp.parameters;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

public class SolverParametersDefaultValues {
	private static Map<SolverParameterDouble, Double> s_doubles = null;

	private static Map<SolverParameterInt, Integer> s_ints = null;

	private static Map<SolverParameterString, String> s_strings = null;

	/**
	 * Retrieves the default double values.
	 *
	 * @return a (possibly read-only) copy of the default values as a map.
	 */
	static public Map<SolverParameterDouble, Double> getDefaultDoubleValues() {
		lazyInit();
		return s_doubles;
	}

	/**
	 * Retrieves the default integer values.
	 *
	 * @return a (possibly read-only) copy of the default values as a map.
	 */
	static public Map<SolverParameterInt, Integer> getDefaultIntValues() {
		lazyInit();
		return s_ints;
	}

	/**
	 * Retrieves the default string values.
	 *
	 * @return a (possibly read-only) copy of the default values as a map.
	 */
	static public Map<SolverParameterString, String> getDefaultStringValues() {
		lazyInit();
		return s_strings;
	}

	/**
	 * Retrieves the default value associated to the given parameter.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @return the default value, possibly <code>null</code> as this is a meaningful
	 *         value for some parameters.
	 */
	static public Double getDefaultValue(SolverParameterDouble parameter) {
		lazyInit();
		return s_doubles.get(parameter);
	}

	/**
	 * Retrieves the default value associated to the given parameter.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @return the default value, possibly <code>null</code> as this is a meaningful
	 *         value for some parameters.
	 */
	static public Integer getDefaultValue(SolverParameterInt parameter) {
		lazyInit();
		return s_ints.get(parameter);
	}

	/**
	 * Retrieves the default value associated to the given parameter.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @return the default value, possibly <code>null</code> as this is a meaningful
	 *         value for some parameters.
	 */
	static public String getDefaultValue(SolverParameterString parameter) {
		lazyInit();
		return s_strings.get(parameter);
	}

	/**
	 * Retrieves the default value associated to the given parameter. This method
	 * allows for more flexible use as the caller does not have to distinguish the
	 * type of the parameter. The type of the parameter argument must be
	 * {@link SolverParameterInt}, {@link SolverParameterDouble},
	 * {@link SolverParameterString} or {@link SolverParameterObject}, otherwise an
	 * exception is thrown.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @return the default value, possibly <code>null</code> as this is a meaningful
	 *         value for some parameters.
	 */
	static public Object getDefaultValueObject(Object parameter) {
		lazyInit();
		final Object value;
		if (parameter instanceof SolverParameterInt) {
			final SolverParameterInt paramTyped = (SolverParameterInt) parameter;
			value = s_ints.get(paramTyped);
		} else if (parameter instanceof SolverParameterDouble) {
			final SolverParameterDouble paramTyped = (SolverParameterDouble) parameter;
			value = s_doubles.get(paramTyped);
		} else if (parameter instanceof SolverParameterString) {
			final SolverParameterString paramTyped = (SolverParameterString) parameter;
			value = s_strings.get(paramTyped);
		} else {
			throw new IllegalArgumentException("Unknown parameter type.");
		}
		return value;
	}

	private static void lazyInit() {
		if (s_doubles == null) {
			/** Unfotunately guava's ImmutableMap does not accept null values. */
			// final Builder<IlpDoubleParameter, Double> doublesBuilder =
			// ImmutableMap.builder();
			// doublesBuilder.put(IlpDoubleParameter.MAX_CPU_SECONDS, null);
			// doublesBuilder.put(IlpDoubleParameter.MAX_TREE_SIZE_MB, null);
			// doublesBuilder.put(IlpDoubleParameter.MAX_WALL_SECONDS, null);
			// s_doubles = doublesBuilder.build();
			//
			// final Builder<IlpIntParameter, Integer> intsBuilder = ImmutableMap.builder();
			// intsBuilder.put(IlpIntParameter.MAX_THREADS_NULLABLE, null);
			// s_ints = intsBuilder.build();
			//
			// final Builder<IlpStringParameter, String> stringsBuilder =
			// ImmutableMap.builder();
			// stringsBuilder.put(IlpStringParameter.WORK_DIR_NULLABLE, null);
			// s_strings = stringsBuilder.build();

			s_doubles = Maps.newHashMap();
			s_doubles.put(SolverParameterDouble.MAX_CPU_SECONDS, null);
			s_doubles.put(SolverParameterDouble.MAX_TREE_SIZE_MB, null);
			s_doubles.put(SolverParameterDouble.MAX_MEMORY_MB, null);
			s_doubles.put(SolverParameterDouble.MAX_WALL_SECONDS, null);
			s_doubles = Collections.unmodifiableMap(s_doubles);

			s_ints = Maps.newHashMap();
			s_ints.put(SolverParameterInt.MAX_THREADS, null);
			s_ints.put(SolverParameterInt.DETERMINISTIC, Integer.valueOf(0));
			s_ints = Collections.unmodifiableMap(s_ints);

			s_strings = Maps.newHashMap();
			s_strings.put(SolverParameterString.WORK_DIR, null);
			s_strings = Collections.unmodifiableMap(s_strings);

			assert (s_doubles.size() == SolverParameterDouble.values().length);
			assert (s_ints.size() == SolverParameterInt.values().length);
			assert (s_strings.size() == SolverParameterString.values().length);
		}
	}

	private SolverParametersDefaultValues() {
		/** Non-instantiable. */
	}
}
