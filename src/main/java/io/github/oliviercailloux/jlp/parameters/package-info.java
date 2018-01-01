/**
 * <p>
 * <em>This package is enduring ongoing changes and should not be considered
 * public API yet.</em>
 * </p>
 * <p>
 * Objects which can be used to change the value of solving parameters, or to
 * query default parameter values.
 * </p>
 * <p>
 * The {@link io.github.oliviercailloux.jlp.parameters.SolverParameters} object
 * may be used to define a set of parameter values. It can then be bound to a
 * solver. An other possibility is to query the solver itself for its bound
 * parameters and change the returned object, through
 * {@link io.github.oliviercailloux.jlp.LpSolver#getParameters()}. The
 * documentation of
 * {@link io.github.oliviercailloux.jlp.parameters.SolverParameters} also
 * contains definitions for the terms used in the classes of these packages, for
 * example, the definition of what means a <em>meaningful</em> value for a
 * parameter.
 * </p>
 * <p>
 * The set of values a parameter can take and their meaning is documented in the
 * appropriate enum types, i.e.
 * {@link io.github.oliviercailloux.jlp.parameters.SolverParameterDouble},
 * {@link io.github.oliviercailloux.jlp.parameters.SolverParameterInt},
 * {@link io.github.oliviercailloux.jlp.parameters.SolverParameterString}. The
 * default values for all parameters may be queried from the
 * {@link io.github.oliviercailloux.jlp.parameters.SolverParametersDefaultValues}
 * class.
 * </p>
 */
package io.github.oliviercailloux.jlp.parameters;