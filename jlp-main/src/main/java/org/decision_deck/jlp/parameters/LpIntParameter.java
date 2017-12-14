package org.decision_deck.jlp.parameters;

public enum LpIntParameter {
	/**
	 * 1 to force the solver to have a deterministic behavior: if run twice with the
	 * same input and parameters, will find twice the same solution. The default
	 * value of 0 does not force the solver, which can enhance performances
	 * especially in multi thread setting.
	 */
	DETERMINISTIC,

	/**
	 * <code>null</code> for no max (default), otherwise must be a strictly positive
	 * number. TODO document what happens if the number is higher than the number of
	 * physical threads.
	 */
	MAX_THREADS
}
