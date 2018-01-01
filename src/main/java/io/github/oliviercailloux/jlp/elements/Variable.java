package io.github.oliviercailloux.jlp.elements;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.oliviercailloux.jlp.elements.FiniteRange.ALL_FINITE;
import static io.github.oliviercailloux.jlp.elements.FiniteRange.ZERO_ONE_RANGE;
import static io.github.oliviercailloux.jlp.elements.VariableDomain.INT_DOMAIN;
import static io.github.oliviercailloux.jlp.elements.VariableDomain.REAL_DOMAIN;
import static io.github.oliviercailloux.jlp.elements.VariableKind.BOOL_KIND;
import static io.github.oliviercailloux.jlp.elements.VariableKind.INT_KIND;
import static io.github.oliviercailloux.jlp.elements.VariableKind.REAL_KIND;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Objects;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

/**
 * <p>
 * An object which may be used to refer to a variable in a linear programming
 * context. A variable in a linear program often refers to other objects from
 * some set. Consider for example the set of variables x_i with i being taken
 * from some set I. The index i may refer to a product, and x refer to the cost
 * of that product. In such a case the variable name would be "x", and the
 * reference object would be a product.
 * </p>
 * <p>
 * A variable has a description. The description should be unique to that
 * variable (in objects in which the variable will appear). It is suggested to
 * make it a short description, for example, "c_p1" for a variable representing
 * the cost of the product number 1.
 * </p>
 * <p>
 * A variable {@link #equals(Object)} an other one when both descriptions are
 * equal and they have the same bounds and domain.
 * </p>
 * <p>
 * It is expected that this object be immutable. In particular, it is important
 * that the description does not change once a variable has been added to a
 * constraint, or a problem. (This is because hashcode, or equality status viz
 * other variables, should not change, and because it will also be referred to
 * in solutions of problems.) Hence, the bounds and domain of this variable
 * should be considered as a structural property of the variable, meaning, a
 * property that will never change.
 * </p>
 * <p>
 * A variable has a domain (integer or real), and bounds which may further
 * restrict its domain. We call this its bounded domain. For example, an integer
 * variable with a lower bound of -3.1 and upper bound of 0.8 has as bounded
 * domain {-3, -2, -1, 0}, the integers between -3 and 0. The bounds can be set
 * freely, as long as they are finite numbers and that it leaves at least one
 * finite number within the bounds. Thus, the lower bound must be lower than or
 * equal to the upper bound, and, in the case of integers, the range defined by
 * the bounds must contain an integer (for example lower bound 3.2 and upper
 * bound 3.3 is forbidden).
 * </p>
 * <p>
 * Supplementary to its domain, this library further partitions the variables
 * into three kinds of variable. A variable is of kind
 * {@link VariableKind#BOOL_KIND} iff its domain is the integers and its bounds
 * are exactly zero and one. A variable is of kind {@link VariableKind#INT_KIND}
 * iff its domain is the integers and its bounds are anything else than [0, 1].
 * A variable is of kind {@link VariableKind#REAL_KIND} iff its domain is real.
 * It follows that boolean variables have a bounded domain equal to {0, 1}, but
 * the converse does not hold: a variable of kind integer with bounds [-0.5,
 * 1.5] also has a {0, 1} bounded domain.
 * </p>
 * <p>
 * Assume you want a variable to refer to a Truck in your domain model, but
 * truck has an inappropriately long toString description. Then you can subclass
 * this class and create a TruckVariable class, with a reference to a Truck, and
 * an overridden {@link #getDescription()}. If this class is inherited, the
 * inheriting class must honor the contracts of this class (objects of this
 * library count on it), except that it may provide a different description (as
 * implementation of {@link #toString()}), subject to the constraints in this
 * documentation.
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
 * mutable objects, provided that the description itself does not change. And it
 * is probably easier for the user to retrieve the description from the variable
 * name and references than the converse. Finally, we could also rely on default
 * equality implementation (equality as identity), but this would result in two
 * variables being created with Variable.int("x") as being confusingly treated
 * as different variables, and would provide an advantage only if the user does
 * not use descriptions (e.g. uses only empty names and descriptions), which
 * renders any printing of the problem unreadable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Variable {

	/**
	 * Returns a variable of the given name and with the provided references, with
	 * domain {@link VariableDomain#INT}, bounds set at zero and one, and hence of
	 * kind {@link VariableKind#BOOL_KIND}.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param references
	 *            not <code>null</code>, no <code>null</code> reference inside. May
	 *            be empty.
	 */
	static public Variable bool(String name, Object... references) {
		return new Variable(name, INT_DOMAIN, ZERO_ONE_RANGE, references);
	}

	/**
	 * Returns the default description of a variable given its name and references.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param references
	 *            not <code>null</code>, may be empty.
	 * @return the corresponding description.
	 */
	static public String getDefaultDescription(String name, Iterable<Object> references) {
		final String suff = Joiner.on('-').join(references);
		final String sep = suff.isEmpty() ? "" : "_";
		return name + sep + suff;
	}

	/**
	 * Returns the default description of a variable given its name and references.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param references
	 *            not <code>null</code>, may be empty.
	 * @return the corresponding description.
	 */
	static public String getDefaultDescription(String name, Object... references) {
		return getDefaultDescription(name, Arrays.asList(references));
	}

	/**
	 * Returns an {@link VariableDomain#INT} variable of the given name and with the
	 * provided references, with maximal bounds.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param references
	 *            not <code>null</code>, no <code>null</code> reference inside. May
	 *            be empty.
	 */
	static public Variable integer(String name, Object... references) {
		return new Variable(name, INT_DOMAIN, ALL_FINITE, references);
	}

	/**
	 * Returns a variable with the given data. The bounds must be set as to contain
	 * at least one valid value for the variable (see {@link Variable}). Use
	 * {@link Variable#ALL_FINITE} for the maximal bounds.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param domain
	 *            not <code>null</code>.
	 * @param bounds
	 *            not <code>null</code>, each bound in this range must be of type
	 *            closed iff it is a finite number different than (positive or
	 *            negative) {@link Double#MAX_VALUE}, the lower bound must be open
	 *            iff it is negative infinity and the upper bound must be open iff
	 *            it is positive infinity.
	 * @param references
	 *            not <code>null</code>, no <code>null</code> reference inside. May
	 *            be empty.
	 * @see FiniteRange.
	 */
	static public Variable of(String name, VariableDomain domain, Range<Double> bounds, Object... references) {
		return new Variable(name, domain, bounds, references);
	}

	/**
	 * Returns a {@link VariableDomain#REAL} variable of the given name and with the
	 * provided references, with maximal bounds.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param references
	 *            not <code>null</code>, no <code>null</code> reference inside. May
	 *            be empty.
	 */
	static public Variable real(String name, Object... references) {
		return new Variable(name, REAL_DOMAIN, ALL_FINITE, references);
	}

	private final Range<Double> bounds;

	private final VariableKind kind;

	/**
	 * Not <code>null</code>, may be empty (an empty description).
	 */
	private String name;

	/** Does not contain <code>null</code>. */
	private final ImmutableList<Object> refs;

	/**
	 * Builds a variable of the given name and with the provided references.
	 *
	 * @param name
	 *            not <code>null</code>.
	 * @param domain
	 *            not <code>null</code>.
	 * @param bounds
	 *            not <code>null</code>, each bound in this range must be of type
	 *            closed iff it is a finite number different than (positive or
	 *            negative) {@link Double#MAX_VALUE}, the lower bound must be open
	 *            iff it is negative infinity and the upper bound must be open iff
	 *            it is positive infinity.
	 * @param references
	 *            not <code>null</code>, no <code>null</code> reference inside. May
	 *            be empty.
	 */
	private Variable(String name, VariableDomain domain, Range<Double> bounds, Object... references) {
		this.name = requireNonNull(name);
		requireNonNull(domain);
		this.bounds = requireNonNull(bounds);
		final boolean lowClosedFinite = bounds.hasLowerBound() && bounds.lowerBoundType() == BoundType.CLOSED
				&& Double.isFinite(bounds.lowerEndpoint()) && (Math.abs(bounds.lowerEndpoint()) != Double.MAX_VALUE);
		final boolean lowOpenInfinite = bounds.hasLowerBound() && bounds.lowerBoundType() == BoundType.OPEN
				&& bounds.lowerEndpoint().equals(Double.NEGATIVE_INFINITY);
		checkArgument(lowClosedFinite || lowOpenInfinite);

		final boolean upClosedFinite = bounds.hasUpperBound() && bounds.upperBoundType() == BoundType.CLOSED
				&& Double.isFinite(bounds.upperEndpoint()) && (Math.abs(bounds.upperEndpoint()) != Double.MAX_VALUE);
		final boolean upOpenInfinite = bounds.hasUpperBound() && bounds.upperBoundType() == BoundType.OPEN
				&& bounds.upperEndpoint().equals(Double.POSITIVE_INFINITY);
		checkArgument(upClosedFinite || upOpenInfinite);

		assert ALL_FINITE.encloses(bounds);
		/**
		 * TODO check javadoc warnings, should trigger when type param is unused.
		 */
		requireNonNull(references);
		/** Note ImmutableList is hostile to nulls. */
		refs = ImmutableList.copyOf(references);

		kind = domain == INT_DOMAIN ? (bounds.equals(ZERO_ONE_RANGE) ? BOOL_KIND : INT_KIND) : REAL_KIND;

		checkBounds(domain, bounds);
	}

	/**
	 * Indicates whether the given object represents the same variable as this one.
	 */
	@Override
	public boolean equals(Object obj) {
		/** Includes null case: if null, is not an instance. */
		if (!(obj instanceof Variable)) {
			return false;
		}
		final Variable v2 = (Variable) obj;
		return this == v2 || (toString().equals(v2.toString()) && kind.equals(v2.kind) && bounds == v2.bounds);
	}

	/**
	 * Returns the bounds of this variable.
	 *
	 * @return not <code>null</code>, a range of finite values.
	 *
	 * @see {@link FiniteRange}.
	 */
	public Range<Double> getBounds() {
		return bounds;
	}

	/**
	 * Returns the default description of this variable, using its name and its
	 * references.
	 *
	 * @see #getDefaultDescription(String, Object...)
	 *
	 * @return not <code>null</code>.
	 */
	public String getDescription() {
		return getDefaultDescription(name, refs);
	}

	public VariableDomain getDomain() {
		return kind.getDomain();
	}

	public VariableKind getKind() {
		return kind;
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

	@Override
	public int hashCode() {
		return Objects.hash(toString(), kind, bounds);
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this).add("name", name).add("domain", kind.getDomain())
				.add("bounds", bounds).add("refs", refs);
		return helper.toString();
	}

	/**
	 * Checks that the bounded domain is non empty, thus, that the bounds contain at
	 * least one number in the domain. (This is only necessary for integer
	 * variables, given the other checks in this class.)
	 */
	private void checkBounds(VariableDomain d, Range<Double> r) throws IllegalArgumentException {
		if (d == REAL_DOMAIN) {
			return;
		}
		/**
		 * Example with lowerBound = 3.2, upperBound = 3.4. effUp = 3, effDown = 4,
		 * crash.
		 */
		final double effUp = Math.floor(r.upperEndpoint());
		final double effDown = Math.ceil(r.lowerEndpoint());
		checkArgument(effDown <= effUp);
	}

}
