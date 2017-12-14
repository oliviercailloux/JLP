package org.decision_deck.jlp;

import java.util.List;

/**
 * <p>
 * A linear expression consisting of a sum of terms, where a term is a coefficient multiplying a variable. A linear
 * object {@link #equals(Object)} an other one iff they contain the same terms in the same order. Order of addition is
 * retained and reused when queried.
 * </p>
 * <p>
 * Such a linear object may be immutable, in which case the methods modifying the state will throw
 * {@link UnsupportedOperationException}.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
public interface LpLinear<V> extends List<LpTerm<V>> {
    /**
     * Adds a term to this linear expression.
     * 
     * @param coefficient
     *            a valid double.
     * @param variable
     *            not <code>null</code>.
     */
    public void addTerm(double coefficient, V variable);
}
