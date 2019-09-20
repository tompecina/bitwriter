/* ControoledOutputStream.java
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
import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Controlled output stream.
 * 
 * This stream can be discarded and/or switched to hexadecimal mode,
 * useful for debugging.  
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class ControlledOutputStream implements Stream {

  // static logger
  private static final Logger log =
    Logger.getLogger(ControlledOutputStream.class.getName());

  /**
   * Number of characters per line in the hexadecimal mode.
   */
  protected int BYTES_PER_LINE = 16;

  // fields
  private InputTreeProcessor processor;
  private OutputStream outputStream;
  private boolean discard;
  private boolean hexMode;
  private int hexCount;
  private int streamNumber;
  private int streamLength;
  private int totalLength;
  protected static final BigInteger mask = Constants.FF;
    
  /**
   * Sets the discard mode switch.
   *
   * @param discard if <code>true</code> data is not written to
   *                the output stream
   */
  public void setDiscard(final boolean discard) {
    log.finer("Setting discard to: " + discard);
    this.discard = discard;
    reset();
  }

  /**
   * Gets the discard mode switch.
   *
   * @return value of the <code>discard</code> mode switch
   */
  public boolean getDiscard() {
    log.finer("Getting discard: " + discard);
    return discard;
  }

  /**
   * Sets the hexadecimal mode switch.
   *
   * @param hexMode if <code>true</code> data is written in hexadecimal
   *                representation instead of bytes
   */
  public void setHexMode(final boolean hexMode) {
    log.finer("Setting hexMode to: " + hexMode);
    this.hexMode = hexMode;
    reset();
  }

  /**
   * Gets the hexadecimal mode switch.
   *
   * @return value of the hexadecimal mode switch
   */
  public boolean isHexMode() {
    log.finer("Getting hexMode: " + hexMode);
    return hexMode;
  }

  /**
   * Gets the stream number.
   *
   * @return number of the current stream (numbering is zero-based)
   */
  public int  getStreamNumber() {
    log.finer("Getting streamNumber: " + streamNumber);
    return streamNumber;
  }

  /**
   * Gets the number of bytes written to the current stream.
   *
   * @return number of bytes written to the current stream
   */
  public int getStreamLength() {
    log.finest("Getting streamLength: " + streamNumber);
    return streamLength;
  }

  /**
   * Gets the total number of bytes already written to OutputStream.
   *
   * @return number of bytes written to OutputStream
   */
  public int getTotalLength() {
    log.finest("Getting totalLength: " + totalLength);
    return totalLength;
  }

  /**
   * Resets the stream counters.
   */
  public void resetStream() {
    log.finer("Stream counters reset");
    streamLength = 0;
    streamNumber++;
  }
    
  // for description see Object
  @Override
  public void setDefaults() {
    log.fine("Setting defaults on ControlledOutputStream");
    reset();
  };

  /**
   * Write a newline to the controlled output stream.
   *
   * @exception IOException on I/O error
   */
  public void newLine() throws IOException {
    log.finest("Writing newlien to ControlledOutputStream");
    outputStream.write(String.format("%n").getBytes());
  }

  // for description see Object
  @Override
  public void reset() {
    log.fine("Resetting ControlledOutputStream");
  };
    
  // for description see Object
  @Override
  public void write(BigInteger value) throws IOException {
    log.finest("Writing to ControlledOutputStream: " +
	       Util.bigIntegerToString(value));
    value = value.and(mask);
    try {
      processor.trigger(Variable.Type.OUTPUT_STREAM, value);
    } catch (final ProcessorException exception) {
      throw new IOException(exception.getMessage());
    }
    if (!discard) {
      if (hexMode) {
	outputStream.write(String.format(
	  "%s%02x",
	  (((hexCount % BYTES_PER_LINE) != 0) ? " " : ""),
	  value.intValue()).getBytes());
	if (((++hexCount) % BYTES_PER_LINE) == 0) {
	  newLine();
	}
      }
      else {
	outputStream.write(value.intValue());
      }
      totalLength++;
    }
    streamLength++;
  }

  // for description see Object
  @Override
  public void flush() throws IOException {
    log.finer("Flushing ControlledOutputStream");
    if (!discard) {
      outputStream.flush();
    }
  }	

  // for description see Object
  @Override
  public void close() throws IOException {
    log.fine("Closing ControlledOutputStream");
    if (hexMode && (hexCount != 0) &&
	((hexCount % BYTES_PER_LINE) != 0)) {
      newLine();
    }
    outputStream.close();
  }
    
  // for description see Object
  @Override
  public String toString() {
    return "ControlledOutputStream";
  }

  /**
   * Main constructor.
   *
   * @param processor    input tree processor object
   * @param outputStream downstream output stream
   */
  public ControlledOutputStream(final InputTreeProcessor processor,
				final OutputStream outputStream) {
    log.fine("ControlledOutputStream creation started");

    this.processor = processor;
    this.outputStream = outputStream;
	
    log.fine("ControlledOutputStream creation completed");
  }
}
