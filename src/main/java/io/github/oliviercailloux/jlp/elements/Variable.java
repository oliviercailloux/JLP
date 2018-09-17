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
 * context. A variable, as represented by a Variable object, has a categorical
 * name and possibly references. For example, a variable x_i referring to the
 * cost of the product i would have “x” as a categorical name and would have a
 * reference to the product referred to by the index i.
 * </p>
 * <p>
 * A variable has a (non-empty) description. The description should be unique to
 * that variable (in objects in which the variable will appear). The default
 * description provided by this object uses its categorical name and its
 * references (by calling {@link #toString()} on the references). To use other
 * descriptions, you are advised to create classes extending {@link Variable}
 * and override {@link #getDescription()}. It is suggested to make sure the
 * description is short, for example, "c_p1" for a variable representing the
 * cost of the product number 1. (Some details follow. Assume you want a
 * variable to refer to a Truck in your domain model, but truck has an
 * inappropriately long toString description. Then you can subclass this class
 * and create a TruckVariable class, with a reference to a Truck, and an
 * overridden {@link #getDescription()}. If this class is inherited, the
 * inheriting class must honor the contracts of this class (objects of this
 * library count on it), except that it may provide a different description,
 * provided it is immutable.)
 * </p>
 * <p>
 * A variable has a domain (integer or real), and bounds which may further
 * restrict its domain. The domain of the variable after restriction to the
 * given bounds is called its bounded domain. For example, an integer variable
 * with a lower bound of -3.1 and upper bound of 0.8 has as bounded domain {-3,
 * -2, -1, 0}, the integers between -3 and 0. The bounds can be set freely, as
 * long as they are finite numbers and that it leaves at least one finite number
 * within the bounds. Thus, the lower bound must be less than or equal to the
 * upper bound, and, in the case of integers, the range defined by the bounds
 * must contain an integer (for example lower bound 3.2 and upper bound 3.3 is
 * forbidden).
 * </p>
 * <p>
 * A variable {@link #equals(Object)} an other one when they have equal
 * descriptions, equal bounds, and equal domains.
 * </p>
 * <p>
 * It is expected that this object be immutable. In particular, it is important
 * that its equality status does not change once a variable has been added to a
 * constraint, or to an MP. (This is because hashcode, or equality status viz
 * other variables, should not change once objects are stored in collections,
 * and because it will also be referred to in solutions of MPs.) Hence, the
 * description, bounds and domain of this variable should be considered as
 * structural properties of the variable, meaning, a property that will never
 * change.
 * </p>
 * <p>
 * Supplementary to its domain, this library further partitions the variables
 * into three kinds of variable. A variable is of kind
 * <ul>
 * <li>{@link VariableKind#BOOL_KIND} iff its domain is the integers and its
 * bounds are exactly [0, 1];</li>
 * <li>{@link VariableKind#INT_KIND} iff its domain is the integers and its
 * bounds are anything else than [0, 1];</li>
 * <li>{@link VariableKind#REAL_KIND} iff its domain is real.
 * </ul>
 * It follows that boolean variables have a bounded domain equal to {0, 1}, but
 * the converse does not hold: a variable of kind integer with bounds [-0.5,
 * 1.5] also has a {0, 1} bounded domain.
 * </p>
 * <p>
 * For the curious reader, here is the rationale for the uniqueness constraint
 * of the description: this makes it possible for the user to retrieve the
 * variable knowing only its description, given an MP. We could also have made
 * equality depend on its categorical name and references, to make it possible
 * to retrieve the variable knowing those values. But this has no advantage: we
 * would then have to mandate as especially important that the references do not
 * change and identify uniquely the variable, in which case it is anyway
 * probably easy to provide a unique string description; and furthermore the
 * user would have to hold a reference equal to the original reference in order
 * to retrieve the variable, not just a description of it. In any case, ensuring
 * uniqueness of the description is a good idea to make the MP contents clear,
 * and provides for a cleaner interface and concept. Furthermore, the user may
 * with the adopted solution refer to mutable objects, provided that the
 * description itself does not change. And it is probably easier for the user to
 * retrieve the description from the variable categorical name and references
 * than the converse. Finally, we could also rely on default equality
 * implementation (equality as identity), but this would result in two variables
 * being created with Variable.int("x") as being confusingly treated as
 * different variables, and would provide an advantage only if the user does not
 * use descriptions (e.g. uses only empty categorical names and descriptions),
 * which renders any printing of the MP unreadable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class Variable {

	/**
	 * Returns the default description of a variable given its categorical name and
	 * references.
	 *
	 * @param categoricalName not <code>null</code>, may be empty only if at least
	 *                        one reference is given.
	 * @param references      not <code>null</code>, may be empty only if
	 *                        categoricalName is not empty, may not contain
	 *                        <code>null</code>.
	 * @return the corresponding description, not <code>null</code>, not empty.
	 */
	public static String getDefaultDescription(String categoricalName, Iterable<?> references) {
		checkArgument(!categoricalName.isEmpty() || references.iterator().hasNext());
		final String suff = Joiner.on('-').join(references);
		final String sep = suff.isEmpty() ? "" : "_";
		return categoricalName + sep + suff;
	}

	/**
	 * Returns the default description of a variable given its categorical name and
	 * references.
	 *
	 * @param categoricalName not <code>null</code>, may be empty only if at least
	 *                        one reference is given.
	 * @param references      not <code>null</code>, may be empty only if
	 *                        categoricalName is not empty, may not contain
	 *                        <code>null</code>.
	 * @return the corresponding description, not <code>null</code>, not empty.
	 */
	public static String getDefaultDescription(String categoricalName, Object... references) {
		return getDefaultDescription(categoricalName, Arrays.asList(references));
	}

	/**
	 * Returns a variable with the given categorical name and references, with
	 * domain {@link VariableDomain#INT_DOMAIN}, bounds set at zero and one, and
	 * hence of kind {@link VariableKind#BOOL_KIND}.
	 *
	 * @param categoricalName not <code>null</code>, may be empty only if at least
	 *                        one reference is given.
	 * @param references      not <code>null</code>, may be empty only if
	 *                        categoricalName is not empty, may not contain
	 *                        <code>null</code>.
	 */
	public static Variable bool(String categoricalName, Object... references) {
		return new Variable(categoricalName, INT_DOMAIN, ZERO_ONE_RANGE, ImmutableList.copyOf(references));
	}

	/**
	 * Returns an {@link VariableDomain#INT_DOMAIN} variable with the given
	 * categorical name and references, with maximal bounds.
	 *
	 * @param categoricalName not <code>null</code>, may be empty only if at least
	 *                        one reference is given.
	 * @param references      not <code>null</code>, may be empty only if
	 *                        categoricalName is not empty, may not contain
	 *                        <code>null</code>.
	 */
	public static Variable integer(String categoricalName, Object... references) {
		return new Variable(categoricalName, INT_DOMAIN, ALL_FINITE, ImmutableList.copyOf(references));
	}

	/**
	 * Returns a {@link VariableDomain#REAL_DOMAIN} variable with the given
	 * categorical name and references, with maximal bounds.
	 *
	 * @param categoricalName not <code>null</code>, may be empty only if at least
	 *                        one reference is given.
	 * @param references      not <code>null</code>, may be empty only if
	 *                        categoricalName is not empty, may not contain
	 *                        <code>null</code>.
	 */
	public static Variable real(String categoricalName, Object... references) {
		return new Variable(categoricalName, REAL_DOMAIN, ALL_FINITE, ImmutableList.copyOf(references));
	}

	/**
	 * Returns a variable with the given data. The bounds must be set as to contain
	 * at least one valid value for the variable (see {@link Variable}). Use
	 * {@link FiniteRange#ALL_FINITE} for the maximal bounds.
	 *
	 * @param categoricalName not <code>null</code>, may be empty only if at least
	 *                        one reference is given.
	 * @param domain          not <code>null</code>.
	 * @param bounds          not <code>null</code>, each bound in this range must
	 *                        be of type closed iff it is a finite number different
	 *                        than (positive or negative) {@link Double#MAX_VALUE},
	 *                        the lower bound must be open iff it is negative
	 *                        infinity and the upper bound must be open iff it is
	 *                        positive infinity.
	 * @param references      not <code>null</code>, may be empty only if
	 *                        categoricalName is not empty, may not contain
	 *                        <code>null</code>.
	 * @see FiniteRange
	 */
	public static Variable of(String categoricalName, VariableDomain domain, Range<Double> bounds,
			Iterable<?> references) {
		return new Variable(categoricalName, domain, bounds, references);
	}

	/**
	 * Not <code>null</code>, may be empty.
	 */
	private final String categoricalName;

	private final Range<Double> bounds;

	private final VariableKind kind;

	/** Does not contain <code>null</code>. */
	private final ImmutableList<Object> refs;

	/**
	 * Builds a variable with the given categorical name and references.
	 *
	 * @param categoricalName not <code>null</code>, may be empty only if references
	 *                        is not empty.
	 * @param domain          not <code>null</code>.
	 * @param bounds          not <code>null</code>, each bound in this range must
	 *                        be of type closed iff it is a finite number different
	 *                        than (positive or negative) {@link Double#MAX_VALUE},
	 *                        the lower bound must be open iff it is negative
	 *                        infinity and the upper bound must be open iff it is
	 *                        positive infinity.
	 * @param references      not <code>null</code>, may be empty only if
	 *                        categoricalName is not empty, may not contain
	 *                        <code>null</code>.
	 */
	private Variable(String categoricalName, VariableDomain domain, Range<Double> bounds, Iterable<?> references) {
		this.categoricalName = requireNonNull(categoricalName);
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
		requireNonNull(references);
		/** Note that ImmutableList is hostile to nulls. */
		refs = ImmutableList.copyOf(references);

		switch (domain) {
		case INT_DOMAIN:
			kind = bounds.equals(ZERO_ONE_RANGE) ? BOOL_KIND : INT_KIND;
			break;
		case REAL_DOMAIN:
			kind = REAL_KIND;
			break;
		default:
			throw new AssertionError();
		}

		checkBounds(domain, bounds);
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

	/**
	 * @return not <code>null</code>.
	 */
	public String getCategoricalName() {
		return categoricalName;
	}

	/**
	 * Returns the bounds of this variable.
	 *
	 * @return not <code>null</code>, a range of finite values.
	 *
	 * @see FiniteRange
	 */
	public Range<Double> getBounds() {
		return bounds;
	}

	public VariableDomain getDomain() {
		return kind.getDomain();
	}

	public VariableKind getKind() {
		return kind;
	}

	/**
	 * Returns the references associated to the variable.
	 *
	 * @return not <code>null</code>, may be empty.
	 */
	public ImmutableList<Object> getReferences() {
		return refs;
	}

	/**
	 * Returns the default description of this variable, using its name and its
	 * references.
	 *
	 * @see #getDefaultDescription(String, Object...)
	 *
	 * @return not <code>null</code>, not empty.
	 */
	public String getDescription() {
		return getDefaultDescription(categoricalName, refs);
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
		return this == v2
				|| (getDescription().equals(v2.getDescription()) && kind.equals(v2.kind) && bounds == v2.bounds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getDescription(), kind, bounds);
	}

	/**
	 * Returns a string representation of this variable (useful for debug). This may
	 * differ from its description (the description is supposedly even shorter than
	 * this debug string).
	 *
	 * @see #getDescription()
	 */
	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this).add("name", categoricalName)
				.add("domain", kind.getDomain()).add("bounds", bounds).add("refs", refs);
		return helper.toString();
	}

}
