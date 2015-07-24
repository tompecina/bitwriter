/* InAggregateStream.java
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
 * Input aggregate stream.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class InAggregateStream implements Stream {

    // static logger
    private static final Logger log =
	Logger.getLogger(InAggregateStream.class.getName());

    // fields
    protected InputTreeProcessor processor;
    protected BitStream bitStream;
    protected int widthInAggregate;
    protected boolean reflectIn;
    protected BigInteger mask;

    /**
     * Sets input aggregate width.
     *
     * @param widthInAggregate input aggregate width
     */
    public void setWidthInAggregate(final int widthInAggregate) {
	log.finer("Setting widthInAggregate to: " + widthInAggregate);
	this.widthInAggregate = widthInAggregate;
	mask = Util.makeMask(widthInAggregate);
	reset();
    }

    /**
     * Gets input aggregate width.
     *
     * @return input aggregate width
     */
    public int getWidthInAggregate() {
	log.finer("Getting widthInAggregate: " + widthInAggregate);
	return widthInAggregate;
    }
    
    /**
     * Sets input reflection.
     *
     * @param reflectIn input reflection
     */
    public void setReflectIn(final boolean reflectIn) {
	log.finer("Setting input reflection to: " + reflectIn);
	this.reflectIn = reflectIn;
	reset();
    }
    
    /**
     * Gets input reflection.
     *
     * @return input reflection
     */
    public boolean getReflectIn() {
	log.finer("Getting input reflection: " + reflectIn);
	return reflectIn;
    }

    // for description see Stream
    @Override
    public void setDefaults() {
	log.fine("Setting defaults on InAggregateStream");
	widthInAggregate = 8;
	mask = Constants.FF;
	reflectIn = false;
	reset();
    }

    // for description see Stream
    @Override
    public void reset() {
	log.fine("Resetting InAggregateStream");
    };
    
    // for description see Stream
    @Override
    public void write(BigInteger value) throws IOException {
	log.finest("Writing BigInteger to InAggregateStream: " +
		   Util.bigIntegerToString(value));
	value = value.and(mask);
	try {
	    processor.trigger(Variable.Type.AGGREGATE_STREAM_IN, value);
	} catch (final ProcessorException exception) {
	    throw new IOException(exception.getMessage());
	}
	final int offset = (reflectIn ? 0 : (widthInAggregate - 1));
	final int step = (reflectIn ? 1 : -1);
	for (int i = offset;
	     (reflectIn ? (i < widthInAggregate) : (i >= 0));
	     i += step) {
	    bitStream.write(
	      value.testBit(i) ? BigInteger.ONE : BigInteger.ZERO);
	}
    }
    
    // for description see Stream
    @Override
    public void flush() throws IOException {
	log.finer("Flushing InAgregateStream");
	bitStream.flush();
    }

    // for description see Stream
    @Override
    public void close() throws IOException {
	log.fine("Closing InAgregateStream");
	bitStream.close();
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "InAggregateStream";
    }

    /**
     * Main constructor.
     *
     * @param processor input tree processor object
     * @param bitStream downstream bit stream
     */
    public InAggregateStream(final InputTreeProcessor processor,
			     final BitStream bitStream) {
	log.fine("InAggregateStream creation started");

	this.processor = processor;
	this.bitStream = bitStream;
	setDefaults();
	
	log.fine("InAggregateStream creation completed");
    }
}
