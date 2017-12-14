package io.github.oliviercailloux.jlp.solver.experimental;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.jlp.instanciation.LpSolverType;
import io.github.oliviercailloux.jlp.parameters.LpParameters;
import io.github.oliviercailloux.jlp.problem.LpProblem;
import io.github.oliviercailloux.jlp.result.LpResult;
import io.github.oliviercailloux.jlp.result.LpResultStatus;

/**
 * <p>
 * An instance of this class always has a mathematical program and a set of
 * parameters bound to it. If none is given when the object is created, an empty
 * mathematical program is bound to it, and the parameters all have default
 * values.
 * </p>
 * <p>
 * The object allows to modify the problem and parameters, and then to solve the
 * corresponding problem.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 * @param <V>
 *            the type of the variables.
 */
public class LpSolverOnce<V> {
	private LpParameters m_parameters;

	private LpResult<V> m_results;

	public LpResult<V> getResults() {
		return m_results;
	}

	public LpResultStatus solve() {

	}

	public LpResultStatus solve(LpSolverType solverType) {

	}

	/**
	 * Retrieves a writable view of the problem bound to this solver instance.
	 * 
	 * @return not <code>null</code>.
	 */
	public LpProblem<V> getProblem() {
		return m_problem;
	}

	/**
	 * Sets the mathematical program in this object. All references to the given
	 * program should be released by the user. The mathematical program bound to
	 * this object should be queried and modified only through use of
	 * {@link #getProblem()}.
	 * 
	 * @param problem
	 *            not <code>null</code>.
	 */
	public void setProblem(LpProblem<V> problem) {
		checkNotNull(problem);
		m_problem = problem;
	}

	public void setParameters(LpParameters parameters) {
		m_parameters = parameters;
	}

	private LpProblem<V> m_problem;

	public LpParameters getParameters() {
		return m_parameters;
	}

}
