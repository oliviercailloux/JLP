package io.github.oliviercailloux.jlp.problem;

import com.google.common.base.Function;

import io.github.oliviercailloux.jlp.elements.LpConstraint;
import io.github.oliviercailloux.jlp.elements.LpDirection;
import io.github.oliviercailloux.jlp.elements.LpLinear;
import io.github.oliviercailloux.jlp.elements.LpOperator;
import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * A read-only view of an other problem.
 *
 * @author Olivier Cailloux
 *
 */
public class LpProblemReadView extends LpProblemForwarder implements LpProblem {

	public LpProblemReadView(LpProblem delegate) {
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
	public boolean add(LpConstraint constraint) {
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
	public boolean add(String id, LpLinear lhs, LpOperator operator, double rhs) {
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
	public boolean addVariable(Variable variable) {
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
	 * @param namer
	 *            anything.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public void setConstraintsNamer(Function<LpConstraint, String> namer) {
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
	public boolean setObjective(LpLinear objective, LpDirection direction) {
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
	public boolean setVariableBounds(Variable variable, Number lowerBound, Number upperBound) {
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
	public void setVariablesNamer(Function<Variable, String> namer) {
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
	public boolean setVariableType(Variable variable, LpVariableType type) {
		throw new UnsupportedOperationException("This object is a read-only view.");
	}

}
