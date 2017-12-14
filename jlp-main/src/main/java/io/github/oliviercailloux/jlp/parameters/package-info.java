/**
 * <p>
 * Contains objects which can be used to change the value of solving parameters,
 * or to query default parameter values.
 * </p>
 * <p>
 * The {@link io.github.oliviercailloux.jlp.parameters.LpParameters} object may be used
 * to define a set of parameter values. It can then be bound to a solver. An
 * other possibility is to query the solver itself for its bound parameters and
 * change the returned object, through
 * {@link io.github.oliviercailloux.jlp.LpSolver#getParameters()}. The documentation of
 * {@link io.github.oliviercailloux.jlp.parameters.LpParameters} also contains
 * definitions for the terms used in the classes of these packages, for example,
 * the definition of what means a <em>meaningful</em> value for a parameter.
 * </p>
 * <p>
 * The set of values a parameter can take and their meaning is documented in the
 * appropriate enum types, i.e.
 * {@link io.github.oliviercailloux.jlp.parameters.LpDoubleParameter},
 * {@link io.github.oliviercailloux.jlp.parameters.LpIntParameter},
 * {@link io.github.oliviercailloux.jlp.parameters.LpStringParameter},
 * {@link io.github.oliviercailloux.jlp.parameters.LpObjectParameter}. The default
 * values for all parameters may be queried from the
 * {@link io.github.oliviercailloux.jlp.parameters.LpParametersDefaultValues} class.
 * </p>
 */
package io.github.oliviercailloux.jlp.parameters;