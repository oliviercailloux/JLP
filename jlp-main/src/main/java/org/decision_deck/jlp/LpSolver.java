/**
 * Copyright © 2010-2012 Olivier Cailloux
 *
 * 	This file is part of JLP.
 *
 * 	JLP is free software: you can redistribute it and/or modify it under the
 * 	terms of the GNU Lesser General Public License version 3 as published by
 * 	the Free Software Foundation.
 *
 * 	JLP is distributed in the hope that it will be useful, but WITHOUT ANY
 * 	WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * 	FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * 	more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public License
 * 	along with JLP. If not, see <http://www.gnu.org/licenses/>.
 */
package org.decision_deck.jlp;

import java.io.IOException;

import org.decision_deck.jlp.parameters.LpParameters;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.problem.LpProblems;
import org.decision_deck.jlp.problem.LpVariableType;
import org.decision_deck.jlp.result.LpResult;
import org.decision_deck.jlp.result.LpResultStatus;
import org.decision_deck.jlp.result.LpSolution;

import com.google.common.io.CharSink;

/**
 * <p>
 * A solver instance, representing a problem (with data, constraints), a set of
 * parameters for solving the problem, and possibly after having been run, a
 * feasible solution. The solver abstracts several commercial and free solvers
 * such as lp_solve, CPLEX, etc., and thus relies on a solver implementation
 * called the underlying solver.
 * </p>
 * <p>
 * The solver may be run several times. It remembers its bound problem and
 * parameters between runs. Elements may be changed in between runs.
 * </p>
 * <p>
 * The solver also offers a possiblity for the user to mess manually with the
 * underlying solver, that can be used to access advanced functionalities not
 * offered by the wrapper (but please consider rather adding the functionality
 * to the wrapper itself if it makes sense, or introduce a bug report to that
 * effect). However, this possibility must be used with caution. After the
 * underlying solver is retrieved by the user, this solver does not update its
 * content any more (to avoid erasing manually introduced changes). Hence,
 * calling {@link #getUnderlyingSolver()} then modifying the bound problem is
 * forbidden. Although this is currently not enforced, the user
 * <em>should not</em> modify this solver state through the parameters or the
 * problem views after a call to {@link #getUnderlyingSolver()}. The caller must
 * not forget to {@link #close()} the solver after use when using it manually,
 * except that the methods {@link #solve()} and {@link #writeProblem} close the
 * underlying solver after the job is done, thus it is not necessary to call
 * close again after these calls. After a call to {@link #solve()} or
 * {@link #writeProblem}, the underlying solver pointer is not valid any more.
 *
 * TODO make {@link #getUnderlyingSolver()} bring this object to an immutable
 * state.
 * </p>
 * <p>
 * When a problem is transferred to the underlying solver by this solver, the
 * variables having a type of {@link LpVariableType#BOOL} are associated with 0
 * and 1 lower and upper bounds except if their associated bounds are even
 * further constrained. E.g. a variable defined as BOOL with bounds of <0.5, 4>
 * have its bounds modified to become <0.5, 1> for the underlying solver.
 * However this object will report the bounds as they are set in the problem,
 * thus with bounds of <0.5, 4>. Also note that this solver does not attempt to
 * transform the input in a manner dependent on the underlying solver. If, for
 * example, a 0-1 binary solver is used, the bounds are <em>not</em>
 * automatically transformed to conform to this setting. (Thus in that situation
 * the solver will throw an exception when attempted to solve.) An undefined
 * bound is equivalent to a minus, or plus, infinity. This is so, once again,
 * independently of the underlying solver. This permits to ensure that the same
 * problem is solved independently of the chosen underlying solver. An automatic
 * transformation is done in cases it does not modify the problem, e.g. if the
 * underlying solver requires integer bounds for integer variables, the bounds
 * will be set accordingly (e.g. transforming a lower bound of 2.5 to a
 * lowerbound of 3) because this has no impact on the set of feasible solution.
 * </p>
 * <p>
 * The type of the objects used for the variables should have their
 * {@link #equals(Object)} method implemented in a meaningful way as this is
 * used when retrieving the values, and their {@link #hashCode()} method should
 * be correctly implemented. The variables should be immutable.
 * </p>
 * <p>
 * Unless otherwise specified in the documentation of specific solvers, the
 * solution returned by {@link #getSolution()} is set with the exact values
 * returned by the solver, i.e. no attempt is made to be clever and change the
 * returned solution values. E.g. if the optimal value is not returned by the
 * underlying solver, it is not set in the solution, although it could possibly
 * be deduced from the variable values and the objective function. Also no
 * attempt is made to round supposedly integer results. This allows the user to
 * manually take into account the numerical imprecision of the solver used.
 * </p>
 * <p>
 * Note about unconstrained variable: it is admitted to include variables that
 * are used in no constraints and no objective function in a problem. Such a
 * variable may have bounds. This solver does not take action: if the underlying
 * solver gives a solution involving these variables (therefore chosen
 * arbitrarily), it will appear as a solution. Forcing, with this solver, no
 * solution for such a variable would be appropriate only if the variable has no
 * bound. If it has, then a solution should be given to avoid exhibiting a
 * different behavior between a bound on a variable and a constraint expressing
 * the same bound.
 * </p>
 * <p>
 * Problems may be solved without an objective function, in which case the solve
 * will simply search for a feasible solution. In this case it never returns the
 * status {@link LpResultStatus#OPTIMAL}. When solve is called, the bound
 * problem must have its objective function defined iff the objective direction
 * is defined. Defining one and not the other one is meaningless and considered
 * as an error.
 * </p>
 * <p>
 * Solving a problem with no constraints (and even no variables) is allowed and
 * results in a {@link LpResultStatus#FEASIBLE} status if no objective function
 * is given.
 * </p>
 * <p>
 * Note that solvers may give a solution that is slightly outside the defined
 * bounds for the variable.
 * </p>
 *
 * @param <V>
 *            the type of the variables.
 *
 * @author Olivier Cailloux
 */
