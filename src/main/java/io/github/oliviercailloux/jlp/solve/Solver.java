package io.github.oliviercailloux.jlp.solve;

import io.github.oliviercailloux.jlp.mp.IMP;
import io.github.oliviercailloux.jlp.parameters.Configuration;
import io.github.oliviercailloux.jlp.result.Result;

public interface Solver {
	/**
	 * Sets the configuration this instance will use.
	 *
	 * @param configuration not <code>null</code>.
	 */
	public void setConfiguration(Configuration configuration);

	public Result solve(IMP mp);
}
