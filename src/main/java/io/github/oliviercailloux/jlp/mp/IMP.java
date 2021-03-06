package io.github.oliviercailloux.jlp.mp;

import java.util.List;
import java.util.Set;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * <p>
 * A mathematical program (MP), more precisely a linear program or a mixed
 * integer linear program, consisting of variables (with bounds), constraints,
 * and an objective. Such a mathematical program defines a set of feasible
 * solutions. A candidate solution is an association of a value to each of the
 * variables of the program, the value being within the bounded domain of the
 * variable (see {@link Variable}). A feasible solution is a candidate solution
 * that satisfies all the constraints in the program. This object may represent
 * programs that are infeasible, meaning that they have no feasible solutions.
 * </p>
 * <p>
 * Such an MP may have an objective function, in which case the following
 * definitions apply. A feasible solution is optimal iff its objective function
 * value is best (best meaning a value greater than or equal to the objective
 * function value of any other feasible solution, for a maximization sense, or a
 * value less than or equal to the objective function value of any other
 * feasible solution, for a minimization sense). An MP (as representable by this
 * object) has no optimal solutions iff it has no feasible solution or is
 * unbounded.
 * </p>
 * <p>
 * An MP may have the {@link Objective#ZERO ZERO} objective, which indicates
 * that it does not care about optimizing anything, only about finding a
 * feasible solution. For an MP with the <code>ZERO</code> objective, all
 * feasible solutions are optimal.
 * </p>
 * <p>
 * Two MPs are considered equal when they have the same (as per {@link #equals})
 * name, variables, constraints, and objective. Two equal MPs, as determined by
 * this interface {@link #equals(Object)} contract, have the same set of
 * feasible solutions, although a non equality between two MPs does <em>not</em>
 * imply that they have different sets of feasible solutions.
 * </p>
 * <p>
 * Implementations of this interface may be writeable, immutable or be a
 * read-only view.
 * </p>
 * <p>
 * As the variables in this MP are not duplicated, the return type of
 * {@link #getVariables()} could have chosen to be a {@link Set} rather than a
 * {@link List}. However, the relative position of a variable in the list,
 * compared to its peers, will usually be meaningful, as variables are probably
 * to be grouped in a way that depends on their meaning (variables about the
 * structure come first, …). Hence, explicitly indexing the variables seems
 * adequate.
 * </p>
 * <p>
 * The list of constraints is a real list in the sense that it allows
 * duplicates. This permits to really satisfy the constract of {@link List}, and
 * makes it easily sortable, for example. (The choice is different for the list
 * of variables because it can’t fulfill entirely the contract of
 * <code>List</code> anyway, as a variable can’t be removed once it is used.)
 * </p>
 *
 * @author Olivier Cailloux
 * @see Variable
 *
 */
public interface IMP {

	/**
	 * Returns the name of this MP.
	 *
	 * @return not <code>null</code>, may be empty.
	 */
	public String getName();

	/**
	 * Returns <code>true</code> iff the given description corresponds to a variable
	 * in this MP.
	 *
	 * @param description not <code>null</code>, not empty.
	 * @return <code>true</code> iff some variable in this MP has a corresponding
	 *         description.
	 */
	public boolean containsVariable(String description);

	/**
	 * Returns the variable corresponding to the given description.
	 *
	 * @param description the description of the variable, as given by
	 *                    {@link Variable#getDescription()}; must correspond to a
	 *                    variable in this MP.
	 * @return the variable, not <code>null</code>.
	 * @see Variable#getDefaultDescription(String, Iterable)
	 * @see #containsVariable(String)
	 */
	public Variable getVariable(String description);

	/**
	 * <p>
	 * Returns the variables in this MP.
	 * </p>
	 * <p>
	 * This list contains all variables used in any constraint, and any variables
	 * used in the objective function (and possibly more).
	 * </p>
	 *
	 * @return not <code>null</code>, may be empty, contains no duplicates.
	 */
	public List<Variable> getVariables();

	/**
	 * Returns the constraints in this MP.
	 *
	 * @return not <code>null</code>, may be empty, may contain duplicates.
	 */
	public List<Constraint> getConstraints();

	/**
	 * Returns the objective of this MP, which may be the {@link Objective#ZERO
	 * ZERO} objective to indicate that no objective function is associated to this
	 * MP.
	 *
	 * @return not <code>null</code>.
	 */
	public Objective getObjective();

	/**
	 * Returns the dimension of this MP as a number of variables and constraints.
	 * The bounds constraining the variables do not count as constraints in this
	 * count.
	 *
	 * @return not <code>null</code>.
	 */
	public MPDimension getDimension();

	/**
	 * Two MPs are equal when they have equal name, variables, constraints, and
	 * objective.
	 *
	 * @param o2 the reference object with which to compare.
	 * @return <code>true</code> iff this object is the same as the obj argument.
	 */
	@Override
	public boolean equals(Object o2);

	/**
	 * Returns a (reasonably) short description of this MP.
	 */
	@Override
	String toString();
}