public interface LpSolver<V> {

	/**
	 * In manual close mode, must be used after use in order to release possibly
	 * acquired resources after a call that initialized the underlying solver.
	 * In auto close mode, it is not necessary to call this method. If there is
	 * nothing to close (e.g. because everything is closed already or no
	 * resources have been acquired yet), this method has no effect.
	 * 
	 * @throws LpSolverException
	 *             if a solver-specific exception is thrown.
	 */
	public void close() throws LpSolverException;

	/**
	 * Retrieves a writable view that allow to query and set the values of the
	 * parameters of this solver instance.
	 * 
	 * @return not <code>null</code>, a view that reads and writes through to
	 *         this object.
	 */
	public LpParameters getParameters();

	/**
	 * Retrieves the format this solver will use when asked to write a problem
	 * file with a format of {@link LpFileFormat#SOLVER_PREFERRED}.
	 * 
	 * @return <code>null</code> if the solver preferred format is not in the
	 *         set of enum constants in {@link LpFileFormat}.
	 * @throws LpSolverException
	 *             if the solver does not implement writing problem files.
	 */
	public LpFileFormat getPreferredFormat() throws LpSolverException;

	/**
	 * Retrieves a writable view that allows to read and set the problem bound
	 * to this solver instance.
	 * 
	 * @return not <code>null</code>.
	 */
	public LpProblem<V> getProblem();

	/**
	 * Retrieves the result of solving the bound problem (the last time it was
	 * solved). This method may not be called before a solve has been attempted.
	 * If a solve has been attempted, this method necessarily yields a valid
	 * result object, even if the solve did not find any feasible solution.
	 * 
	 * @return not <code>null</code>.
	 * @see #hasResult()
	 */
	public LpResult<V> getResult();

	/**
	 * Retrieves one solution found to the last problem solved. If the result of
	 * the solve is optimal, the returned solution is an optimal solution.
	 * 
	 * @return <code>null</code> iff no feasible solution to the problem have
	 *         been found (yet). Immutable.
	 */
	public LpSolution<V> getSolution();

	/**
	 * Switches to manual close mode, initializes the underlying solver and
	 * returns it. This is a writeable view to the solver underlying this
	 * object, thus modifications from this object are reflected to the returned
	 * solver, and conversely. Modifying the underlying solver through the
	 * returned object may make this object behave in an unpredictable manner,
	 * as it does not necessarily have a means to detect such changes.
	 * Therefore, the user must choose between two possible uses: either only
	 * use the underlying solver in read mode, not changing its state, or change
	 * its state in which case this object should not be used any more
	 * afterwards. Only the close method of this object should be called after
	 * use of the underlying solver in such situation.
	 * 
	 * @return not <code>null</code>.
	 * @throws LpSolverException
	 *             if a solver-specific exception is thrown.
	 */
	public Object getUnderlyingSolver() throws LpSolverException;

	/**
	 * Indicates if this solver has a result object available, or equivalently,
	 * if a solve has been attempted. The method {@link #getResult()} may be
	 * called iff this method returns <code>true</code>.
	 * 
	 * @return <code>true</code> iff {@link #solve()} has been called.
	 */
	public boolean hasResult();

