package io.github.oliviercailloux.jlp.problem;

import com.google.common.base.Function;

import io.github.oliviercailloux.jlp.elements.LpConstraint;
import io.github.oliviercailloux.jlp.elements.LpDirection;
import io.github.oliviercailloux.jlp.elements.LpLinear;
import io.github.oliviercailloux.jlp.elements.LpOperator;
import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * An immutable problem. To obtain such a problem, use
 * {@link LpProblems#newProblem()}, populate the problem, then use
 * {@link LpProblems#newImmutable(LpProblem)}.
 *
 * @author Olivier Cailloux
 *
 * @param <V>
 *            the type of the variables.
 */
public class LpProblemImmutable<V> extends LpProblemForwarder<V> implements LpProblem<V> {

	/**
	 * Creates a new problem that contains the same data than the given problem and
	 * is immutable.
	 *
	 * @param problem
	 *            not <code>null</code>.
	 */
	public LpProblemImmutable(LpProblem<V> problem) {
		super((problem instanceof LpProblemImmutable<?>) ? problem : new LpProblemImpl<>(problem));
	}

	/**
	 * Throws an exception as this object is immutable.
	 *
	 * @param constraint
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean add(LpConstraint<V> constraint) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
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
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
	 *
	 * @param variable
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean addVariable(Variable variable) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
	 *
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
	 *
	 * @param namer
	 *            anything.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public void setConstraintsNamer(Function<LpConstraint<V>, String> namer) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
	 *
	 * @param name
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean setName(String name) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
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
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
	 *
	 * @param direction
	 *            anything.
	 * @return nothing.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public boolean setObjectiveDirection(LpDirection direction) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
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
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
	 *
	 * @param namer
	 *            anything.
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	@Override
	public void setVariablesNamer(Function<Variable, String> namer) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

	/**
	 * Throws an exception as this object is immutable.
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
		throw new UnsupportedOperationException("This object is immutable.");
	}

}
