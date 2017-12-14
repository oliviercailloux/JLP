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
	static public <V> LpSolution<V> newSolution(LpProblem<V> problem) {
		checkNotNull(problem);
		return new LpSolutionImpl<V>(problem);
	}

	/**
	 * Returns a solution containing the same information as the source solution,
	 * and which is immutable.
	 * 
	 * @param <V>
	 *            the type of variables.
	 * @param source
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <V> LpSolutionImmutable<V> newImmutable(LpSolution<V> source) {
		if (source instanceof LpSolutionImmutable<?>) {
			return (LpSolutionImmutable<V>) source;
		}
		return new LpSolutionImmutable<V>(source);
	}
}
