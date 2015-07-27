/* OutAggregateStream.java
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
 * Output aggregate stream.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class OutAggregateStream implements Stream {

  // static logger
  private static final Logger log =
    Logger.getLogger(OutAggregateStream.class.getName());

  // fields
  protected InputTreeProcessor processor;
  protected OutStream outStream;
  protected int widthOutAggregate;
  protected Endianness endiannessOut;
  protected BigInteger mask;

  /**
   * Sets output aggregate width.
   *
   * @param widthOutAggregate output aggregate width
   */
  public void setWidthOutAggregate(final int widthOutAggregate
				   ) throws ProcessorException {
    log.finer("Setting widthOutAggregate to: " + widthOutAggregate);
    if (widthOutAggregate < 1) {
      throw new ProcessorException("Illegal output width");
    }
    if ((widthOutAggregate % outStream.getWidthOut()) != 0) {
      throw new ProcessorException("Mismatch of output widths");
    }
    this.widthOutAggregate = widthOutAggregate;
    mask = Util.makeMask(widthOutAggregate);
    reset();
  }

  /**
   * Gets output aggregate width.
   *
   * @return output aggregate width
   */
  public int getWidthOutAggregate() {
    log.finer("Getting widthOutAggregate: " + widthOutAggregate);
    return widthOutAggregate;
  }
    
  /**
   * Sets output endianness.
   *
   * @param endiannessOut output endianness
   */
  public void setEndiannessOut(final Endianness endiannessOut) {
    log.finer("Getting endiannessOut: " + endiannessOut);
    this.endiannessOut = endiannessOut;
    reset();
  }
    
  /**
   * Gets output endianness.
   *
   * @return output endianness
   */
  public Endianness getEndiannessOut() {
    return endiannessOut;
  }
    
  // for description see Stream
  @Override
  public void setDefaults() {
    log.fine("Setting defaults on OutAggregateStream");
    widthOutAggregate = 8;
    mask = Constants.FF;
    endiannessOut = Endianness.BIG;
    reset();
  }

  // for description see Stream
  @Override
  public void reset() {
    log.fine("Resetting OutAggregateStream");
  };
    
  // for description see Stream
  @Override
  public void write(BigInteger value) throws IOException {
    log.finest("Writing BigInteger to OutStream: " +
	       Util.bigIntegerToString(value));
    value = value.and(mask);
    final int widthOut = outStream.getWidthOut();
    try {
      processor.trigger(Variable.Type.AGGREGATE_STREAM_OUT, value);
    } catch (final ProcessorException exception) {
      throw new IOException(exception.getMessage());
    }
    final int counter = widthOutAggregate / widthOut;
    int offset;
    int step;
    if (endiannessOut == Endianness.BIG) {
      offset = (counter - 1) * widthOut;
      step = -widthOut;
    } else {
      offset = 0;
      step = widthOut;
    }
    final BigInteger outMask = Util.makeMask(widthOut);
    for (int i = 0; i < counter; i++) {
      outStream.write(value.shiftRight(offset).and(outMask));
      offset += step;
    }
  }
    
  // for description see Stream
  @Override
  public void flush() throws IOException {
    log.finer("Flushing OutAgregateStream");
    outStream.flush();
  }

  @Override
  public void close() throws IOException {
    log.fine("Closing OutAgregateStream");
    outStream.close();
  }
    
  // for description see Object
  @Override
  public String toString() {
    return "OutAggregateStream";
  }

  /**
   * Main constructor.
   *
   * @param processor input tree processor object
   * @param outStream downstream output stream
   */
  public OutAggregateStream(final InputTreeProcessor processor,
			    final OutStream outStream) {
    log.fine("OutAggregateStream creation started");

    this.processor = processor;
    this.outStream = outStream;
    setDefaults();
	
    log.fine("OutAggregateStream creation completed");
  }
}
