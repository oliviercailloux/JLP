package io.github.oliviercailloux.jlp.mp;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import com.google.common.collect.ForwardingList;

import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * A list of variables in an MP. Forbids duplicates. Forbids removal of
 * variables that are used in some constraint or in the objective function.
 *
 * @author Olivier Cailloux
 *
 */
public class VariablesInMP extends ForwardingList<Variable> implements List<Variable>, RandomAccess {

	/**
	 * A read-only view of the source list of variables.
	 */
	private final List<Variable> delegate;

	private final MPBuilder source;

	/**
	 *
	 * @param source     will be used to modify the list of variables.
	 * @param sourceList will be used to read through the list of variables.
	 */
	<T extends List<Variable> & RandomAccess> VariablesInMP(MPBuilder source, T sourceList) {
		this.source = requireNonNull(source);
		delegate = Collections.unmodifiableList(sourceList);
	}

	@Override
	protected List<Variable> delegate() {
		return delegate;
	}

	@Override
	public Variable set(int index, Variable variable) {
		final Variable removed = remove(index);
		add(index, variable);
		return removed;
	}

	/**
	 * Inserts the specified variable at the specified position in this list. Shifts
	 * the element currently at that position (if any) and any subsequent elements
	 * to the right (adds one to their indices).
	 *
	 * @param index    index at which the specified element is to be inserted.
	 * @param variable not <code>null</code>, may not already exist in this list.
	 */
	@Override
	public void add(int index, Variable variable) {
		source.putVariable(index, variable, true);
	}

	/**
	 * Appends the specified variable to the end of this list.
	 *
	 * @param variable not <code>null</code>, may not already exist in this list.
	 * @return <code>true</code> (as specified by {@link Collection#add}).
	 */
	@Override
	public boolean add(Variable variable) {
		return source.putVariable(delegate.size(), variable, true);
	}

	@Override
	public boolean addAll(Collection<? extends Variable> collection) {
		return standardAddAll(collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Variable> elements) {
		return standardAddAll(index, elements);
	}

	/**
	 * Appends the specified variable to the end of this list, if it is not already
	 * in the list.
	 *
	 * @param variable not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object,
	 *         <code>false</code> iff the given variable was already in this MP.
	 */
	public boolean addIfNew(Variable variable) {
		return source.putVariable(delegate.size(), variable, false);
	}

	@Override
	public Variable remove(int index) {
		final Variable variable = get(index);
		final boolean removed = source.removeVariable(variable);
		assert removed;
		return variable;
	}

	@Override
	public boolean remove(Object object) {
		return standardRemove(object);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		return standardRemoveAll(collection);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		return standardRetainAll(collection);
	}

	@Override
	public Iterator<Variable> iterator() {
		return standardIterator();
	}

	@Override
	public ListIterator<Variable> listIterator() {
		return standardListIterator();
	}

	@Override
	public ListIterator<Variable> listIterator(int index) {
		return standardListIterator(index);
	}

}
