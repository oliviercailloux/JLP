package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.google.common.base.Joiner;
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
 * A variable has a description, given by the {@link #toString()} method. The
 * description should be unique to that variable (in objects in which the
 * variable will appear). It is suggested to make it a short description, for
 * example, "c-p1" for a variable representing the cost of the product number 1.
 * </p>
 * <p>
 * A variable {@link #equals(Object)} an other one when both descriptions are
 * equal and they have the same bounds and type. (This is why it is important to
 * make the description unique.)
 * </p>
 * <p>
 * It is expected that this object be immutable. In particular, it is important
 * that their description do not change once they have been added to a
 * constraint, or a problem. (This is because hashcode, or equality status viz
 * other variables, should not change, and because it will also be referred to
 * in solutions of problems.) Hence, the bounds and type of this variable should
 * be considered as a structural property of the variable, that will never
 * change.
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
 * different {@link #toString()} implementation, subject to the constraints in
 * this documentation.
 * </p>
 * <p>
 * Rationale for the uniqueness constraint of the description: this makes it
 * possible for the user to retrieve the variable knowing only its description,
 * given a problem. We could also have made equality depend on its name and
 * references, to make it possible to retrieve the variable knowing its name and
 * references. But this has no advantage: we would then have to mandate as
 * especially important that the references do not change and identify uniquely
 * the variable, in which case it is anyway probably easy to provide a unique
 * string description; and furthermore the user would have to hold a reference
 * equal to the original reference in order to retrieve the variable, not just a
 * description of it. In any case, ensuring uniqueness of the description is a
 * good idea to make the MP contents clear, and provides for a cleaner interface
 * and concept. Furthermore, the user may with the adopted solution refer to
 * mutable objects, provided that the description itself does not change.
 * Finally, it is probably easier for the user to retrieve the description from
 * the variable name and references than the converse.
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

	static public Variable newVariable(String name, VariableType type, double lowerBound, double upperBound,
			Object... references) {
		/** TODO provide static constructors for all types. */
		return new Variable(name, type, lowerBound, upperBound, references);
	}

	private double lowerBound;

	/**
	 * Not <code>null</code>, may be empty (an empty description).
	 */
	private String name;

	/** Does not contain <code>null</code>. */
	private final ImmutableList<Object> refs;

	private VariableType type;

	private double upperBound;

	/**
	 * Builds a variable of the given name and with the provided references.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param type
	 *            not <code>null</code>.
	 * @param lowerBound
	 *            not <code>NAN</code>, not positive infinity.
	 * @param upperBound
	 *            not <code>NAN</code>, not negative infinity.
	 * @param references
	 *            not <code>null</code>, no <code>null</code> reference inside. May
	 *            be empty.
	 */
	private Variable(String name, VariableType type, double lowerBound, double upperBound, Object... references) {
		this.type = requireNonNull(type);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
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
	 *         the obj argument (as judged by their description).
	 */
	@Override
	public boolean equals(Object obj) {
		/** Includes null case: if null, is not an instance. */
		if (!(obj instanceof Variable)) {
			return false;
		}
		final Variable v2 = (Variable) obj;
		return this == v2 || (toString().equals(v2.toString()) && lowerBound == v2.lowerBound
				&& upperBound == v2.upperBound && type.equals(v2.type));
	}

	/**
	 * @return minus infinity, for a lower bound equal to minus infinity, may not be
	 *         positive infinity.
	 */
	public double getLowerBound() {
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
	public double getUpperBound() {
		return upperBound;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, refs, lowerBound, upperBound, type);
	}

	/**
	 * Returns the description of this variable, using its name and its references.
	 *
	 * TODO provide a reusable algorithm so that user can get descr from name and
	 * refs.
	 *
	 * @return not <code>null</code>.
	 */
	@Override
	public String toString() {
		/** TODO. */
		final String suff = Joiner.on('-').join(refs);
		final String sep = suff.isEmpty() ? "" : "-";
		return name + sep + suff;
	}

}
