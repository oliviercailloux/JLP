package org.decision_deck.jlp;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;

/**
 * <p>
 * A term in a linear expression in a linear program or mixed integer program, consisting of a coefficient multiplying a
 * variable.
 * </p>
 * <p>
 * Such terms should be used with immutable variables, and should be considered as immutable. An object of this class is
 * indeed immutable iff the variable type is immutable.
 * </p>
 * 
 * @author Olivier Cailloux
 * @param <V>
 *            the type of the variables, should be immutable.
 * 
 */
public class LpTerm<V> {
    private final double m_coefficient;

    private final V m_variable;

    /**
     * @param coefficient
     *            a valid number.
     * @param variable
     *            not <code>null</code>.
     */
    public LpTerm(double coefficient, V variable) {
	Preconditions.checkNotNull(variable);
	Preconditions.checkArgument(!Double.isInfinite(coefficient));
	Preconditions.checkArgument(!Double.isNaN(coefficient));
	m_coefficient = coefficient;
	m_variable = variable;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!(obj instanceof LpTerm<?>)) {
	    return false;
	}

	LpTerm<?> t2 = (LpTerm<?>) obj;
	if (m_coefficient != t2.m_coefficient) {
	    return false;
	}
	if (!m_variable.equals(t2.m_variable)) {
	    return false;
	}

	return true;
    }

    /**
     * Retrieves the coefficient that multiplies the variable in this term.
     * 
     * @return a valid number.
     */
    public double getCoefficient() {
	return m_coefficient;
    }

    /**
     * Retrieves the variable of this term.
     * 
     * @return not <code>null</code>.
     */
    public V getVariable() {
	return m_variable;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	long temp;
	temp = Double.doubleToLongBits(m_coefficient);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	result = prime * result + m_variable.hashCode();
	return result;
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.addValue(getCoefficient() + "*" + getVariable());
	return helper.toString();
    }
}
