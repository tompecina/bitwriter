/* OutStream.java
 *
 * Copyright (C) 2015-19, Tomáš Pecina <tomas@pecina.cz>
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
 * Output stream.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class OutStream implements Stream {

  // static logger
  private static final Logger log =
    Logger.getLogger(OutStream.class.getName());

  // fields
  protected InputTreeProcessor processor;
  protected ControlledOutputStream controlledOutputStream;
  protected int widthOut;
  protected static final BigInteger mask = Constants.FF;

  /**
   * Sets output width.
   *
   * @param     widthOut           output width
   * @exception ProcessorException on illegal output width
   */
  public void setWidthOut(final int widthOut) throws ProcessorException {
    log.finer("Setting widthOut to: " + widthOut);
    if (widthOut != 8) {
      throw new ProcessorException(
        "Output width must be 8 bits on this architecture");
    }
    this.widthOut = widthOut;
    reset();
  }

  /**
   * Gets output width.
   *
   * @return output width
   */
  public int getWidthOut() {
    log.finer("Getting widthOut: " + widthOut);
    return widthOut;
  }
    
  // for description see Stream
  @Override
  public void setDefaults() {
    log.fine("Setting defaults on OutStream");
    widthOut = 8;
    reset();
  }

  // for description see Stream
  @Override
  public void reset() {
    log.fine("Resetting OutStream");
  };
    
  // for description see Stream
  @Override
  public void write(BigInteger value) throws IOException {
    log.finest("Writing to OutStream: " + Util.bigIntegerToString(value));
    value = value.and(mask);
    try {
      processor.trigger(Variable.Type.STREAM_OUT, value);
    } catch (final ProcessorException exception) {
      throw new IOException(exception.getMessage());
    }
    controlledOutputStream.write(value);
  }
    
  // for description see Stream
  @Override
  public void flush() throws IOException {
    log.finer("Flushing OutStream");
    controlledOutputStream.flush();
  }	

  // for description see Stream
  @Override
  public void close() throws IOException {
    log.fine("Closing OutStream");
    controlledOutputStream.close();
  }
    
  // for description see Object
  @Override
  public String toString() {
    return "OutStream";
  }

  /**
   * Main constructor.
   *
   * @param processor              input tree processor object
   * @param controlledOutputStream downstream controlled output stream
   */
  public OutStream(final InputTreeProcessor processor,
		   final ControlledOutputStream controlledOutputStream) {
    log.fine("OutStream creation started");

    this.processor = processor;
    this.controlledOutputStream = controlledOutputStream;
    setDefaults();
	
    log.fine("OutStream creation completed");
  }
}
