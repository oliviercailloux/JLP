package org.decision_deck.jlp.cplex;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * A simple implementation that writes everything to a logger. This class
 * implements an output stream and thus can be used as a logger for CPLEX.
 * 
 * @author Olivier Cailloux
 * 
 */
public class CplexLogger extends OutputStream {
	/**
	 * The output level that should be used when outputting an information to a
	 * logger.
	 * 
	 * @author Olivier Cailloux
	 * 
	 */
	@SuppressWarnings("javadoc")
	// to avoid missing javadoc warnings.
	public enum OutLevel {
		DEBUG, ERROR, INFO, WARNING;
	}

	private static final Logger s_logger = LoggerFactory.getLogger(CplexLogger.class);

	private final OutLevel m_level;

	private StringWriter m_str;

	/**
	 * Creates a new logger that will write everything received to the logger bound
	 * to this class, with the given level.
	 * 
	 * @param outLevel
	 *            not <code>null</code>.
	 */
	public CplexLogger(OutLevel outLevel) {
		Preconditions.checkNotNull(outLevel);
		m_level = outLevel;
		m_str = new StringWriter();
	}

	@Override
	public void flush() throws IOException {
		if (m_str.toString().trim().length() != 0) {
			final String toWrite;
			// toWrite = m_str.toString().replaceAll("\\n", "");
			toWrite = m_str.toString();
			switch (m_level) {
			case DEBUG:
				s_logger.debug(toWrite);
				break;
			case INFO:
				s_logger.info(toWrite);
				break;
			case WARNING:
				s_logger.warn(toWrite);
				break;
			case ERROR:
				s_logger.error(toWrite);
				break;
			default:
				throw new IllegalStateException("Unknown level.");
			}
		}
		m_str = new StringWriter();
	}

	@Override
	public void write(int b) throws IOException {
		if (b == '\n') {
			flush();
		} else {
			m_str.write(b);
		}
	}

}
