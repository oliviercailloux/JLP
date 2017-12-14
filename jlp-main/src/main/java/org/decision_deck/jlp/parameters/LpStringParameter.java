package org.decision_deck.jlp.parameters;

public enum LpStringParameter {
	/**
	 * If the implementing solver creates some temporary files during its solving
	 * process, they will be created in the given directory or possibly a
	 * subdirectory of the given directory. Default to <code>null</code>, which
	 * means that the solver will try to be clever and choose a temporary directory,
	 * or a directory with enough available space, or anything it thinks is a good
	 * option. This default behavior is solver implementation dependent. An empty
	 * string is not a meaningful value.
	 */
	WORK_DIR
}
