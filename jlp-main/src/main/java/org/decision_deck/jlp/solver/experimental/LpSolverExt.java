package org.decision_deck.jlp.solver.experimental;

import org.decision_deck.jlp.LpSolverException;
import org.decision_deck.jlp.instanciation.LpSolverType;
import org.decision_deck.jlp.parameters.LpParameters;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.result.LpResultStatus;
import org.decision_deck.jlp.result.LpSolutionAlone;

public interface LpSolverExt<V> {
	/**
	 * Initializes the underlying solver. This method must be called prior to any
	 * other use of this object, except calling close which is always permitted.
	 * This method may be called at most once. If this object has been obtained
	 * through the main solver factory, it is already initialized.
	 * 
	 * @throws LpSolverException
	 *             if an error happens during initialization of the underlying
	 *             solver.
	 */
	public void init() throws LpSolverException;

	/**
	 * @return a writable view
	 */
	public LpParameters getParameters();

	/**
	 * @return a writable view
	 */
	public LpProblem<V> getMP();

	/**
	 * @return the current solution, as known by the underlying solver. May not be
	 *         available any more once problem is modified after a solve.
	 */
	public LpSolutionAlone<V> getSolutionAlone();

	/**
	 * The returned result object is only up to date as long as the parameters and
	 * the problem in this solver instance are unmodified.
	 * 
	 * @return <code>null</code> iff no solve attempt ever succeeded. If a solve
	 *         attempt ended, a result object is returned. The returned result is
	 *         not necessarily up to date.
	 */
	public LpResultTransient<V> getResult();

	/**
	 * If already closed, has no effect.
	 */
	public void close();

	/**
	 * @return not <code>null</code>.
	 */
	public Object getUnderlying();

	public LpResultStatus solve() throws LpSolverException;

	public LpResultStatus solve(LpProblem<V> problem, LpParameters parameters) throws LpSolverException;

	/**
	 * Retrieves the solver brand used as underlying solver. This is set at creation
	 * and does not change.
	 * 
	 * @return not <code>null</code>.
	 */
	public LpSolverType getSolverBrand();
}
