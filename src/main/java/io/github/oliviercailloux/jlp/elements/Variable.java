package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * <p>
 * An object which may be used to refer to a variable in a linear programming
 * context. A variable in a linear program often refers to other objects from
 * some set. Consider for example the set of variables x_i with i being taken
 * from some set I. The indice i may refer to, e.g., a product, and x refer to
 * the cost of that product. In such a case the variable name would be "x", and
 * the reference object would be a product.
 * </p>
 * <p>
 * A variable {@link #equals(Object)} an other one when both names are equal and
 * the list of reference objects are equal and in the same order.
 * </p>
 * <p>
 * This object is immutable if the objects used as references are immutable.
 * </p>
 * <p>
 * For clarity, two variables (used together in a problem) should be equal, as
 * determined by {@link #equals(Object)}, iff they have the same description, as
 * given by {@link #toString()}.
 * </p>
 * <p>
 * It is suggested that {@link #toString()} returns a short description unique
 * to that variable (in problems in which the variable will appear). For
 * example, "c-p1" for a variable representing the cost of the product number 1.
 * </p>
 * <p>
 * It is expected that this object be immutable, more precisely, their hashcode,
 * or equality status viz other variables, should not change once they have been
 * added to a constraint, or a problem. (It will also be referred to in
 * solutions of problems.) Hence, the bounds (or type) of this variable should
 * be considered as a structural property of the variable, that will never
 * change. Furthermore, the objects this variable refers to main not change
 * hashcode either during the time a problem (or other objects from this library
 * which contain variables) is used.
 * </p>
 * <p>
 * A variable bounds may be set to anything, as long as the lower bound is lower
 * than or equal to the upper bound, independently of the variable type. For
 * example, a boolean typed variable may have a lower bound of -3 and upper
 * bound of 0.8. When solving the problem, the variable will be considered as
 * having the most restrictive bounds imposed by either its bounds or its type.
 * In the example, the variable would be constrained to zero.
 * </p>
 * <p>
 * Assume you want a variable to refer to a Truck in your domain model, but
 * truck has an inappropriately long toString description. Then you can subclass
 * this class and create a TruckVariable class, with a reference to a Truck, and
 * an overridden toString.
 * </p>
 * <p>
 * If this class is inherited, the inheriting class must honor the contracts of
 * this class (objects of this library count on it), except that it may use a
 * different {@link #toString()} implementation.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Variable {

	static public Variable newInt(String name, Object... references) {
		return new Variable(name, VariableType.INT, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, references);
	}

	static public Variable newReal(String name, Object... references) {
		return new Variable(name, VariableType.REAL, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, references);
	}

	static public Variable newVariable(String name, VariableType type, Number lowerBound, Number upperBound,
			Object... references) {
		/** TODO provide static constructors for all types. */
		return new Variable(name, type, lowerBound, upperBound, references);
	}

	private Number lowerBound;

	/**
	 * Not <code>null</code>, may be empty (an empty description).
	 */
	private String name;

	/** Does not contain <code>null</code>. */
	private final ImmutableList<Object> refs;

	private VariableType type;

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
	private Variable(String name, VariableType type, Number lowerBound, Number upperBound, Object... references) {
		this.type = requireNonNull(type);
		this.lowerBound = requireNonNull(lowerBound);
		this.upperBound = requireNonNull(upperBound);
		this.name = requireNonNull(name);
		/**
		 * TODO check javadoc warnings, should trigger when type param is unused.
		 *
		 * TODO update javadoc in this constructor. Provide other constructors.
		 */
		requireNonNull(references);
		/** Note ImmutableList is hostile to nulls. */
		refs = ImmutableList.copyOf(references);
	}

	/**
	 * Indicates whether the given object represents the same variable as this one.
	 *
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> iff this variable represents the same variable as
	 *         the obj argument.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Variable)) {
			return false;
		}
		Variable other = (Variable) obj;
		if (!name.equals(other.name)) {
			return false;
		}
		if (!refs.equals(other.refs)) {
			return false;
		}
		return true;
	}

	/**
	 * @return minus infinity, for a lower bound equal to minus infinity, may not be
	 *         positive infinity.
	 */
	public Number getLowerBound() {
		return lowerBound;
	}

	/**
	 * @return not <code>null</code>.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the references associated to the variable.
	 *
	 * @return not <code>null</code>, may be empty
	 */
	public ImmutableList<Object> getReferences() {
		return refs;
	}

	public VariableType getType() {
		return type;
	}

	/**
	 * @return positive infinity, for an upper bound equal to positive infinity, may
	 *         not be negative infinity, may not be smaller than the lower bound.
	 */
	public Number getUpperBound() {
		return upperBound;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, refs);
	}

	/**
	 * Returns a short description of this variable, using its name and its
	 * references.
	 *
	 * @return not <code>null</code>.
	 */
	@Override
	public String toString() {
		/** TODO. */
		return name + "-" + Arrays.toString(refs.toArray());
	}

}
