package org.decision_deck.jlp;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.decision_deck.jlp.parameters.LpDoubleParameter;
import org.decision_deck.jlp.parameters.LpIntParameter;
import org.decision_deck.jlp.parameters.LpObjectParameter;
import org.decision_deck.jlp.parameters.LpParameters;
import org.decision_deck.jlp.parameters.LpParametersImpl;
import org.decision_deck.jlp.parameters.LpParametersUtils;
import org.decision_deck.jlp.parameters.LpStringParameter;
import org.decision_deck.jlp.parameters.LpTimingType;
import org.decision_deck.jlp.problem.LpProblem;
import org.decision_deck.jlp.problem.LpProblems;
import org.decision_deck.jlp.result.LpResult;
import org.decision_deck.jlp.result.LpResultImpl;
import org.decision_deck.jlp.result.LpResultStatus;
import org.decision_deck.jlp.result.LpSolution;
import org.decision_deck.jlp.result.LpSolutions;
import org.decision_deck.jlp.result.LpSolverDuration;
import org.decision_deck.jlp.utils.TimingHelper;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * <p>
 * A helper class that can be used as a basis to implement a solver.
 * </p>
 * <p>
 * The following receipt for implementing a new solver type may be followed.
 * <ul>
 * <li>Implement lazy init: init the underlying solver store it in a local
 * field. Init the parameters. Declare the variables, add the constraints.
 * <li>Implement {@link #getUnderlyingSolver()}: lazy init the underlying solver
 * and return it.</li>
 * <li>Implement {@link #solve()}: lazy init the underlying solver (if
 * necessary), then solve using the {@link TimingHelper}. Store the result
 * status. If feasible, build a feasible solution. Set the solution and set the
 * last duration. Call {@link #close()}.</li>
 * </ul>
 * This permits an external user to get the underlying solver, modify it a bit
 * manually, then call solve.
 * </p>
 * 
 * @param <V>
 *            the type of the variables.
 * 
 * @author Olivier Cailloux
 * 
 */
public abstract class AbstractLpSolver<V> implements LpSolver<V> {
	protected LpSolverDuration m_lastDuration;

	private LpResultStatus m_lastResultStatus;

	private LpSolution<V> m_lastSolution;

	private final LpParameters m_parameters;

	private LpProblem<V> m_problem;

	final private TimingHelper m_timingHelper;

	public AbstractLpSolver() {
		m_problem = LpProblems.newProblem();
		m_parameters = new LpParametersImpl();
		m_timingHelper = new TimingHelper();
		m_lastSolution = null;
		m_lastDuration = null;
		m_lastResultStatus = null;
	}

	/**
	 * Retrieves the value associated with the given parameter. If the value has not
	 * been set, returns the default value for that parameter.
	 * 
	 * @param parameter
	 *            not <code>null</code>.
	 * @return a meaningful value for that parameter, possibly <code>null</code> as
	 *         this is a meaningful value for some parameters.
	 */
	public Double getParameter(LpDoubleParameter parameter) {
		return m_parameters.getValue(parameter);
	}

	/**
	 * Retrieves the value associated with the given parameter. If the value has not
	 * been set, returns the default value for that parameter.
	 * 
	 * @param parameter
	 *            not <code>null</code>.
	 * @return a meaningful value for that parameter, possibly <code>null</code> as
	 *         this is a meaningful value for some parameters.
	 */
	public Integer getParameter(LpIntParameter parameter) {
		return m_parameters.getValue(parameter);
	}

	/**
	 * Retrieves the value associated with the given parameter. If the value has not
	 * been set, returns the default value for that parameter.
	 * 
	 * @param parameter
	 *            not <code>null</code>.
	 * @return a meaningful value for that parameter, possibly <code>null</code> as
	 *         this is a meaningful value for some parameters.
	 */
	public String getParameter(LpStringParameter parameter) {
		return m_parameters.getValue(parameter);
	}

	@Override
	public LpParameters getParameters() {
		return m_parameters;
	}

	/**
	 * Retrieves the preferred timing type according to the parameters values set in
	 * this object. If both the max wall time and max cpu time parameters are set,
	 * an exception is thrown. If the max cpu time parameter is set but cpu timing
	 * is not supported by the Java virtual machine, an exception is thrown.
	 * Otherwise, this method returns the timing type for which a time limit has
	 * been set as a parameter, or if none has been set, returns cpu timing if it is
	 * supported and wall timing otherwise.
	 * 
	 * @return not <code>null</code>.
	 * @throws LpSolverException
	 *             if both cpu and wall time limit parameters have a value; or if
	 *             cpu time limit parameter is set but cpu timing is not supported
	 *             by the Java virtual machine.
	 */
	public LpTimingType getPreferredTimingType() throws LpSolverException {
		final boolean hasMaxWall = getParameter(LpDoubleParameter.MAX_WALL_SECONDS) != null;
		final boolean hasMaxCpu = getParameter(LpDoubleParameter.MAX_CPU_SECONDS) != null;
		if (hasMaxCpu && hasMaxWall) {
			throw new LpSolverException("Can't have both CPU time limit and Wall time limit.");
		}
		final LpTimingType timingType;
		if (hasMaxWall) {
			timingType = LpTimingType.WALL_TIMING;
		} else if (hasMaxCpu) {
			if (!m_timingHelper.isCpuTimingSupported()) {
				throw new LpSolverException("Cpu timing not supported but max cpu time is set.");
			}
			timingType = LpTimingType.CPU_TIMING;
		} else {
			if (m_timingHelper.isCpuTimingSupported()) {
				timingType = LpTimingType.CPU_TIMING;
			} else {
				timingType = LpTimingType.WALL_TIMING;
			}
		}
		return timingType;
	}

	@Override
	public LpProblem<V> getProblem() {
		return m_problem;
	}

	@Override
	public LpSolution<V> getSolution() {
		return m_lastSolution;
	}

	public Double getTimeLimit(LpTimingType timingType) {
		switch (timingType) {
		case WALL_TIMING:
			return m_parameters.getValue(LpDoubleParameter.MAX_WALL_SECONDS);
		case CPU_TIMING:
			return m_parameters.getValue(LpDoubleParameter.MAX_CPU_SECONDS);
		default:
			throw new IllegalStateException("Unknown timing type.");
		}
	}

	/**
	 * Retrieves the name that should be used for a constraint according to the
	 * specified export format, or the default constraint name if the export format
	 * is <code>null</code>. This method uses the appropriate naming function if it
	 * is set.
	 * 
	 * @param constraint
	 *            not <code>null</code>.
	 * @param format
	 *            may be <code>null</code>.
	 * @return not <code>null</code>, empty string for no name.
	 */
	public String getConstraintName(LpConstraint<V> constraint, LpFileFormat format) {
		checkNotNull(constraint);

		if (format == null) {
			return getConstraintName(constraint);
		}

		final Map<?, ?> namers = (Map<?, ?>) getParameter(LpObjectParameter.NAMER_CONSTRAINTS_BY_FORMAT);
		if (namers == null || !namers.containsKey(format)) {
			return getConstraintName(constraint);
		}
		final Object namerObj = namers.get(format);
		if (!(namerObj instanceof Function)) {
			throw new ClassCastException("Illegal constraint namer '" + namerObj + "', namers should be functions.");
		}
		final Function<?, ?> namer = (Function<?, ?>) namerObj;

		return getConstraintName(constraint, namer);
	}

	private String getConstraintName(LpConstraint<V> constraint, Function<?, ?> namer) {
		@SuppressWarnings("unchecked")
		final Function<LpConstraint<V>, ?> namerTyped = (Function<LpConstraint<V>, ?>) namer;
		final Object named = namerTyped.apply(constraint);
		if (named == null) {
			return "";
		}
		if (!(named instanceof String)) {
			throw new ClassCastException(
					"Illegal constraint name '" + named + "', namer should only return strings or nulls.");
		}
		final String name = (String) named;
		return name;
	}

	@Override
	public boolean setParameters(LpParameters parameters) {
		Preconditions.checkNotNull(parameters);
		if (parameters.equals(m_parameters)) {
			return false;
		}
		LpParametersUtils.removeAllValues(m_parameters);
		LpParametersUtils.setAllValues(m_parameters, parameters);
		return true;
	}

	@Override
	public void setProblem(LpProblem<V> problem) {
		checkNotNull(problem);
		m_problem = problem;
	}

	/**
	 * Sets the solution as a defensive copy of the given solution.
	 * 
	 * @param solution
	 *            not <code>null</code>.
	 */
	protected void setSolution(LpSolution<V> solution) {
		Preconditions.checkNotNull(solution);
		m_lastSolution = LpSolutions.newImmutable(solution);
	}

	@Override
	public LpResultStatus solve() throws LpSolverException {
		Preconditions.checkState(getProblem().getObjective().isEmpty() || getProblem().getObjective().isComplete(),
				"Problem must have an objective function iff it has an objective direction.");
		m_lastResultStatus = solveUnderlying();
		return m_lastResultStatus;
	}

	abstract protected LpResultStatus solveUnderlying() throws LpSolverException;

	/**
	 * Retrieves the value associated with the given parameter. If the value has not
	 * been set, returns the default value for that parameter.
	 * 
	 * @param parameter
	 *            not <code>null</code>.
	 * @return a meaningful value for that parameter, possibly <code>null</code> as
	 *         this is a meaningful value for some parameters.
	 */
	public Object getParameter(LpObjectParameter parameter) {
		return m_parameters.getValue(parameter);
	}

	/**
	 * Retrieves the name that should be used for a variable. This method uses the
	 * naming function if it is set.
	 * 
	 * @param variable
	 *            not <code>null</code>.
	 * @return not <code>null</code>, empty string for no name.
	 */
	public String getVariableName(V variable) {
		Preconditions.checkNotNull(variable);

		final Function<?, ?> namer = (Function<?, ?>) getParameter(LpObjectParameter.NAMER_VARIABLES);
		if (namer == null) {
			return getProblem().getVariableName(variable);
		}

		return getVariableName(variable, namer);
	}

	/**
	 * Retrieves the name of the constraint, using the appropriate namer function if
	 * it is set.
	 * 
	 * @param constraint
	 *            not <code>null</code>.
	 * 
	 * @return never <code>null</code>, empty if no id is set.
	 */
	public String getConstraintName(LpConstraint<V> constraint) {
		Preconditions.checkNotNull(constraint);

		final Function<?, ?> namer = (Function<?, ?>) getParameter(LpObjectParameter.NAMER_CONSTRAINTS);
		final Function<?, ?> realNamer = namer == null ? getProblem().getConstraintsNamer() : namer;

		return getConstraintName(constraint, realNamer);
	}

	private String getVariableName(V variable, Function<?, ?> namer) {
		@SuppressWarnings("unchecked")
		final Function<V, ?> namerTyped = (Function<V, ?>) namer;
		final Object named = namerTyped.apply(variable);
		if (named == null) {
			return "";
		}
		if (!(named instanceof String)) {
			throw new ClassCastException(
					"Illegal variable name '" + named + "', namer should only return strings or nulls.");
		}
		final String name = (String) named;
		return name;
	}

	/**
	 * Retrieves the name that should be used for a variable according to the
	 * specified export format, or the default name if the given format is
	 * <code>null</code>. This method uses the appropriate naming function if it is
	 * set.
	 * 
	 * @param variable
	 *            not <code>null</code>.
	 * @param format
	 *            may be <code>null</code>.
	 * @return not <code>null</code>, empty string for no name.
	 */
	public String getVariableName(V variable, LpFileFormat format) {
		checkNotNull(variable);

		if (format == null) {
			return getVariableName(variable);
		}

		final Map<?, ?> namers = (Map<?, ?>) getParameter(LpObjectParameter.NAMER_VARIABLES_BY_FORMAT);
		if (namers == null || !namers.containsKey(format)) {
			return getVariableName(variable);
		}
		final Object namerObj = namers.get(format);
		if (!(namerObj instanceof Function)) {
			throw new ClassCastException("Illegal variable namer '" + namerObj + "', namers should be functions.");
		}
		final Function<?, ?> namer = (Function<?, ?>) namerObj;

		return getVariableName(variable, namer);
	}

	@Override
	public void setAutoClose(boolean autoClose) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LpResult<V> getResult() {
		checkState(m_lastResultStatus != null);
		checkState(m_lastDuration != null);
		final LpResultImpl<V> result;
		if (m_lastResultStatus.foundFeasible()) {
			checkState(m_lastSolution != null, "Last result indicates solution found but no solution was set.");
			result = LpResultImpl.withSolution(m_lastResultStatus, m_lastDuration, m_parameters, m_lastSolution);
		} else {
			result = LpResultImpl.noSolution(m_lastResultStatus, m_lastDuration, m_parameters);
		}
		return result;
	}

	@Override
	public boolean hasResult() {
		return m_lastResultStatus != null;
	}

}
