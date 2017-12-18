package io.github.oliviercailloux.jlp.problem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import com.google.common.base.Function;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.OptimizationDirection;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.ObjectiveFunction;
import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableType;
import io.github.oliviercailloux.jlp.parameters.SolverParameterObject;

/**
 * <p>
 * A mathematical program, more precisely a linear program or a mixed integer
 * program, consisting of variables (with bounds), constraints and zero or one
 * objective function. Such a mathematical program defines a set of feasible
 * solutions. A feasible solution is an association of a value to each of the
 * variables of the program, satisfying all the constraints in the program. The
 * set of feasible solutions is also called the feasible region. This object may
 * also represent programs that have an empty feasible region, or equivalently,
 * no feasible solutions, or equivalently, that are infeasible. Some of the
 * feasible solutions are said to be optimal, these are the solutions that have
 * the best value according to the objective function associated with the
 * program. This class uses the name <em>Problem</em> rather than Program
 * because of the (even bigger) ambiguity of the latter word.
 * </p>
 * <p>
 * A variable bounds may be set to anything, as long as the lower bound is lower
 * than or equal to the upper bound, independently of the variable type. For
 * example, a boolean typed variable may have a lower bound of -3 and upper
 * bound of 0.8. When solving the problem, the variable will be considered as
 * having the most restrictive bounds imposed by either its bounds or its type.
 * In the example, the variable would be constrained to zero.
 * </p>
 * <p>
 * The order of additions of the variables and constraints is retained and
 * reused when reading variables and constraints sets.
 * </p>
 * <p>
 * This interface has been designed for use with immutable numbers: the types
 * {@link Double}, {@link Integer}, {@link BigDecimal}, {@link BigInteger} will
 * pose no problem. Using other types of numbers is unsupported.
 * </p>
 * <p>
 * Some implementations of this interface may be read-only (either because they
 * are immutable or because they are a read-only view), in which case an attempt
 * to change the state of this object will throw
 * {@link UnsupportedOperationException}.
 * </p>
 * <p>
 * Two such problems are considered equal when they define the same variables
 * (as per {@link #equals}), constraints, objective function, and bounds for the
 * variables as determined by the {@link Number#doubleValue()} method. The
 * equality between two problems does not take the names into account: neither
 * the problem, variables or constraints names are considered. Two equal
 * problems, as determined by this class {@link #equals(Object)} method, have
 * the same set of feasible solutions, although a non equality between two
 * problems does <em>not</em> imply that they have different sets of feasible
 * solutions.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public interface IMP {

	/**
	 * Two problems are considered equal when they define the same variables (as per
	 * {@link #equals}), constraints, objective function, and bounds for the
	 * variables as determined by the {@link Number#doubleValue()} method. The
	 * equality between two problems does not take the names into account: neither
	 * the problem, variables or constraints names are considered.
	 *
	 * @see IMP
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * Retrieves a copy or read-only view of the constraints in this problem. The
	 * returned set uses insertion order, thus is iterated in the order the
	 * constraints have been added to this problem.
	 *
	 * @return not <code>null</code>, but may be empty.
	 */
	public Set<Constraint> getConstraints();

	/**
	 * <p>
	 * Retrieves the function used to name the constraints.
	 * </p>
	 * <p>
	 * The default namer uses the constraint id transformed to string as per
	 * {@link Constraint#getId()} and {@link #toString()}, and the empty string if
	 * the constraint has a <code>null</code> id.
	 * </p>
	 *
	 * @return not <code>null</code>.
	 * @see #setConstraintsNamer(Function)
	 * @see MPs.DefaultConstraintsNamer
	 */
	public Function<Constraint, String> getConstraintsNamer();

	/**
	 * Retrieves the dimension of this problem in number of variables and
	 * constraints. The bounds do not count as constraints.
	 *
	 * @return not <code>null</code>.
	 */
	public MPDimension getDimension();

	/**
	 * Retrieves the name of the problem.
	 *
	 * @return never <code>null</code>, empty if not set.
	 */
	public String getName();

	/**
	 * Retrieves a copy or a read-only view of the objective function. It is
	 * possible that the objective function or the direction inside is
	 * <code>null</code>, or that both are <code>null</code>.
	 *
	 * @return not <code>null</code>.
	 */
	public ObjectiveFunction getObjective();

	/**
	 * Retrieves the lower bound of the given variable. The lower bound is less or
	 * equal to the upper bound.
	 *
	 * @param variable
	 *            not <code>null</code>, must be a variable of this problem.
	 * @return a real number, or negative infinity.
	 */
	public Number getVariableLowerBound(Variable variable);

	/**
	 * Retrieves the name of the variable as given by the variable namer function
	 * and transformed to empty string if the variable namer function gave a
	 * <code>null</code> answer.
	 *
	 * @param variable
	 *            not <code>null</code>, must be a variable of this problem.
	 * @return not <code>null</code>, may be an empty string.
	 * @see #setVariablesNamer
	 */
	public String getVariableName(Variable variable);

	/**
	 * Retrieves a copy or a read-only view of the variables. The returned set uses
	 * insertion order, thus is iterated in the order the variables have been added
	 * to this problem.
	 *
	 * @return not <code>null</code>, but may be empty.
	 */
	public Set<Variable> getVariables();

	/**
	 * Retrieves the variable namer function.
	 *
	 * @return not <code>null</code>, by default returns the
	 *         {@link #TO_STRING_NAMER} function.
	 */
	public Function<Variable, String> getVariablesNamer();

	/**
	 * Retrieves the type of a variable.
	 *
	 * @param variable
	 *            not <code>null</code>, must be a variable of this problem.
	 * @return not <code>null</code>.
	 */
	public VariableType getVariableType(Variable variable);

	/**
	 * Retrieves the upper bound of the given variable. The upper bound is greater
	 * than or equal to the lower bound.
	 *
	 * @param variable
	 *            not <code>null</code>, must be a variable of this problem.
	 * @return a real number, or positive infinity.
	 */
	public Number getVariableUpperBound(Variable variable);
}
