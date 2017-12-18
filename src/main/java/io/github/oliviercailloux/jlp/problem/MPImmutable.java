package io.github.oliviercailloux.jlp.problem;

import com.google.common.base.Function;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.OptimizationDirection;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableType;

/**
 * An immutable problem. To obtain such a problem, use
 * {@link MPs#newProblem()}, populate the problem, then use
 * {@link MPs#newImmutable(MP)}.
 *
 * @author Olivier Cailloux
 *
 */
public class MPImmutable extends MPForwarder implements MP {

	/**
	 * Creates a new problem that contains the same data than the given problem and
	 * is immutable.
	 *
	 * @param problem
	 *            not <code>null</code>.
	 */
	public MPImmutable(MP problem) {
		super((problem instanceof MPImmutable) ? problem : new MPImpl(problem));
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
	public boolean add(Constraint constraint) {
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
	public boolean add(String id, SumTerms lhs, ComparisonOperator operator, double rhs) {
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
	public void setConstraintsNamer(Function<Constraint, String> namer) {
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
	public boolean setObjective(SumTerms objective, OptimizationDirection direction) {
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
	public boolean setObjectiveDirection(OptimizationDirection direction) {
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
	public boolean setVariableType(Variable variable, VariableType type) {
		throw new UnsupportedOperationException("This object is immutable.");
	}

}
