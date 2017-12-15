package io.github.oliviercailloux.jlp;

public class LpSolverException extends Exception {

	private static final long serialVersionUID = 1L;

	public LpSolverException() {
		super();
	}

	public LpSolverException(String message) {
		super(message);
	}

	public LpSolverException(String message, Throwable cause) {
		super(message, cause);
	}

	public LpSolverException(Throwable cause) {
		super(cause);
	}

}