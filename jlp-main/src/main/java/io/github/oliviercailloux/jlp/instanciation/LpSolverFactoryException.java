package io.github.oliviercailloux.jlp.instanciation;

import io.github.oliviercailloux.jlp.LpSolverException;

public class LpSolverFactoryException extends LpSolverException {

	private static final long serialVersionUID = 1L;

	public LpSolverFactoryException() {
		super();
	}

	public LpSolverFactoryException(String message) {
		super(message);
	}

	public LpSolverFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public LpSolverFactoryException(Throwable cause) {
		super(cause);
	}

}