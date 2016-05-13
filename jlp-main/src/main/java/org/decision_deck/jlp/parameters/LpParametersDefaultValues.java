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
package org.decision_deck.jlp.parameters;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

public class LpParametersDefaultValues {
    private static Map<LpDoubleParameter, Double> s_doubles = null;

    private static Map<LpIntParameter, Integer> s_ints = null;

    private static Map<LpObjectParameter, Object> s_objects = null;

    /**
     * Retrieves the default double values.
     * 
     * @return a (possibly read-only) copy of the default values as a map.
     */
    static public Map<LpDoubleParameter, Double> getDefaultDoubleValues() {
	lazyInit();
	return s_doubles;
    }

    /**
     * Retrieves the default integer values.
     * 
     * @return a (possibly read-only) copy of the default values as a map.
     */
    static public Map<LpIntParameter, Integer> getDefaultIntValues() {
	lazyInit();
	return s_ints;
    }

    /**
     * Retrieves the default object values.
     * 
     * @return a (possibly read-only) copy of the default values as a map.
     */
    static public Map<LpObjectParameter, Object> getDefaultObjectValues() {
	lazyInit();
	return s_objects;
    }

    /**
     * Retrieves the default value associated to the given parameter.
     * 
     * @param parameter
     *            not <code>null</code>.
     * @return the default value, possibly <code>null</code> as this is a meaningful value for some parameters.
     */
    static public Double getDefaultValue(LpDoubleParameter parameter) {
	lazyInit();
	return s_doubles.get(parameter);
    }

    /**
     * Retrieves the default value associated to the given parameter.
     * 
     * @param parameter
     *            not <code>null</code>.
     * @return the default value, possibly <code>null</code> as this is a meaningful value for some parameters.
     */
    static public Integer getDefaultValue(LpIntParameter parameter) {
	lazyInit();
	return s_ints.get(parameter);
    }

    /**
     * Retrieves the default value associated to the given parameter.
     * 
     * @param parameter
     *            not <code>null</code>.
     * @return the default value, possibly <code>null</code> as this is a meaningful value for some parameters.
     */
    static public Object getDefaultValue(LpObjectParameter parameter) {
	lazyInit();
	return s_objects.get(parameter);
    }

    /**
     * Retrieves the default value associated to the given parameter. This method allows for more flexible use as the
     * caller does not have to distinguish the type of the parameter. The type of the parameter argument must be
     * {@link LpIntParameter}, {@link LpDoubleParameter}, {@link LpStringParameter} or {@link LpObjectParameter},
     * otherwise an exception is thrown.
     * 
     * @param parameter
     *            not <code>null</code>.
     * @return the default value, possibly <code>null</code> as this is a meaningful value for some parameters.
     */
    static public Object getDefaultValueObject(Object parameter) {
	lazyInit();
	final Object value;
	if (parameter instanceof LpIntParameter) {
	    value = s_ints.get(parameter);
	} else if (parameter instanceof LpDoubleParameter) {
	    value = s_doubles.get(parameter);
	} else if (parameter instanceof LpStringParameter) {
	    value = s_strings.get(parameter);
	} else if (parameter instanceof LpObjectParameter) {
	    value = s_objects.get(parameter);
	} else {
	    throw new IllegalArgumentException("Unknown parameter type.");
	}
	return value;
    }

    private static void lazyInit() {
	if (s_doubles == null) {
	    /** Unfotunately guava's ImmutableMap does not accept null values. */
	    // final Builder<IlpDoubleParameter, Double> doublesBuilder = ImmutableMap.builder();
	    // doublesBuilder.put(IlpDoubleParameter.MAX_CPU_SECONDS, null);
	    // doublesBuilder.put(IlpDoubleParameter.MAX_TREE_SIZE_MB, null);
	    // doublesBuilder.put(IlpDoubleParameter.MAX_WALL_SECONDS, null);
	    // s_doubles = doublesBuilder.build();
	    //
	    // final Builder<IlpIntParameter, Integer> intsBuilder = ImmutableMap.builder();
	    // intsBuilder.put(IlpIntParameter.MAX_THREADS_NULLABLE, null);
	    // s_ints = intsBuilder.build();
	    //
	    // final Builder<IlpStringParameter, String> stringsBuilder = ImmutableMap.builder();
	    // stringsBuilder.put(IlpStringParameter.WORK_DIR_NULLABLE, null);
	    // s_strings = stringsBuilder.build();

	    s_doubles = Maps.newHashMap();
	    s_doubles.put(LpDoubleParameter.MAX_CPU_SECONDS, null);
	    s_doubles.put(LpDoubleParameter.MAX_TREE_SIZE_MB, null);
	    s_doubles.put(LpDoubleParameter.MAX_MEMORY_MB, null);
	    s_doubles.put(LpDoubleParameter.MAX_WALL_SECONDS, null);
	    s_doubles = Collections.unmodifiableMap(s_doubles);

	    s_ints = Maps.newHashMap();
	    s_ints.put(LpIntParameter.MAX_THREADS, null);
	    s_ints.put(LpIntParameter.DETERMINISTIC, Integer.valueOf(0));
	    s_ints = Collections.unmodifiableMap(s_ints);

	    s_strings = Maps.newHashMap();
	    s_strings.put(LpStringParameter.WORK_DIR, null);
	    s_strings = Collections.unmodifiableMap(s_strings);

	    s_objects = Maps.newHashMap();
	    s_objects.put(LpObjectParameter.NAMER_VARIABLES, null);
	    s_objects.put(LpObjectParameter.NAMER_VARIABLES_BY_FORMAT, null);
	    s_objects.put(LpObjectParameter.NAMER_CONSTRAINTS, null);
	    s_objects.put(LpObjectParameter.NAMER_CONSTRAINTS_BY_FORMAT, null);
	    s_objects = Collections.unmodifiableMap(s_objects);

	    assert (s_doubles.size() == LpDoubleParameter.values().length);
	    assert (s_ints.size() == LpIntParameter.values().length);
	    assert (s_strings.size() == LpStringParameter.values().length);
	    assert (s_objects.size() == LpObjectParameter.values().length);
	}
    }

    private static Map<LpStringParameter, String> s_strings = null;

    private LpParametersDefaultValues() {
	/** Non-instantiable. */
    }

    /**
     * Retrieves the default value associated to the given parameter.
     * 
     * @param parameter
     *            not <code>null</code>.
     * @return the default value, possibly <code>null</code> as this is a meaningful value for some parameters.
     */
    static public String getDefaultValue(LpStringParameter parameter) {
	lazyInit();
	return s_strings.get(parameter);
    }

    /**
     * Retrieves the default string values.
     * 
     * @return a (possibly read-only) copy of the default values as a map.
     */
    static public Map<LpStringParameter, String> getDefaultStringValues() {
	lazyInit();
	return s_strings;
    }
}
