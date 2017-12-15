/**
 * <h2>About the type of the variables</h2>
 * <p>
 * Classes such as {@link io.github.oliviercailloux.jlp.problem.LpProblem} let you
 * choose a type for the variables in the mathematical programs you create. If
 * you like to name your variables, use String. If you like to number them, use
 * Integer. For advanced use, you may create a type for your variables and make
 * every variable of your program extend (or implement) that type.
 * </p>
 * <p>
 * TODO talk about more clever variable typing.
 * </p>
 * <p>
 * Make sure that the type you use implements correctly {@link #equals(Object)}
 * and {@link #hashCode()}. Also, the variable objects <em>must</em> be
 * immutable. If you use String or Integer, there is nothing to worry about.
 * </p>
 */
package io.github.oliviercailloux.jlp.problem;