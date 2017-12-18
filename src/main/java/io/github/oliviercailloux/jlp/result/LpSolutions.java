package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.jlp.problem.LpProblem;

/**
 * Utilities methods related to a {@link LpSolution}.
 *
 * @author Olivier Cailloux
 *
 */
public class LpSolutions {
	/**
	 * Returns a solution containing the same information as the source solution,
	 * and which is immutable.
	 *
	 * @param source
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public LpSolutionImmutable newImmutable(LpSolution source) {
		if (source instanceof LpSolutionImmutable) {
			return (LpSolutionImmutable) source;
		}
		return new LpSolutionImmutable(source);
	}

	static public LpSolution newSolution(LpProblem problem) {
		checkNotNull(problem);
		return new LpSolutionImpl(problem);
	}
}
