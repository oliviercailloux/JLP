/**
 * <p>
 * Result and Solution objects for representing the output of an attempt of
 * solving a mathematical program.</em>
 * </p>
 * <p>
 * A solution object represents a feasible solution to an MP (see
 * {@link io.github.oliviercailloux.jlp.mp.IMP}). A result is different from a
 * solution: attempting to solve an MP always ends up producing a result object;
 * whereas attempting to solve an MP yields a solution object only if a feasible
 * solution has been found.
 * </p>
 */
package io.github.oliviercailloux.jlp.result;