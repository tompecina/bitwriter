/* Connector.java
 *
 * Copyright (C) 2015, Tomáš Pecina <tomas@pecina.cz>
 *
 * This file is part of cz.pecina.bin, a suite of binary-file
 * processing applications.
 *
 * This application is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.         
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.pecina.bin.bitwriter;

import java.math.BigInteger;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Connector object providing script with access of the application's resources.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Connector {

    // static logger
    private static final Logger log =
	Logger.getLogger(Connector.class.getName());

    // fields
    protected InputTreeProcessor processor;

    
    /**
     * Accessor of the input tree processor.
     *
     * @return the input tree processor
     */
    public InputTreeProcessor getProcessor() {
	log.finer("Script requests InputTreeProcessor object");
	return processor;
    }

    // sanitizes (conditions) data written by script to streams
    private BigInteger sanitize(final Object value
				) throws ProcessorException {
	if (value == null) {
	    throw new ProcessorException(
	        "null cannot be converted to number");
	}
	BigInteger r = null;
	if (value instanceof BigInteger) {
	    r = (BigInteger)value;
	} else {
	    try {
		if (value instanceof Double) {
		    r = BigInteger.valueOf(Math.round((Double)value));
		} else {
		    r = new BigInteger(value.toString());
		}
	    } catch (final NumberFormatException |
		     NullPointerException exception) {
		throw new ProcessorException(
		    "Value '" + value + "' cannot be converted to number");
	    }
	}
	return r;
    }
    
    /**
     * Writes to the input stream.
     *
     * @param     value              object to be written
     * @exception ProcessorException if invalid value supplied
     * @exception IOException        on I/O error
     */
    public void writeInStream(final Object value
			      ) throws ProcessorException, IOException {
	processor.getInStream().write(sanitize(value));
    }

    /**
     * Writes to the input stream (a convenience method,
     * equivalent to {@link #writeInStream(Object)}).
     *
     * @param     value              object to be written
     * @exception ProcessorException if invalid value supplied
     * @exception IOException        on I/O error
     */
    public void write(final Object value
		      ) throws ProcessorException, IOException {
	processor.getInStream().write(sanitize(value));
    }

    /**
     * Writes to the input aggregate stream.
     *
     * @param     value              object to be written
     * @exception ProcessorException if invalid value supplied
     * @exception IOException        on I/O error
     */
    public void writeInAggregateStream(final Object value
				       ) throws ProcessorException,
						IOException {
	processor.getInAggregateStream().write(sanitize(value));
    }

    /**
     * Writes to the bit stream.
     *
     * @param     value              object to be written
     * @exception ProcessorException if invalid value supplied
     * @exception IOException        on I/O error
     */
    public void writeBitStream(final Object value
			       ) throws ProcessorException, IOException {
	processor.getBitStream().write(sanitize(value));
    }

    /**
     * Writes to the output aggregate stream.
     *
     * @param     value              object to be written
     * @exception ProcessorException if invalid value supplied
     * @exception IOException        on I/O error
     */
    public void writeOutAggregateStream(final Object value
					) throws ProcessorException,
						 IOException {
	processor.getOutAggregateStream().write(sanitize(value));
    }

    /**
     * Writes to the output stream.
     *
     * @param     value              object to be written
     * @exception ProcessorException if invalid value supplied
     * @exception IOException        on I/O error
     */
    public void writeOutStream(final Object value
			       ) throws ProcessorException, IOException {
	processor.getOutStream().write(sanitize(value));
    }

    /**
     * Writes to the controlled output stream.
     *
     * @param     value              object to be written
     * @exception ProcessorException if invalid value supplied
     * @exception IOException        on I/O error
     */
    public void writeControlledOutputStream(final Object value
					    ) throws ProcessorException,
						     IOException {
	processor.getControlledOutputStream().write(sanitize(value));
    }

    /**
     * Flushes the streams.
     *
     * @exception IOException on I/O error
     */
    public void flush() throws IOException {
	processor.getInStream().flush();
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "Connector";
    }

    public Connector(final InputTreeProcessor processor) {
	log.fine("Connection creation started");

	this.processor = processor;
	
	log.fine("Connector creation completed");
    }
}
