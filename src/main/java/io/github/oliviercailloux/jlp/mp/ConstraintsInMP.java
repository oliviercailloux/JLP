package io.github.oliviercailloux.jlp.mp;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import com.google.common.collect.ForwardingList;

import io.github.oliviercailloux.jlp.elements.Constraint;

class ConstraintsInMP extends ForwardingList<Constraint> implements List<Constraint>, RandomAccess {

	/**
	 * A view of the source list of constraints, also used to remove constraints.
	 */
	private final List<Constraint> delegate;

	private final MPBuilder source;

	/**
	 *
	 * @param source     will be used to modify the list of constraints.
	 * @param sourceList will be used to read through the list of constraints and to
	 *                   remove constraints.
	 */
	<T extends List<Constraint> & RandomAccess> ConstraintsInMP(MPBuilder source, T sourceList) {
		this.source = requireNonNull(source);
		delegate = sourceList;
	}

	@Override
	protected List<Constraint> delegate() {
		return delegate;
	}

	/**
	 * Appends the specified constraint to the end of this list.
	 *
	 * @param constraint not <code>null</code>.
	 * @return <code>true</code> (as specified by {@link Collection#add}).
	 */
	@Override
	public boolean add(Constraint constraint) {
		return source.putConstraint(delegate.size(), constraint);
	}

	/**
	 * Inserts the specified constraint at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 *
	 * @param index      index at which the specified element is to be inserted.
	 * @param constraint not <code>null</code>.
	 */
	@Override
	public void add(int index, Constraint constraint) {
		source.putConstraint(index, constraint);
	}

	@Override
	public boolean addAll(Collection<? extends Constraint> collection) {
		return standardAddAll(collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Constraint> elements) {
		return standardAddAll(index, elements);
	}

	@Override
	public Constraint set(int index, Constraint constraint) {
		final Constraint removed = remove(index);
		add(index, constraint);
		return removed;
	}
}
