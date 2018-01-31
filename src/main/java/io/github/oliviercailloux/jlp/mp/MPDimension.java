package io.github.oliviercailloux.jlp.mp;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.oliviercailloux.jlp.elements.VariableKind.BOOL_KIND;
import static io.github.oliviercailloux.jlp.elements.VariableKind.INT_KIND;
import static io.github.oliviercailloux.jlp.elements.VariableKind.REAL_KIND;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedMultiset.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.SortedMultiset;

import io.github.oliviercailloux.jlp.elements.VariableDomain;
import io.github.oliviercailloux.jlp.elements.VariableKind;

/**
 * <p>
 * The dimension of an MP, in numbers of variables (by kind) and constraints.
 * </p>
 * <p>
 * Immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class MPDimension {

	/**
	 * Returns a dimension with the given counts of variables by kind and of
	 * constraints.
	 *
	 * @param boolsCount
	 *            a non-negative number.
	 * @param intsNotBoolCount
	 *            a non-negative number.
	 * @param realsCount
	 *            a non-negative number.
	 * @param constraintsCount
	 *            a non-negative number.
	 * @return not <code>null</code>.
	 */
	static public MPDimension of(int boolsCount, int intsNotBoolCount, int realsCount, int constraintsCount) {
		checkArgument(boolsCount >= 0);
		checkArgument(intsNotBoolCount >= 0);
		checkArgument(realsCount >= 0);
		checkArgument(constraintsCount >= 0);
		final ImmutableSortedMultiset<VariableKind> variablesCount = ImmutableSortedMultiset
				.<VariableKind>naturalOrder().setCount(BOOL_KIND, boolsCount).setCount(INT_KIND, intsNotBoolCount)
				.setCount(REAL_KIND, realsCount).build();
		return new MPDimension(variablesCount, constraintsCount);
	}

	/**
	 * Returns a dimension with the given counts of variables by kind and
	 * constraints. This constructor is especially handy when used with a
	 * {@link EnumMultiset}{@code <VariableKind>} as a first argument.
	 *
	 * @param variablesKinds
	 *            not <code>null</code>.
	 * @param constraintsCount
	 *            at least zero.
	 */
	static public MPDimension of(Iterable<VariableKind> variablesKinds, int constraintsCount) {
		return new MPDimension(variablesKinds, constraintsCount);
	}

	private final int constraintsCount;

	private final ImmutableSortedMultiset<VariableKind> variablesCounts;

	/**
	 * Returns a dimension with the given counts of variables by kind and
	 * constraints.
	 *
	 * @param variablesKinds
	 *            not <code>null</code>.
	 * @param constraintsCount
	 *            at least zero.
	 */
	private MPDimension(Iterable<VariableKind> variablesKinds, int constraintsCount) {
		if (variablesKinds instanceof SortedMultiset) {
			final SortedMultiset<VariableKind> sortedVC = (SortedMultiset<VariableKind>) variablesKinds;
			variablesCounts = ImmutableSortedMultiset.copyOfSorted(sortedVC);
		} else {
			final Builder<VariableKind> builder = ImmutableSortedMultiset.naturalOrder();
			for (VariableKind variableKind : VariableKind.values()) {
				builder.setCount(variableKind, Iterables.frequency(variablesKinds, variableKind));
			}
			variablesCounts = builder.build();
		}
		checkArgument(constraintsCount >= 0);
		this.constraintsCount = constraintsCount;
	}

	/**
	 * Indicates whether <code>obj</code> is equal to this one. This object equals
	 * <code>obj</code> iff <code>obj</code> is an {@link MPDimension} and has the
	 * same count for all variable kinds and constraints.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MPDimension)) {
			return false;
		}

		final MPDimension d2 = (MPDimension) obj;
		if (this == d2) {
			return true;
		}

		if (!variablesCounts.equals(d2.variablesCounts)) {
			return false;
		}
		return constraintsCount == d2.constraintsCount;
	}

	/**
	 * Returns the number of variables of kind {@link VariableKind#BOOL_KIND}.
	 *
	 * @return a non-negative number.
	 */
	public int getBoolsCount() {
		return variablesCounts.count(BOOL_KIND);
	}

	/**
	 * Returns the number of constraints.
	 *
	 * @return a non-negative number.
	 */
	public int getConstraintsCount() {
		return constraintsCount;
	}

	/**
	 * Returns the number of variables having domain
	 * {@link VariableDomain#INT_DOMAIN}, that is, the number of variables of kind
	 * {@link VariableKind#BOOL_KIND} plus the number of variables of kind
	 * {@link VariableKind#INT_KIND}.
	 *
	 * @return a non-negative number.
	 */
	public int getIntegerDomainsCount() {
		return getBoolsCount() + getIntsNotBoolCount();
	}

	/**
	 * Returns the number of variables of kind {@link VariableKind#INT_KIND}.
	 *
	 * @return a non-negative number.
	 */
	public int getIntsNotBoolCount() {
		return variablesCounts.count(INT_KIND);
	}

	/**
	 * Returns the number of variables of kind {@link VariableKind#REAL_KIND},
	 * equivalently, the number of variables having domain
	 * {@link VariableDomain#REAL_DOMAIN}.
	 *
	 * @return a non-negative number.
	 */
	public int getRealsCount() {
		return variablesCounts.count(REAL_KIND);
	}

	/**
	 * Returns the number of variables (of all kinds, equivalently, having any
	 * domain).
	 *
	 * @return a non-negative number.
	 */
	public int getVariablesCount() {
		return variablesCounts.size();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(variablesCounts, constraintsCount);
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		for (VariableKind variableKind : VariableKind.values()) {
			helper.add(variableKind.toString() + " nb", variablesCounts.count(variableKind));
		}
		helper.add("constraints nb", constraintsCount);
		return helper.toString();
	}
}
