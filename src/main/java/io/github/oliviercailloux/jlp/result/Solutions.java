package io.github.oliviercailloux.jlp.result;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.jlp.problem.IMP;

/**
 * Utilities methods related to a {@link Solution}.
 *
 * @author Olivier Cailloux
 *
 */
public class Solutions {
	/**
	 * Returns a solution containing the same information as the source solution,
	 * and which is immutable.
	 *
	 * @param source
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public SolutionImmutable newImmutable(Solution source) {
		if (source instanceof SolutionImmutable) {
			return (SolutionImmutable) source;
		}
		return new SolutionImmutable(source);
	}

	static public Solution newSolution(IMP problem) {
		checkNotNull(problem);
		return new SolutionImpl(problem);
	}
}
