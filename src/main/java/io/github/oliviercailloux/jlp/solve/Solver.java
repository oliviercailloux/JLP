package io.github.oliviercailloux.jlp.solve;

import io.github.oliviercailloux.jlp.mp.IMP;
import io.github.oliviercailloux.jlp.parameters.Configuration;
import io.github.oliviercailloux.jlp.result.Result;

public interface Solver {
	public Result solve(IMP mp, Configuration configuration);

	public Result solve(IMP mp);
}