	/**
	 * <p>
	 * Turns this object into auto close or manual close mode. When this object
	 * auto closes (the default), the underlying solver is initialized when
	 * needed and closed immediately afterwards, before the call that triggered
	 * the initialization ends. This permits to shield the user from having to
	 * think about closing the object after use. However, it renders certain
	 * optimizations impossible, such as not having to rebuild the entire
	 * problem each time a solve is asked. Turning the auto close off switches
	 * to manual close mode, thus makes this object never close the underlying
	 * solver automatically, which implies the user has to call {@link #close()}
	 * after use.
	 * </p>
	 * <p>
	 * Turning the auto close on switches to auto close mode and immediately
	 * close the underlying solver if it was intialized.
	 * </p>
	 * 
	 * TODO implement this.
	 * 
	 * @param autoClose
	 *            <code>true</code> for auto close mode, <code>false</code> to
	 *            enable manual close mode.
	 */
	public void setAutoClose(boolean autoClose);

	/**
	 * <p>
	 * Sets the parameters this solver will use to the given parameters. Any
	 * value already set in this object is lost. Thus if the given parameters
	 * are set to the default value for some parameter p and that parameter has
	 * a value set in this object before the method is called, the value of p
	 * after the method returns is the default value.
	 * </p>
	 * <p>
	 * The given parameters values are copied in this object, no reference is
	 * kept to the given object.
	 * </p>
	 * 
	 * @param parameters
	 *            not <code>null</code>.
	 * @return <code>true</code> iff this object state changed as a result of
	 *         this call. Equivalently, <code>false</code> iff the given
	 *         parameter values are identical to the current values.
	 */
	public boolean setParameters(LpParameters parameters);

	/**
	 * Copies the given problem data (including its name) into the problem bound
	 * to this solver. Any information possibly existing in this object problem
	 * is lost. This solver keeps a reference to the given problem, no defensive
	 * copy is done. Changes to the given problem will change the problem in
	 * this solver, and conversely. To obtain a defensive copy, use
	 * {@link LpProblems#copyTo(LpProblem, LpProblem)} with
	 * {@link #getProblem()}. This should be preferred because the latter method
	 * allows this object to use any implementation it considers most suitable
	 * for optimisation (in particular to track changes to the problem, useful
	 * in manual close mode). Using this method may prevent this object from
	 * applying certain optimisations in manual close mode when the problem is
	 * solved several times.
	 * 
	 * @param problem
	 *            not <code>null</code>.
	 */
	public void setProblem(LpProblem<V> problem);

	/**
	 * Solves the bound optimization problem. If the bound problem has an
	 * objective function set, the optimization direction must be set as well,
	 * and conversely.
	 * 
	 * @return not <code>null</code>.
	 * 
	 * @throws LpSolverException
	 *             if some problem specific to the underlying solver occurs.
	 *             Happens if some feature required for solving the bound
	 *             problem with the bound parameters is missing in this
	 *             implementation, e.g. a parameter value is not legal for this
	 *             solver implementation (see {@link LpParameters}); or if the
	 *             underlying solver throws a solver-dependent exception.
	 *             Runtime exceptions thrown by a solver are not wrapped into a
	 *             {@link LpSolverException}, except for exceptions known to be
	 *             solver-specific.
	 */
	public LpResultStatus solve() throws LpSolverException;

	/**
	 * <p>
	 * Writes the current problem bound to this solver to a file.
	 * </p>
	 * 
	 * @param format
	 *            <code>null</code> is equivalent to
	 *            {@link LpFileFormat#SOLVER_PREFERRED}.
	 * @param destination
	 *            not <code>null</code>.
	 * @throws LpSolverException
	 *             if a solver-specific exception occurs, or the solver does not
	 *             implement writing problem files, or does not support the
	 *             specified format.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void writeProblem(LpFileFormat format, CharSink destination) throws LpSolverException, IOException;

	/**
	 * Writes the current problem bound to this solver to a file.
	 * 
	 * @param format
	 *            not <code>null</code>.
	 * @param file
	 *            not <code>null</code>.
	 * @param addExtension
	 *            <code>true</code> to automatically add an appropriate
	 *            extension to the given path.
	 * @throws LpSolverException
	 *             if a solver-specific exception occurs, or the solver does not
	 *             implement writing problem files, or does not support the
	 *             specified format.
	 */
	@Deprecated
	public void writeProblem(LpFileFormat format, String file, boolean addExtension) throws LpSolverException;

}
