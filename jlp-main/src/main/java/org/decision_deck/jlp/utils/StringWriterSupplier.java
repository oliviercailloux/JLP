/**
 * Copyright © 2010-2012 Olivier Cailloux
 *
 * 	This file is part of JLP.
 *
 * 	JLP is free software: you can redistribute it and/or modify it under the
 * 	terms of the GNU Lesser General Public License version 3 as published by
 * 	the Free Software Foundation.
 *
 * 	JLP is distributed in the hope that it will be useful, but WITHOUT ANY
 * 	WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * 	FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * 	more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public License
 * 	along with JLP. If not, see <http://www.gnu.org/licenses/>.
 */
package org.decision_deck.jlp.utils;

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
    private final StringWriter m_stringWriter;

    /**
     * Creates a new supplier.
     * 
     * @param stringWriter
     *            not <code>null</code>.
     */
    public StringWriterSupplier(StringWriter stringWriter) {
	checkNotNull(stringWriter);
	m_stringWriter = stringWriter;
    }

    @Override
    public StringWriter getOutput() throws IOException {
	return m_stringWriter;
    }
}