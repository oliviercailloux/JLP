package io.github.oliviercailloux.jlp.elements;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

/**
 * <p>
 * A linear expression consisting of a possibly empty sum of terms, where a term
 * is a coefficient multiplying a variable. A linear object
 * {@link #equals(Object)} an other one iff they contain the same terms in the
 * same order. Order of addition is retained and reused when queried.
 * </p>
 * <p>
 * Immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class SumTerms extends ForwardingList<Term> {
	static public SumTermsBuilder builder() {
		return new SumTermsBuilder();
	}

	/**
	 * Retrieves a linear, immutable object containing the given terms.
	 *
	 * @param terms
	 *            not <code>null</code>.
	 * @return a linear object.
	 */
	static public SumTerms of(Collection<Term> terms) {
		requireNonNull(terms);
		if (terms instanceof SumTerms) {
			return (SumTerms) terms;
		}
		return new SumTerms(terms);
	}

	static public SumTerms of(double c1, Variable v1) {
		final List<Term> asList = Lists.newLinkedList();
		asList.add(new Term(c1, v1));
		return new SumTerms(asList);
	}

	static public SumTerms of(double c1, Variable v1, double c2, Variable v2) {
		final List<Term> asList = Lists.newLinkedList();
		asList.add(new Term(c1, v1));
		asList.add(new Term(c2, v2));
		return new SumTerms(asList);
	}

	static public SumTerms of(double c1, Variable v1, double c2, Variable v2, double c3, Variable v3) {
		final List<Term> asList = Lists.newLinkedList();
		asList.add(new Term(c1, v1));
		asList.add(new Term(c2, v2));
		asList.add(new Term(c3, v3));
		return new SumTerms(asList);
	}

	static public SumTerms of(Term... terms) {
		return new SumTerms(Arrays.asList(terms));
	}

	private final ImmutableList<Term> delegate;

	private ImmutableList<Variable> variables;

	private SumTerms(Collection<Term> terms) {
		delegate = ImmutableList.copyOf(terms);
		initVariables();
	}

	/**
	 * Returns the variables used in this sum, with duplicates iff multiple terms
	 * use the same variable.
	 *
	 * @return a list that has the same size as this list.
	 *
	 */
	public ImmutableList<Variable> getVariables() {
		return variables;
	}

	/**
	 * Returns a string representation of the given linear expression.
	 *
	 * @param linear
	 *            not <code>null</code>
	 * @return not <code>null</code>, may be empty
	 */
	@Override
	public String toString() {
		return Joiner.on(" + ").join(this);
	}

	private void initVariables() {
		final Builder<Variable> builder = ImmutableList.builder();
		for (Term term : this) {
			builder.add(term.getVariable());
		}
		variables = builder.build();
	}

	@Override
	protected List<Term> delegate() {
		return Collections.unmodifiableList(delegate);
	}
}
