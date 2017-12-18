package io.github.oliviercailloux.jlp.problem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import com.google.common.base.Function;

import io.github.oliviercailloux.jlp.elements.LpConstraint;
import io.github.oliviercailloux.jlp.elements.LpDirection;
import io.github.oliviercailloux.jlp.elements.LpLinear;
import io.github.oliviercailloux.jlp.elements.LpObjective;
import io.github.oliviercailloux.jlp.elements.LpOperator;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.parameters.LpObjectParameter;

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
 * @param <V>
 *            the type of the variables. See the package description.
 *
 * @author Olivier Cailloux
 *
 */
public interface LpProblem<V> {

	public Function<Variable, String> TO_STRING_NAMER = Variable::toString;

	/**
	 * Adds a constraint, or does nothing if the given constraint is already in the
	 * problem. The variables used in the objective must have been added to this
	 * problem already.
	 *
	 * @param constraint
	 *            the constraint to be added. Not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object.
	 *         Equivalently, returns <code>false</code> iff the given constraint
	 *         already was in the problem.
	 */
	public boolean add(LpConstraint<V> constraint);

	/**
	 * Adds a constraint, or does nothing if the given constraint is already in the
	 * problem. The variables used in the objective must have been added to this
	 * problem already.
	 *
	 * @param id
	 *            the id of the constraint. If <code>null</code>, a no-id constraint
	 *            is used.
	 * @param lhs
	 *            the left-hand-side linear expression. Not <code>null</code>.
	 * @param operator
	 *            the operator. Not <code>null</code>.
	 * @param rhs
	 *            the right-hand-side number. A real value (not NaN or infinite).
	 * @return <code>true</code> iff the call modified the state of this object.
	 *         Equivalently, returns <code>false</code> iff the given constraint
	 *         already was in the problem.
	 */
	public boolean add(String id, LpLinear lhs, LpOperator operator, double rhs);

	/**
	 * Adds the variable to this problem if it is not already in, with a default
	 * type of REAL, no name, a lower bound equal to zero and a positive infinite
	 * upper bound.
	 *
	 * @param variable
	 *            not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object.
	 */
	public boolean addVariable(Variable variable);

	/**
	 * Removes all the variables and constraints, objective function, name, namers
	 * set in this problem. As a result of this call, this problem has the same
	 * visible state as a newly created, empty problem.
	 */
	public void clear();

	/**
	 * Two problems are considered equal when they define the same variables (as per
	 * {@link #equals}), constraints, objective function, and bounds for the
	 * variables as determined by the {@link Number#doubleValue()} method. The
	 * equality between two problems does not take the names into account: neither
	 * the problem, variables or constraints names are considered.
	 *
	 * @see LpProblem
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
	public Set<LpConstraint<V>> getConstraints();

	/**
	 * <p>
	 * Retrieves the function used to name the constraints.
	 * </p>
	 * <p>
	 * The default namer uses the constraint id transformed to string as per
	 * {@link LpConstraint#getId()} and {@link #toString()}, and the empty string if
	 * the constraint has a <code>null</code> id.
	 * </p>
	 *
	 * @return not <code>null</code>.
	 * @see #setConstraintsNamer(Function)
	 * @see LpProblems.DefaultConstraintsNamer
	 */
	public Function<LpConstraint<V>, String> getConstraintsNamer();

	/**
	 * Retrieves the dimension of this problem in number of variables and
	 * constraints. The bounds do not count as constraints.
	 *
	 * @return not <code>null</code>.
	 */
	public LpDimension getDimension();

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
	public LpObjective getObjective();

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
	public LpVariableType getVariableType(Variable variable);

	/**
	 * Retrieves the upper bound of the given variable. The upper bound is greater
	 * than or equal to the lower bound.
	 *
	 * @param variable
	 *            not <code>null</code>, must be a variable of this problem.
	 * @return a real number, or positive infinity.
	 */
	public Number getVariableUpperBound(Variable variable);

