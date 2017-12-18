package io.github.oliviercailloux.jlp.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringWriter;

import com.google.common.io.OutputSupplier;

/**
 * A supplier writing to the provided string writer.
 *
 * @author Olivier Cailloux
 *
 */
public class StringWriterSupplier implements OutputSupplier<StringWriter> {
	private final StringWriter stringWriter;

	/**
	 * Creates a new supplier.
	 *
	 * @param stringWriter
	 *            not <code>null</code>.
	 */
	public StringWriterSupplier(StringWriter stringWriter) {
		checkNotNull(stringWriter);
		this.stringWriter = stringWriter;
	}

	@Override
	public StringWriter getOutput() throws IOException {
		return stringWriter;
	}
}