package io.github.oliviercailloux.jlp.solve;

@SuppressWarnings("serial")
public class SolverException extends RuntimeException {

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