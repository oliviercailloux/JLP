/**
 * <p>
 * <em>This package is enduring ongoing changes and should not be considered
 * public API yet.</em>
 * </p>
 * <p>
 * A solution object represents a feasible solution to an mp (see
 * {@link io.github.oliviercailloux.jlp.mp.IMP}). A result is different from a
 * solution: attempting to solve a problem always ends up producing a result
 * object; whereas solving a problem does not necessarily yield a solution to
 * the problem: a solution object only exists if a feasible solution has been
 * found. If the solve attempt resulted in an error, a result object will be
 * produced indicating that an error occurred, but no solution will be produced.
 * </p>
 */
package io.github.oliviercailloux.jlp.result;