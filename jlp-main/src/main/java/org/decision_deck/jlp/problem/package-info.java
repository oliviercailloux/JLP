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
/**
 * <h2>About the type of the variables</h2><p>Classes such as {@link org.decision_deck.jlp.problem.LpProblem}
 *  let you choose a type for the variables in the mathematical programs you create. If you like to name your variables, use String. If you like to number them, use
 *            Integer. For advanced use, you may create a type for your variables and make every variable of your
 *            program extend (or implement) that type.</p><p>TODO talk about more clever
 *  variable typing.</p><p>Make sure that the type you use implements correctly
 *            {@link #equals(Object)} and {@link #hashCode()}. Also, the variable objects <em>must</em> be immutable. If
 *            you use String or Integer, there is nothing to worry about.</p>
 */
package org.decision_deck.jlp.problem;