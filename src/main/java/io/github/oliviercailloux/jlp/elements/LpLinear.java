package io.github.oliviercailloux.jlp.elements;

import java.util.List;

/**
 * <p>
 * A linear expression consisting of a sum of terms, where a term is a
 * coefficient multiplying a variable. A linear object {@link #equals(Object)}
 * an other one iff they contain the same terms in the same order. Order of
 * addition is retained and reused when queried.
 * </p>
 * <p>
 * Such a linear object may be immutable, in which case the methods modifying
 * the state will throw {@link UnsupportedOperationException}.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public interface LpLinear extends List<LpTerm> {
	/**
	 * Adds a term to this linear expression.
	 *
	 * @param coefficient
	 *            a valid double.
	 * @param variable
	 *            not <code>null</code>.
	 */
	public void addTerm(double coefficient, Variable variable);
}