	/**
	 * <p>
	 * Sets the namer function that is used to associate names to constraints. If
	 * the given namer is <code>null</code>, the namer function is set back to the
	 * default function. The function is never given a <code>null</code> constraint;
	 * however the constraint id may be <code>null</code>.
	 * </p>
	 *
	 * @param namer
	 *            <code>null</code> to reset default behavior.
	 * @see #getConstraintsNamer()
	 * @see LpObjectParameter#NAMER_CONSTRAINTS
	 */
	public void setConstraintsNamer(Function<LpConstraint<V>, String> namer);

	/**
	 * Sets or removes the name of this problem.
	 *
	 * @param name
	 *            <code>null</code> or empty string for no name. A <code>null</code>
	 *            string is converted to an empty string.
	 * @return <code>true</code> iff the call modified the state of this object.
	 *         Equivalently, returns <code>false</code> iff the given name was
	 *         different than this problem name.
	 *
	 */
	public boolean setName(String name);

	/**
	 * Sets or removes the objective bound to this problem. The variables used in
	 * the objective function must have been added to this problem already. Setting
	 * both parameters to <code>null</code> is legal.
	 *
	 * @param objectiveFunction
	 *            <code>null</code> to remove a possibly set objective function.
	 * @param direction
	 *            <code>null</code> for not set (removes a possibly set optimization
	 *            direction).
	 * @return <code>true</code> iff the call modified the state of this object.
	 */
	public boolean setObjective(LpLinear objectiveFunction, LpDirection direction);

	/**
	 * Sets or removes the optimization direction. The objective function itself
	 * (without direction), if set, is unchanged: only the direction possibly
	 * changes.
	 *
	 * @param dir
	 *            <code>null</code> to remove, if set, the optimization direction
	 *            information.
	 * @return <code>true</code> iff the call modified the state of this object.
	 */
	public boolean setObjectiveDirection(LpDirection dir);

	/**
	 * <p>
	 * Sets the lower and upper bounds of a variable. Adds the variable to this
	 * problem if it is not already in, with a REAL type as default type. The lower
	 * bound must be less than or equal to the upper bound.
	 * </p>
	 * <p>
	 * The variable type does <em>not</em> impose constraints on the acceptable
	 * bounds that may be associated to it.
	 * </p>
	 *
	 * @param variable
	 *            not <code>null</code>.
	 * @param lowerBound
	 *            <code>null</code>, or minus infinity, for a lower bound equal to
	 *            minus infinity, may not be positive infinity.
	 * @param upperBound
	 *            <code>null</code>, or positive infinity, for an infinite upper
	 *            bound, may not be negative infinity.
	 * @return <code>true</code> iff the call modified the state of this object, or
	 *         equivalently, iff the variable did not exist previously in this
	 *         object or at least one of the given bounds is different than the
	 *         previous bound. Two bounds are considered equal iff they are both
	 *         infinite with the same sign or they would be considered equal
	 *         according to the given bound {@link #equals(Object)} method. For
	 *         example, when given as new bound a {@link BigDecimal} with the same
	 *         value as the current bound but a different scale, this method will
	 *         change the bound and return <code>true</code>, because such numbers
	 *         are not considered equal as per {@link BigDecimal#equals}.
	 */
	public boolean setVariableBounds(Variable variable, Number lowerBound, Number upperBound);

	/**
	 * <p>
	 * Sets the namer function that is used to associate names to variables.
	 * </p>
	 *
	 * @param namer
	 *            <code>null</code> to restore the default namer.
	 * @see #getVariableName
	 * @see #TO_STRING_NAMER
	 * @see LpObjectParameter#NAMER_VARIABLES
	 */
	public void setVariablesNamer(Function<Variable, String> namer);

	/**
	 * Sets or removes the type of a variable. Adds the variable to this problem if
	 * it is not already in, with a default lower bound equal to zero.
	 *
	 * @param variable
	 *            not <code>null</code>.
	 * @param type
	 *            not <code>null</code>.
	 * @return <code>true</code> iff the call modified the state of this object.
	 */
	public boolean setVariableType(Variable variable, LpVariableType type);
}
