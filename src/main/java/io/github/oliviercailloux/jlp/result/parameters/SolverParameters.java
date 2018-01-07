package io.github.oliviercailloux.jlp.result.parameters;

import java.util.Map;

/**
 * <p>
 * Holds values of the parameters that may be used to configure a solver. The
 * set of values a parameter can take and their meaning is documented in the
 * appropriate enum types, i.e. {@link SolverParameterDouble},
 * {@link SolverParameterInt}, {@link SolverParameterString}. The default values
 * for all parameters may be queried from the
 * {@link SolverParametersDefaultValues} class.
 * </p>
 * <p>
 * A value associated to a parameter may be either not set, which means the
 * value will be the default value for that parameter, or set to a non-default
 * value. Setting a parameter to its default value has the same effect as not
 * setting the parameter at all.
 * </p>
 * <p>
 * As of vocabulary, a value for a given parameter is <em>unmeaningful</em> iff
 * it is necessarily inadequate, meaning it is not accepted by any conceivable
 * solver instance. For example, a value of zero for a number of threads. A
 * value for a given parameter is <em>unsupported</em>, considering a given
 * solver, if the solver does not support that parameter value. For example, for
 * a hypothetic solver instance which would not support the deterministic mode,
 * a value of one for the parameter {@link SolverParameterInt#DETERMINISTIC}
 * would be unsupported, although it is a meaningful value. If a value is
 * unmeaningful, then it is necessarily unsupported, meaning that it is
 * unsupported irrespectively of the chosen solver implementation.
 * </p>
 * <p>
 * Some values are supported independently of the solver, thus are
 * <em>necessarily supported</em>, meaning that every solvers compatible with
 * this library support these values, and it is expected that every solver
 * implementation used by this library in the future will also support it. For
 * example, the value zero for the parameter
 * {@link SolverParameterInt#DETERMINISTIC} is necessarily supported. In that
 * specific case this holds because the value zero for that parameter imposes no
 * obligation on the underlying solver, it only tells the solver that it
 * <em>may</em> use a non deterministic mode. Assuming the underlying solver has
 * only deterministic mode, using the “possibly not deterministic mode” for it
 * has no effect, but the value is still supported. The default value for any
 * parameter is necessarily supported.
 * </p>
 * <p>
 * A value is thus <em>meaningful</em> iff it is possibly or necessarily
 * supported. A value is also considered as possibly supported, if it is
 * conceivable that a solver supporting that value would be used together with
 * this library. A value is <em>supported</em> iff it is meaningful and the
 * chosen solver implements it.
 * </p>
 * <p>
 * Some parameters may have <code>null</code> as a possible value, which has a
 * special meaning depending on the parameter, e.g. "be clever" or "the fastest"
 * or "do not use that parameter".
 * </p>
 * <p>
 * When a value for a given parameter is possibly supported, but not
 * necessarily, this is mentioned in the parameter documentation in the relevant
 * enum type − which means that if nothing is mentioned about possible values,
 * everything is to be considered suppported independently of the solver. Trying
 * to set a parameter value to an unmeaningful value results in an
 * {@link IllegalArgumentException} be thrown. However, setting the value of a
 * parameter to a value that is <em>possibly</em> unsupported depending on the
 * solver does not throw an exception when it is set. But the value may reveal
 * unsupported when the solver is used, so be cautious about possibly
 * unsupported values.
 * </p>
 * <p>
 * The class {@link SolverParametersUtils} may be used to determine whether a
 * value is meaningful for a given parameter, see for example
 * {@link SolverParametersUtils#getValidator(SolverParameterDouble)}.
 * </p>
 * <p>
 * A class implementing this interface may be read-only, in which case it should
 * consistently throw {@link UnsupportedOperationException} on the methods
 * attempting to modify its state.
 * </p>
 * <p>
 * Two {@link SolverParameters} objects are equal, as determined by
 * {@link #equals}, iff they contain the same value for each parameter. This
 * also mandates that the values for the parameters having type object be equal.
 * Cautious should thus be exerciced as these values may be special and hence
 * the user may forget to override equals appropriately, e.g. when using a
 * function as a value for the parameter
 * {@link SolverParameterObject#NAMER_VARIABLES}.
 * </p>
 * <p>
 * When two {@link SolverParameters} objects have the default value for a given
 * parameter, these values are guaranteed to be considered equal because all
 * {@link SolverParameters} objects share the same defaults.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public interface SolverParameters {

	/**
	 * Tests whether the given object represents the same parameters as this one.
	 *
	 * @param o
	 *            may be <code>null</code>.
	 * @return <code>true</code> iff the given object is also an
	 *         {@link SolverParameters} object and contains the same value for each
	 *         parameter than this one.
	 */
	@Override
	public boolean equals(Object o);

	/**
	 * Retrieves a copy of the non default double values set in this object.
	 *
	 * @return not <code>null</code>.
	 */
	public Map<SolverParameterDouble, Double> getDoubleParameters();

	/**
	 * Retrieves a copy of the non default integer values set in this object.
	 *
	 * @return not <code>null</code>.
	 */
	public Map<SolverParameterInt, Integer> getIntParameters();

	/**
	 * Retrieves a copy of the non default string values set in this object.
	 *
	 * @return not <code>null</code>.
	 */
	public Map<SolverParameterString, String> getStringParameters();

	/**
	 * Retrieves the value associated with the given parameter. If the value has not
	 * been set, returns the default value for that parameter.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @return a meaningful value for that parameter, possibly <code>null</code> as
	 *         this is a meaningful value for some parameters.
	 */
	public Double getValue(SolverParameterDouble parameter);

	/**
	 * Retrieves the value associated with the given parameter. If the value has not
	 * been set, returns the default value for that parameter.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @return a meaningful value for that parameter, possibly <code>null</code> as
	 *         this is a meaningful value for some parameters.
	 */
	public Integer getValue(SolverParameterInt parameter);

	/**
	 * Retrieves the value associated with the given parameter. If the value has not
	 * been set, returns the default value for that parameter.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @return a meaningful value for that parameter, possibly <code>null</code> as
	 *         this is a meaningful value for some parameters.
	 */
	public String getValue(SolverParameterString parameter);

	/**
	 * Retrieves the value associated with the given parameter. If the value has not
	 * been set, returns the default value for that parameter. This is a non type
	 * safe method equivalent to other get methods found in this object.
	 *
	 * @param parameter
	 *            not <code>null</code>. The type must be
	 *            {@link SolverParameterInt}, {@link SolverParameterDouble},
	 *            {@link SolverParameterString} or {@link SolverParameterObject}.
	 * @return the associated value, possibly <code>null</code> as this is a
	 *         meaningful value for some parameters.
	 */
	public Object getValueAsObject(Enum<?> parameter);

	/**
	 * <p>
	 * Sets all the parameters of this object to the corresponding value set in the
	 * source parameters. The parameters which have a default value in the source
	 * are set to the default value in this object as well.
	 * </p>
	 * <p>
	 * When this method returns, this object is in the same state as the source
	 * object.
	 * </p>
	 *
	 * @param source
	 *            not <code>null</code>.
	 * @return <code>true</code> iff the state of this object changed as a result of
	 *         this call. Equivalently, <code>false</code> iff the values of the
	 *         parameters in this object were already, prior to this call, equal to
	 *         the values of the parameters in the source object.
	 */
	public boolean setAll(SolverParameters source);

	/**
	 * Sets the value associated with a parameter. The value must be a meaningful
	 * value for that parameter. To restore a parameter to its default value, use
	 * the value given by {@link SolverParametersDefaultValues}.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @param value
	 *            a meaningful value for that parameter. May be <code>null</code>
	 *            only if <code>null</code> is a meaningful value for that
	 *            parameter.
	 * @return <code>true</code> iff the state of this object changed as a result of
	 *         this call. E.g., setting a default value for a parameter that had not
	 *         previously been set returns <code>false</code>.
	 */
	public boolean setValue(SolverParameterDouble parameter, Double value);

	/**
	 * Sets the value associated with a parameter. The value must be a meaningful
	 * value for that parameter. To restore a parameter to its default value, use
	 * the value given by {@link SolverParametersDefaultValues}.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @param value
	 *            a meaningful value for that parameter. May be <code>null</code>
	 *            only if <code>null</code> is a meaningful value for that
	 *            parameter.
	 * @return <code>true</code> iff the state of this object changed as a result of
	 *         this call. E.g., setting a default value for a parameter that had not
	 *         previously been set returns <code>false</code>.
	 */
	public boolean setValue(SolverParameterInt parameter, Integer value);

	/**
	 * Sets the value associated with a parameter. The value must be a meaningful
	 * value for that parameter. To restore a parameter to its default value, use
	 * the value given by {@link SolverParametersDefaultValues}.
	 *
	 * @param parameter
	 *            not <code>null</code>.
	 * @param value
	 *            a meaningful value for that parameter. May be <code>null</code>
	 *            only if <code>null</code> is a meaningful value for that
	 *            parameter.
	 * @return <code>true</code> iff the state of this object changed as a result of
	 *         this call. E.g., setting a default value for a parameter that had not
	 *         previously been set returns <code>false</code>.
	 */
	public boolean setValue(SolverParameterString parameter, String value);

	/**
	 * Sets the value associated with a parameter. The value must be a meaningful
	 * value for that parameter. To restore a parameter to its default value, use
	 * the value given by {@link SolverParametersDefaultValues}. This is a non type
	 * safe method equivalent to other set methods found in this object.
	 *
	 * @param parameter
	 *            not <code>null</code>. The type must be
	 *            {@link SolverParameterInt}, {@link SolverParameterDouble},
	 *            {@link SolverParameterString} or {@link SolverParameterObject}.
	 * @param value
	 *            a meaningful value for that parameter. May be <code>null</code>
	 *            only if <code>null</code> is a meaningful value for that
	 *            parameter.
	 * @return <code>true</code> iff the state of this object changed as a result of
	 *         this call. E.g., setting a default value for a parameter that had not
	 *         previously been set returns <code>false</code>.
	 */
	public boolean setValueAsObject(Enum<?> parameter, Object value);
}
