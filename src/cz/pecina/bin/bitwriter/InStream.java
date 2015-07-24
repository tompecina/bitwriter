/* InStream.java
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
 * Input stream.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class InStream implements Stream {

    // static logger
    private static final Logger log =
	Logger.getLogger(InStream.class.getName());

    // fields
    protected InputTreeProcessor processor;
    protected InAggregateStream inAggregateStream;
    protected int widthIn;
    protected Endianness endiannessIn;
    protected BigInteger buffer = BigInteger.ZERO;
    protected int counter;
    protected int offset;
    protected int step;
    protected BigInteger mask;

    /**
     * Sets input width.
     *
     * @param     widthIn            input width
     * @exception ProcessorException on illegal input width
     */
    public void setWidthIn(final int widthIn) throws ProcessorException {
	log.finer("Setting widthIn to: " + widthIn);
	if (widthIn < 1) {
	    throw new ProcessorException("Illegal input width");
	}
	if ((inAggregateStream.getWidthInAggregate() % widthIn) != 0) {
	    throw new ProcessorException("Mismatch of input widths");
	}
	this.widthIn = widthIn;
	mask = Util.makeMask(widthIn);
	reset();
    }

    /**
     * Gets input width.
     *
     * @return input width
     */
    public int getWidthIn() {
	log.finer("Getting widthIn: " + widthIn);
	return widthIn;
    }
    
    /**
     * Sets input endianness.
     *
     * @param endiannessIn input endianness
     */
    public void setEndiannessIn(final Endianness endiannessIn) {
	log.finer("Setting endiannessIn to: " + endiannessIn);
	this.endiannessIn = endiannessIn;
	reset();
    }
    
    /**
     * Gets input endianness.
     *
     * @return input endianness
     */
    public Endianness getEndiannessIn() {
	log.finer("Getting endiannessIn: " + endiannessIn);
	return endiannessIn;
    }

    // for description see Stream
    @Override
    public void setDefaults() {
	log.fine("Setting defaults on InStream");
	widthIn = 8;
	mask = Constants.FF;
	endiannessIn = Endianness.BIG;
	reset();
    }

    // for description see Stream
    @Override
    public void reset() {
	log.fine("Resetting InStream");
	buffer = BigInteger.ZERO;
	counter = inAggregateStream.getWidthInAggregate() / widthIn;
	if (endiannessIn == Endianness.BIG) {
	    offset = (counter - 1) * widthIn;
	    step = -widthIn;
	} else {
	    offset = 0;
	    step = widthIn;
	}
    }
    
    // for description see Stream
    @Override
    public void write(BigInteger value) throws IOException {
	log.finest("Writing BigInteger to InStream: " +
		   Util.bigIntegerToString(value));
	value = value.and(mask);
	try {
	    processor.trigger(Variable.Type.STREAM_IN, value);
	} catch (final ProcessorException exception) {
	    throw new IOException(exception.getMessage());
	}
	for (int i = 0; i < widthIn; i++) {
	    if (value.testBit(i)) {
		buffer = buffer.setBit(offset + i);
	    } else {
		buffer = buffer.clearBit(offset + i);
	    }
	}
	offset += step;
	if ((--counter) == 0) {
	    inAggregateStream.write(buffer);
	    reset();
	}
    }

    // for description see Stream
    @Override
    public void flush() throws IOException {
	log.finer("Flushing InStream");
	if (counter != (inAggregateStream.getWidthInAggregate() / widthIn)) {
	    if (endiannessIn == Endianness.BIG) {
		buffer = buffer.shiftRight(counter * widthIn);
	    }
	    inAggregateStream.write(buffer);
	    reset();
	}
	inAggregateStream.flush();
    }	
    
    // for description see Stream
    @Override
    public void close() throws IOException {
	log.fine("Closing InStream");
	inAggregateStream.close();
    }	

    // for description see Object
    @Override
    public String toString() {
	return "InStream";
    }

    /**
     * Main constructor.
     *
     * @param processor         input tree processor object
     * @param inAggregateStream downstream input aggregate stream
     */
    public InStream(final InputTreeProcessor processor,
		    final InAggregateStream inAggregateStream) {
	log.fine("InStream creation started");

	this.processor = processor;
	this.inAggregateStream = inAggregateStream;
	setDefaults();
	
	log.fine("InStream creation completed");
    }
}
