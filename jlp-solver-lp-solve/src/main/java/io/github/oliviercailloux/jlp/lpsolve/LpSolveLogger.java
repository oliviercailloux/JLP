package io.github.oliviercailloux.jlp.lpsolve;

import lpsolve.LogListener;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper around the LP solve logger that can be used to redirect logging
 * information to the SLF logger embedded in this class.
 * 
 * @author Olivier Cailloux
 * 
 */
public class LpSolveLogger implements LogListener {
	private static final Logger s_logger = LoggerFactory.getLogger(LpSolveLogger.class);

	@Override
	public void logfunc(LpSolve problem, Object userhandle, String buf) throws LpSolveException {
		final String logStr = buf.replace('\n', ' ').trim();
		if (logStr.length() > 0) {
			s_logger.info(logStr);
		}
	}
}