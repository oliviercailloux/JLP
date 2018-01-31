package io.github.oliviercailloux.jlp.mp;

import java.util.List;
import java.util.Optional;

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
 * A feasible solution is optimal iff it has the best value according to its
 * objective function (best meaning an objective function value higher than or
 * equal to the objective function value of any other feasible solution, for a
 * maximization sense, or a value lower than or equal to, for a minimization
 * sense). An MP (as representable by this object) has no optimal solutions iff
 * it has no feasible solution or it is unbounded.
 * </p>
 * <p>
 * An MP may have the {@link Objective#ZERO ZERO} objective, which indicates
 * that it does not care about optimizing anything, only about finding a
 * feasible solution. For an MP with the <code>ZERO</code> objective, all
 * feasible solutions are optimal.
 * </p>
 * <p>
 * Two MPs are considered equal when they define the same (as per
 * {@link #equals}) name, variables, constraints, and objective. Two equal MPs,
 * as determined by this interface {@link #equals(Object)} contract, have the
 * same set of feasible solutions, although a non equality between two MPs does
 * <em>not</em> imply that they have different sets of feasible solutions.
 * </p>
 * <p>
 * Implementations of this interface may be writeable, immutable or be a
 * read-only view.
 * </p>
 *
 * @author Olivier Cailloux
 * @see Variable
 *
 */
public interface IMP {

	/**
	 * Two MPs are equal when they have equal name, variables, constraints, and
	 * objective.
	 *
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> iff this object is the same as the obj argument.
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * Returns the constraints in this MP.
	 *
	 * @return not <code>null</code>, may be empty, contains no duplicates.
	 */
	public List<Constraint> getConstraints();

	/**
	 * Returns the dimension of this MP as a number of variables and constraints.
	 * The variable bounds do not count as constraints.
	 *
	 * @return not <code>null</code>.
	 */
	public MPDimension getDimension();

	/**
	 * Returns the name of this MP.
	 *
	 * @return not <code>null</code>, may be empty.
	 */
	public String getName();

	/**
	 * Returns the objective of this MP, which may be the {@link Objective#ZERO
	 * ZERO} objective to indicate that no objective function is associated to this
	 * MP.
	 *
	 * @return not <code>null</code>.
	 */
	public Objective getObjective();

	/**
	 * Returns the variable corresponding to the given description, or an empty
	 * optional if no variable have the given description in this MP. Note that the
	 * description of a variable generally differs from its name.
	 *
	 * @param description
	 *            the description of the variable, as given by
	 *            {@link Variable#getDescription()}.
	 * @return an optional containing a variable, if found, otherwise an empty
	 *         optional.
	 * @see Variable#getDefaultDescription(String, Iterable) .
	 */
	public Optional<Variable> getVariable(String description);

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
	 * Returns a (reasonably) short description of this MP.
	 */
	@Override
	String toString();
}
