package io.github.oliviercailloux.jlp.problem;

import com.google.common.base.Function;

import io.github.oliviercailloux.jlp.LpConstraint;
import io.github.oliviercailloux.jlp.LpDirection;
import io.github.oliviercailloux.jlp.LpLinear;
import io.github.oliviercailloux.jlp.LpOperator;

/**
 * A read-only view of an other problem.
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
public class LpProblemReadView<V> extends LpProblemForwarder<V> implements LpProblem<V> {

	public LpProblemReadView(LpProblem<V> delegate) {
		super(delegate);
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param constraint
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean add(LpConstraint<V> constraint) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param id
	 *            anything.
	 * @param lhs
	 *            anything.
	 * @param operator
	 *            anything.
	 * @param rhs
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean add(Object id, LpLinear<V> lhs, LpOperator operator, double rhs) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param namer
	 *            anything.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public void setConstraintsNamer(Function<LpConstraint<V>, String> namer) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param variable
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean addVariable(V variable) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param name
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean setName(String name) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param objective
	 *            anything.
	 * @param direction
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean setObjective(LpLinear<V> objective, LpDirection direction) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param direction
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean setObjectiveDirection(LpDirection direction) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param variable
	 *            anything.
	 * @param lowerBound
	 *            anything.
	 * @param upperBound
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean setVariableBounds(V variable, Number lowerBound, Number upperBound) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param namer
	 *            anything.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public void setVariablesNamer(Function<? super V, String> namer) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

	/**
	 * Throws an exception as this object is a read-only view.
	 * 
	 * @param variable
	 *            anything.
	 * @param type
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean setVariableType(V variable, LpVariableType type) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

}
