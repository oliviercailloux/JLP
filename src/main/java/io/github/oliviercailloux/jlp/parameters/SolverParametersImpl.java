package io.github.oliviercailloux.jlp.parameters;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * A simple implementation of {@link SolverParameters} with {@link HashMap}
 * objects.
 *
 * @author Olivier Cailloux
 *
 */
public class SolverParametersImpl implements SolverParameters {

	static private class Equiv extends Equivalence<SolverParameters> {
		public Equiv() {
			/** Public constructor. */
		}

		@Override
		public boolean doEquivalent(SolverParameters a, SolverParameters b) {
			if (!a.getDoubleParameters().equals(b.getDoubleParameters())) {
				return false;
			}
			if (!a.getIntParameters().equals(b.getIntParameters())) {
				return false;
			}
			if (!a.getStringParameters().equals(b.getStringParameters())) {
				return false;
			}
			return true;
		}

		@Override
		public int doHash(SolverParameters t) {
			return Objects.hashCode(t.getDoubleParameters(), t.getIntParameters(), t.getStringParameters());
		}
	}

	static Equivalence<SolverParameters> getEquivalenceRelation() {
		return new Equiv();
	}

	/**
	 * Does not contain default values. No <code>null</code> key.
	 */
	private final Map<SolverParameterDouble, Double> doubleParameters = Maps.newHashMap();

	/**
	 * Does not contain default values. No <code>null</code> key.
	 */
	private final Map<SolverParameterInt, Integer> intParameters = Maps.newHashMap();

	/**
	 * Does not contain default values. No <code>null</code> key.
	 */
	private final Map<SolverParameterString, String> stringParameters = Maps.newHashMap();

	public SolverParametersImpl() {
		/** No parameters constructor. */
	}

