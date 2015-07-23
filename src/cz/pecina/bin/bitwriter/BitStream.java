/* BitStream.java
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
 * Bit stream.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class BitStream implements Stream {

    // static logger
    private static final Logger log =
	Logger.getLogger(BitStream.class.getName());

    // fields
    protected InputTreeProcessor processor;
    protected OutAggregateStream outAggregateStream;
    protected boolean reflectOut;
    protected BigInteger buffer;
    protected int counter;
    protected int offset;
    protected int step;
    protected static final BigInteger mask = BigInteger.ONE;

    /**
     * Sets output reflection.
     *
     * @param reflectOut output reflection
     */
    public void setReflectOut(final boolean reflectOut) {
	log.finer("Setting output reflection to: " + reflectOut);
	this.reflectOut = reflectOut;
	reset();
    }
    
    /**
     * Gets output reflection.
     *
     * @return output reflection
     */
    public boolean getReflectOut() {
	log.finer("Getting input reflection: " + reflectOut);
	return reflectOut;
    }
    
    // for description see Stream
    @Override
    public void setDefaults() {
	log.fine("Setting defaults on BitStream");
	reflectOut = false;
	reset();
    }

    // for description see Stream
    @Override
    public void reset() {
	log.fine("Resetting BitStream");
	buffer = BigInteger.ZERO;
	counter = outAggregateStream.getWidthOutAggregate();
	if (reflectOut) {
	    offset = 0;
	    step = 1;
	} else {
	    offset = counter - 1;
	    step = -1;
	}
    }

    // for description see Stream
    @Override
    public void write(BigInteger value) throws IOException {
	log.finest("Writing BigInteger to BitStream: " +
		   Util.bigIntegerToString(value));
	value = value.and(mask);
	try {
	    processor.trigger(Variable.Type.BITSTREAM, value);
	} catch (ProcessorException exception) {
	    throw new IOException(exception.getMessage());
	}
	if (value.testBit(0)) {
	    buffer = buffer.setBit(offset);
	} else {
	    buffer = buffer.clearBit(offset);
	}
	offset += step;
	if ((--counter) == 0) {
	    outAggregateStream.write(buffer);
	    reset();
	}
    }
    
    // for description see Stream
    @Override
    public void flush() throws IOException {
	log.finer("Flushing BitStream");
	if (counter != outAggregateStream.getWidthOutAggregate()) {
	    if (!reflectOut) {
		buffer = buffer.shiftRight(counter);
	    }
	    outAggregateStream.write(buffer);
	    reset();
	}
	outAggregateStream.flush();
    }
    
    // for description see Stream
    @Override
    public void close() throws IOException {
	log.fine("Closing BitStream");
	outAggregateStream.close();
    }	

    // for description see Object
    @Override
    public String toString() {
	return "BitStream";
    }

    /**
     * Main constructor.
     *
     * @param processor          input tree processor object
     * @param outAggregateStream downstream output aggregate stream
     */
    public BitStream(final InputTreeProcessor processor,
		     final OutAggregateStream outAggregateStream) {
	log.fine("BitStream creation started");

	this.processor = processor;
	this.outAggregateStream = outAggregateStream;
	setDefaults();
	
	log.fine("BitStream creation completed");
    }
}
