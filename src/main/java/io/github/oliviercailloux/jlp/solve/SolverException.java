package io.github.oliviercailloux.jlp.solve;

public class SolverException extends RuntimeException {

	/**
	 * TODO think about this.
	 */
	private static final long serialVersionUID = 1L;

	public SolverException() {
		super();
	}

	public SolverException(String message) {
		super(message);
	}

	public SolverException(String message, Throwable cause) {
		super(message, cause);
	}

	public SolverException(Throwable cause) {
		super(cause);
	}

}