	/**
	 * Creates a new object that contains all the values that have been set in the
	 * source object. The copy is by value.
	 *
	 * @param source
	 *            not <code>null</code>.
	 */
	public SolverParametersImpl(SolverParameters source) {
		intParameters.putAll(source.getIntParameters());
		stringParameters.putAll(source.getStringParameters());
		doubleParameters.putAll(source.getDoubleParameters());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SolverParameters)) {
			return false;
		}
		SolverParameters p2 = (SolverParameters) obj;
		return getEquivalenceRelation().equivalent(this, p2);
	}

	@Override
	public Map<SolverParameterDouble, Double> getDoubleParameters() {
		return Maps.newHashMap(doubleParameters);
	}

	@Override
	public Map<SolverParameterInt, Integer> getIntParameters() {
		return Maps.newHashMap(intParameters);
	}

	@Override
	public Map<SolverParameterString, String> getStringParameters() {
		return Maps.newHashMap(stringParameters);
	}

	@Override
	public Double getValue(SolverParameterDouble parameter) {
		Preconditions.checkNotNull(parameter);
		return doubleParameters.containsKey(parameter) ? doubleParameters.get(parameter)
				: SolverParametersDefaultValues.getDefaultDoubleValues().get(parameter);
	}

	@Override
	public Integer getValue(SolverParameterInt parameter) {
		Preconditions.checkNotNull(parameter);
		return intParameters.containsKey(parameter) ? intParameters.get(parameter)
				: SolverParametersDefaultValues.getDefaultIntValues().get(parameter);
	}

	@Override
	public String getValue(SolverParameterString parameter) {
		Preconditions.checkNotNull(parameter);
		return stringParameters.containsKey(parameter) ? stringParameters.get(parameter)
				: SolverParametersDefaultValues.getDefaultStringValues().get(parameter);
	}

	@Override
	public Object getValueAsObject(Enum<?> parameter) {
		Preconditions.checkNotNull(parameter);
		final Object value;
		if (parameter instanceof SolverParameterInt) {
			SolverParameterInt intParameter = (SolverParameterInt) parameter;
			value = getValue(intParameter);
		} else if (parameter instanceof SolverParameterDouble) {
			SolverParameterDouble doubleParameter = (SolverParameterDouble) parameter;
			value = getValue(doubleParameter);
		} else if (parameter instanceof SolverParameterString) {
			SolverParameterString stringParameter = (SolverParameterString) parameter;
			value = getValue(stringParameter);
		} else {
			throw new IllegalArgumentException("Unknown parameter type.");
		}
		return value;
	}

	@Override
	public int hashCode() {
		return getEquivalenceRelation().hash(this);
	}

	@Override
	public boolean setAll(SolverParameters source) {
		return SolverParametersUtils.setAllValues(this, source);
	}

	@Override
	public boolean setValue(SolverParameterDouble parameter, Double value) {
		Predicate<Double> validator = SolverParametersUtils.getValidator(parameter);
		Preconditions.checkArgument(validator.apply(value),
				"The given value: " + value + " is not meaningful for the parameter " + parameter + ".");
		return setValue(doubleParameters, parameter, value);
	}

	@Override
	public boolean setValue(SolverParameterInt parameter, Integer value) {
		Predicate<Integer> validator = SolverParametersUtils.getValidator(parameter);
		Preconditions.checkArgument(validator.apply(value),
				"The given value: " + value + " is not meaningful for the parameter " + parameter + ".");
		return setValue(intParameters, parameter, value);
	}

	@Override
	public boolean setValue(SolverParameterString parameter, String value) {
		Predicate<String> validator = SolverParametersUtils.getValidator(parameter);
		Preconditions.checkArgument(validator.apply(value),
				"The given value: " + value + " is not meaningful for the parameter " + parameter + ".");
		return setValue(stringParameters, parameter, value);
	}

	@Override
	public boolean setValueAsObject(Enum<?> parameter, Object value) {
		Preconditions.checkNotNull(parameter);
		final boolean changed;
		if (parameter instanceof SolverParameterInt) {
			SolverParameterInt intParameter = (SolverParameterInt) parameter;
			Preconditions.checkArgument(value == null || (value instanceof Integer),
					"Incorrect value type: " + value + ".");
			changed = setValue(intParameter, (Integer) value);
		} else if (parameter instanceof SolverParameterDouble) {
			SolverParameterDouble doubleParameter = (SolverParameterDouble) parameter;
			Preconditions.checkArgument(value == null || (value instanceof Double),
					"Incorrect value type: " + value + ".");
			changed = setValue(doubleParameter, (Double) value);
		} else if (parameter instanceof SolverParameterString) {
			SolverParameterString stringParameter = (SolverParameterString) parameter;
			Preconditions.checkArgument(value == null || (value instanceof String),
					"Incorrect value type: " + value + ".");
			changed = setValue(stringParameter, (String) value);
		} else {
			throw new IllegalArgumentException("Unknown parameter type.");
		}
		return changed;
	}

	@Override
	public String toString() {
		return SolverParametersUtils.toString(this);
	}

	private boolean isDefaultValue(Object parameter, Object value) {
		final Object defaultValue = SolverParametersDefaultValues.getDefaultValueObject(parameter);
		return Objects.equal(value, defaultValue);
	}

	private <IlpParameter, V> boolean setValue(Map<IlpParameter, V> parametersMap, IlpParameter parameter, V value) {
		Preconditions.checkNotNull(parameter);
		final boolean isDefault = isDefaultValue(parameter, value);
		final boolean changed;
		if (isDefault) {
			changed = parametersMap.containsKey(parameter);
			parametersMap.remove(parameter);
			/**
			 * Is equivalent only if contained implies (previous != null), thus, if no null
			 * values may be stored, which might not be guaranteed.
			 */
			// final V previous = parametersMap.remove(parameter);
			// changed = previous != null;
		} else {
			final V previous = parametersMap.put(parameter, value);
			changed = !Objects.equal(previous, value);
		}
		return changed;
	}

}
