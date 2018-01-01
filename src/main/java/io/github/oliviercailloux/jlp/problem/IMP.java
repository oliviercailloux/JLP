package io.github.oliviercailloux.jlp.problem;

import java.util.List;
import java.util.Optional;

import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.Variable;

/**
 * <p>
 * A mathematical program (MP), more precisely a linear program or a mixed
 * integer linear program, consisting of variables (with bounds), constraints,
 * and zero or one objective. Such a mathematical program defines a set of
 * feasible solutions. A feasible solution is an association of a value to each
 * of the variables of the program, satisfying all the constraints in the
 * program. The set of feasible solutions is also called the feasible region.
 * This object may also represent programs that have an empty feasible region,
 * or equivalently, no feasible solutions, or equivalently, that are infeasible.
 * </p>
 * <p>
 * If an MP has an objective, it may have optimal solutions, these are the
 * solutions that have the best value according to its objective function (best
 * meaning highest or smallest depending on the sense of the objective). An MP
 * (as representable by this object) has no objective solutions iff it has no
 * objective function or it has no feasible solution or it is unbounded. For an
 * MP with the {@link Objective#zero()} objective, all feasible solutions are
 * optimal.
 * </p>
 * <p>
 * Two MPs are considered equal when they define the same (as per
 * {@link #equals}) variables, constraints, objective. TODO add name. Two equal
 * MPs, as determined by this interface {@link #equals(Object)} contract, have
 * the same set of feasible solutions, although a non equality between two MPs
 * does <em>not</em> imply that they have different sets of feasible solutions.
 * </p>
 * <p>
 * Implementations of this interface may be writeable, immutable or be a
 * read-only view.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public interface IMP {

	/**
	 * Two problems are considered equal when they define the same variables (as per
	 * {@link #equals}), constraints, objective function.
	 *
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> if this object is the same as the obj argument;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * Retrieves a copy or read-only view of the constraints in this problem.
	 *
	 * @return not <code>null</code>, but may be empty.
	 */
	public List<Constraint> getConstraints();

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
	 * Retrieves the objective function.
	 *
	 * @return not <code>null</code>.
	 */
	public Objective getObjective();

	/**
	 * Retrieves the variable corresponding to the given description, or an absent
	 * optional. Note that the description of the variable generally differs from
	 * its name.
	 *
	 * @param description
	 *            the description of the variable, as given by {@link #toString()}
	 * @return an optional containing a variable, if found, otherwise an empty
	 *         optional.
	 */
	public Optional<Variable> getVariable(String description);

	/**
	 * Retrieves a copy or a read-only view of the variables.
	 *
	 * @return not <code>null</code>, but may be empty.
	 */
	public List<Variable> getVariables();
}
