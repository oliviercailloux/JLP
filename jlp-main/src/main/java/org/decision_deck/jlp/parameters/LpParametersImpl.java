package org.decision_deck.jlp.parameters;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * A simple implementation of {@link LpParameters} with {@link HashMap} objects.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpParametersImpl implements LpParameters {

	static private class Equiv extends Equivalence<LpParameters> {
		public Equiv() {
			/** Public constructor. */
		}

		@Override
		public boolean doEquivalent(LpParameters a, LpParameters b) {
			if (!a.getDoubleParameters().equals(b.getDoubleParameters())) {
				return false;
			}
			if (!a.getIntParameters().equals(b.getIntParameters())) {
				return false;
			}
			if (!a.getStringParameters().equals(b.getStringParameters())) {
				return false;
			}
			if (!a.getObjectParameters().equals(b.getObjectParameters())) {
				return false;
			}
			return true;
		}

		@Override
		public int doHash(LpParameters t) {
			return Objects.hashCode(t.getDoubleParameters(), t.getIntParameters(), t.getStringParameters(),
					t.getObjectParameters());
		}
	}

	/**
	 * Does not contain default values. No <code>null</code> key.
	 */
	private final Map<LpDoubleParameter, Double> m_doubleParameters = Maps.newHashMap();

	/**
	 * Does not contain default values. No <code>null</code> key.
	 */
	private final Map<LpObjectParameter, Object> m_objectParameters = Maps.newHashMap();

	/**
	 * Does not contain default values. No <code>null</code> key.
	 */
	private final Map<LpIntParameter, Integer> m_intParameters = Maps.newHashMap();

	/**
	 * Does not contain default values. No <code>null</code> key.
	 */
	private final Map<LpStringParameter, String> m_stringParameters = Maps.newHashMap();

	public LpParametersImpl() {
		/** No parameters constructor. */
	}

	/**
	 * Creates a new object that contains all the values that have been set in the
	 * source object. The copy is by value.
	 * 
	 * @param source
	 *            not <code>null</code>.
	 */
	public LpParametersImpl(LpParameters source) {
		m_intParameters.putAll(source.getIntParameters());
		m_stringParameters.putAll(source.getStringParameters());
		m_doubleParameters.putAll(source.getDoubleParameters());
		m_objectParameters.putAll(source.getObjectParameters());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LpParameters)) {
			return false;
		}
		LpParameters p2 = (LpParameters) obj;
		return getEquivalenceRelation().equivalent(this, p2);
	}

	@Override
	public Map<LpDoubleParameter, Double> getDoubleParameters() {
		return Maps.newHashMap(m_doubleParameters);
	}

	@Override
	public Map<LpIntParameter, Integer> getIntParameters() {
		return Maps.newHashMap(m_intParameters);
	}

	@Override
	public Map<LpObjectParameter, Object> getObjectParameters() {
		return Maps.newHashMap(m_objectParameters);
	}

	@Override
	public Double getValue(LpDoubleParameter parameter) {
		Preconditions.checkNotNull(parameter);
		return m_doubleParameters.containsKey(parameter) ? m_doubleParameters.get(parameter)
				: LpParametersDefaultValues.getDefaultDoubleValues().get(parameter);
	}

	@Override
	public Integer getValue(LpIntParameter parameter) {
		Preconditions.checkNotNull(parameter);
		return m_intParameters.containsKey(parameter) ? m_intParameters.get(parameter)
				: LpParametersDefaultValues.getDefaultIntValues().get(parameter);
	}

	@Override
	public Object getValue(LpObjectParameter parameter) {
		Preconditions.checkNotNull(parameter);
		if (m_objectParameters.containsKey(parameter)) {
			return m_objectParameters.get(parameter);
		}
		final Map<LpObjectParameter, Object> defaults = LpParametersDefaultValues.getDefaultObjectValues();
		assert (defaults.containsKey(parameter));
		return defaults.get(parameter);
	}

	@Override
	public Object getValueAsObject(Enum<?> parameter) {
		Preconditions.checkNotNull(parameter);
		final Object value;
		if (parameter instanceof LpIntParameter) {
			LpIntParameter intParameter = (LpIntParameter) parameter;
			value = getValue(intParameter);
		} else if (parameter instanceof LpDoubleParameter) {
			LpDoubleParameter doubleParameter = (LpDoubleParameter) parameter;
			value = getValue(doubleParameter);
		} else if (parameter instanceof LpStringParameter) {
			LpStringParameter stringParameter = (LpStringParameter) parameter;
			value = getValue(stringParameter);
		} else if (parameter instanceof LpObjectParameter) {
			LpObjectParameter objectParameter = (LpObjectParameter) parameter;
			value = getValue(objectParameter);
		} else {
			throw new IllegalArgumentException("Unknown parameter type.");
		}
		return value;
	}

	@Override
	public int hashCode() {
		return getEquivalenceRelation().hash(this);
	}

	private boolean isDefaultValue(Object parameter, Object value) {
		final Object defaultValue = LpParametersDefaultValues.getDefaultValueObject(parameter);
		return Objects.equal(value, defaultValue);
	}

	@Override
	public boolean setValue(LpDoubleParameter parameter, Double value) {
		Predicate<Double> validator = LpParametersUtils.getValidator(parameter);
		Preconditions.checkArgument(validator.apply(value),
				"The given value: " + value + " is not meaningful for the parameter " + parameter + ".");
		return setValue(m_doubleParameters, parameter, value);
	}

	@Override
	public boolean setValue(LpIntParameter parameter, Integer value) {
		Predicate<Integer> validator = LpParametersUtils.getValidator(parameter);
		Preconditions.checkArgument(validator.apply(value),
				"The given value: " + value + " is not meaningful for the parameter " + parameter + ".");
		return setValue(m_intParameters, parameter, value);
	}

	@Override
	public boolean setValue(LpObjectParameter parameter, Object value) {
		Predicate<Object> validator = LpParametersUtils.getValidator(parameter);
		Preconditions.checkArgument(validator.apply(value),
				"The given value: " + value + " is not meaningful for the parameter " + parameter + ".");
		return setValue(m_objectParameters, parameter, value);
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

	@Override
	public boolean setValueAsObject(Enum<?> parameter, Object value) {
		Preconditions.checkNotNull(parameter);
		final boolean changed;
		if (parameter instanceof LpIntParameter) {
			LpIntParameter intParameter = (LpIntParameter) parameter;
			Preconditions.checkArgument(value == null || (value instanceof Integer),
					"Incorrect value type: " + value + ".");
			changed = setValue(intParameter, (Integer) value);
		} else if (parameter instanceof LpDoubleParameter) {
			LpDoubleParameter doubleParameter = (LpDoubleParameter) parameter;
			Preconditions.checkArgument(value == null || (value instanceof Double),
					"Incorrect value type: " + value + ".");
			changed = setValue(doubleParameter, (Double) value);
		} else if (parameter instanceof LpStringParameter) {
			LpStringParameter stringParameter = (LpStringParameter) parameter;
			Preconditions.checkArgument(value == null || (value instanceof String),
					"Incorrect value type: " + value + ".");
			changed = setValue(stringParameter, (String) value);
		} else if (parameter instanceof LpObjectParameter) {
			LpObjectParameter objectParameter = (LpObjectParameter) parameter;
			changed = setValue(objectParameter, value);
		} else {
			throw new IllegalArgumentException("Unknown parameter type.");
		}
		return changed;
	}

	@Override
	public String toString() {
		return LpParametersUtils.toString(this);
	}

	@Override
	public Map<LpStringParameter, String> getStringParameters() {
		return Maps.newHashMap(m_stringParameters);
	}

	@Override
	public String getValue(LpStringParameter parameter) {
		Preconditions.checkNotNull(parameter);
		return m_stringParameters.containsKey(parameter) ? m_stringParameters.get(parameter)
				: LpParametersDefaultValues.getDefaultStringValues().get(parameter);
	}

	@Override
	public boolean setValue(LpStringParameter parameter, String value) {
		Predicate<String> validator = LpParametersUtils.getValidator(parameter);
		Preconditions.checkArgument(validator.apply(value),
				"The given value: " + value + " is not meaningful for the parameter " + parameter + ".");
		return setValue(m_stringParameters, parameter, value);
	}

	static Equivalence<LpParameters> getEquivalenceRelation() {
		return new Equiv();
	}

	@Override
	public boolean setAll(LpParameters source) {
		return LpParametersUtils.setAllValues(this, source);
	}

}
