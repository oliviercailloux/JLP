package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import io.github.oliviercailloux.jlp.problem.LpVariableType;

/**
 * <p>
 * An object which may be used to refer to a variable in a linear programming
 * context. A variable in a linear program often refers to other objects from
 * some set. Consider for example the set of variables x_i with i being taken
 * from some set I. The indice i may refer to, e.g., a product, and x refer to
 * the cost of that product. In such a case the variable name would be "x", and
 * the reference object would be a product. Such a variable
 * {@link #equals(Object)} an other one when both names are equal and the list
 * of reference objects are equal and in the same order.
 * </p>
 * <p>
 * This object is immutable if the objects used as references are immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class NamedVariable implements Variable {

	static public NamedVariable newInt(String name, Object... references) {
		return new NamedVariable(name, LpVariableType.INT, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
				references);
	}

	static public NamedVariable newNamedVariable(String name, LpVariableType type, Number lowerBound, Number upperBound,
			Object... references) {
		/** TODO provide static constructors for all types. */
		return new NamedVariable(name, type, lowerBound, upperBound, references);
	}

	static public NamedVariable newReal(String name, Object... references) {
		return new NamedVariable(name, LpVariableType.REAL, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
				references);
	}

	private Number lowerBound;

	/**
	 * Not <code>null</code>, may be empty (an empty description).
	 */
	private String m_name;

	/** Does not contain <code>null</code>. */
	private final List<Object> m_refs;

	private LpVariableType type;

	private Number upperBound;

	/**
	 * Builds a variable of the given name and with the provided references.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param type
	 *            not <code>null</code>.
	 * @param lowerBound
	 *            not <code>null</code>.
	 * @param upperBound
	 *            not <code>null</code>.
	 * @param references
	 *            not <code>null</code>, no <code>null</code> reference inside. May
	 *            be empty.
	 */
	private NamedVariable(String name, LpVariableType type, Number lowerBound, Number upperBound,
			Object... references) {
		this.type = requireNonNull(type);
		this.lowerBound = requireNonNull(lowerBound);
		this.upperBound = requireNonNull(upperBound);
		m_name = requireNonNull(name);
		/**
		 * TODO check javadoc warnings, should trigger when type param is unused.
		 *
		 * TODO update javadoc in this constructor. Provide other constructors.
		 */
		requireNonNull(references);
		final List<Object> asList = Arrays.asList(references);
		final boolean hasNull = Iterables.any(asList, Predicates.isNull());
		if (hasNull) {
			throw new NullPointerException("Given references contain a null reference.");
		}
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
		if (!(obj instanceof NamedVariable)) {
			return false;
		}
		NamedVariable other = (NamedVariable) obj;
		if (!m_name.equals(other.m_name)) {
			return false;
		}
		if (!m_refs.equals(other.m_refs)) {
			return false;
		}
		return true;
	}

	@Override
	public Number getLowerBound() {
		return lowerBound;
	}

	/**
	 * @return not <code>null</code>.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Retrieves a copy, or read-only view, of the references associated to the
	 * variable.
	 *
	 * @return not <code>null</code>, may be empty. Contain no <code>null</code>
	 *         references.
	 */
	public List<Object> getReferences() {
		return m_refs;
	}

	@Override
	public LpVariableType getType() {
		return type;
	}

	@Override
	public Number getUpperBound() {
		return upperBound;
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_name, m_refs);
	}

	/**
	 * Returns a short description of this variable, using its name and its
	 * references.
	 */
	@Override
	public String toString() {
		/** TODO. */
		return m_name + "-" + Arrays.toString(m_refs.toArray());
	}

}
