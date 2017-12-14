package org.decision_deck.jlp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * <p>
 * An object which may be used to refer to a variable in a linear programming context. A variable in a linear program
 * often refers to other objects from some set. Consider for example the set of variables x_i with i being taken from
 * some set I. The indice i may refer to, e.g., a product, and x refer to the cost of that product. In such a case the
 * variable category could be the element "COST" from some enum type, and the reference object would be a product. Such
 * a variable {@link #equals(Object)} an other one when both categories are equal and the list of reference objects are
 * equal and in the same order. Equality tests for the category and reference object use their {@link #equals(Object)}
 * method.
 * </p>
 * <p>
 * Immutable if the category type and objects used as references are immutable. It is strongly suggested to use
 * immutable types as this object does not build defensive copies. As an exception to the immutability of this object,
 * it provides a setter for the string description of the variable. As it is intended to be used only for description
 * (e.g. debugging) purposes in the {@link #toString()} method, it is deemed acceptable to be less strict on the
 * immutability of the string parameter. However the recommanded practice is to set the string description right after
 * creation of the object, before doing anything else with it, and never change it afterwards. This has the effect of
 * defining this object as practically immutable.
 * </p>
 * <p>
 * A <code>null</code> variable category is <em>not</em> allowed (a simpler object could be created for that use case if
 * deemed useful). An empty reference set is allowed.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            a type used to describe the category an instance variable belongs to. Typically, an enum type. Should most
 *            preferably be immutable.
 */
public class LpVariable<V> {

    /** Not <code>null</code>. */
    private final V m_category;

    /**
     * May be <code>null</code> (no manual description, switch to automatic), may be empty (an empty description).
     */
    private String m_descr;

    /** Does not contain <code>null</code>. */
    private final List<Object> m_refs;

    /**
     * Builds a variable of the given category and with the provided references.
     * 
     * @param category
     *            not <code>null</code>.
     * @param references
     *            not <code>null</code>, no <code>null</code> reference inside. May be empty.
     */
    public LpVariable(V category, Object... references) {
	Preconditions.checkNotNull(category);
	Preconditions.checkNotNull(references);
	final List<Object> asList = Arrays.asList(references);
	final boolean hasNull = Iterables.any(asList, Predicates.isNull());
	if (hasNull) {
	    throw new NullPointerException("Given references contain a null reference.");
	}
	m_category = category;
	m_refs = Collections.unmodifiableList(asList);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof LpVariable)) {
	    return false;
	}
	LpVariable<?> other = (LpVariable<?>) obj;
	if (!m_category.equals(other.m_category)) {
	    return false;
	}
	if (!m_refs.equals(other.m_refs)) {
	    return false;
	}
	return true;
    }

    /**
     * @return not <code>null</code>.
     */
    public V getCategory() {
	return m_category;
    }

    /**
     * Retrieves the string set as a description of this object.
     * 
     * @return may be <code>null</code>, but not empty.
     */
    public String getDescription() {
	return m_descr;
    }

    /**
     * Retrieves a copy, or read-only view, of the references associated to the variable.
     * 
     * @return not <code>null</code>, may be empty. Contain no <code>null</code> references.
     */
    public List<Object> getReferences() {
	return m_refs;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (m_category.hashCode());
	result = prime * result + (m_refs.hashCode());
	return result;
    }

    /**
     * Sets the description to be used for this variable by the {@link #toString()} method. An empty string is accepted
     * and will produce an empty answer by the {@link #toString()} method. It is recommended to avoid empty strings and
     * strings empty after {@link String#trim()}ming.
     * 
     * @param description
     *            <code>null</code> to remove the manual description (use automatic description).
     */
    public void setDescription(String description) {
	m_descr = description;
    }

    /**
     * Returns a string description of this variable, using the description set by {@link #setDescription(String)} if it
     * is not <code>null</code> or auto-generated from the category and the references otherwise.
     */
    @Override
    public String toString() {
	if (m_descr != null) {
	    return m_descr;
	}
	final ToStringHelper stringHelper = Objects.toStringHelper(this);
	stringHelper.add("Category", m_category);
	stringHelper.add("References", Arrays.toString(m_refs.toArray()));
	return stringHelper.toString();
    }

}
