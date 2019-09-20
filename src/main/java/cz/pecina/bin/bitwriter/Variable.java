/* Variable.java
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
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Object representing a variable.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 * @see Calculator
 */
public class Variable {

  // static logger
  private static final Logger log =
    Logger.getLogger(Variable.class.getName());

  /**
   * Variable type.
   */
  public enum Type {STREAM_IN, AGGREGATE_STREAM_IN, BITSTREAM,
		    AGGREGATE_STREAM_OUT, STREAM_OUT, OUTPUT_STREAM};

  // fields
  protected String name;
  protected BigInteger value;
  protected Calculator calculator;
  protected String onStreamIn;
  protected String onAggregateStreamIn;
  protected String onBitStream;
  protected String onAggregateStreamOut;
  protected String onStreamOut;
  protected String onOutputStream;    
  protected Type type;
    
  /**
   * Sets the {@link Calculator}.
   *
   * @param calculator the {@link Calculator} object connected
   *                   to the variable
   */
  public void setCalculator(final Calculator calculator) {
    log.finer("Setting the calculator on '" + name + "'");
    this.calculator = calculator;
  }
    
  /**
   * Gets the {@link Calculator}.
   *
   * @return the {@link Calculator} object connected to the variable
   */
  public Calculator getCalculator() {
    log.finer("Getting the calculator on '" + name + "'");
    return calculator;
  }
    
  /**
   * Gets the name of the variable.
   *
   * @return the name of the variable
   */
  public String getName() {
    log.finest("Getting the name of '" + name + "'");
    return name;
  }
    
  /**
   * Sets the type of the variable.
   *
   * @param type the new type of the variable
   */
  public void setType(final Type type) {
    log.finer("Setting variable '" + name + "' to type: " + type);
    this.type = type;
  }
    
  /**
   * Gets the type of the variable.
   *
   * @return the type of the variable
   */
  public Type getType() {
    log.finest("Getting type of variable '" + name + "': " + type);
    return type;
  }
    
  /**
   * Sets the value of the variable.
   *
   * @param value the new value of the variable
   */
  public void setValue(final BigInteger value) {
    log.finer("Setting variable '" + name + "' to: " +
	      Util.bigIntegerToString(value));
    this.value = value;
  }
    
  /**
   * Gets the value of the variable.
   *
   * @return the value of the variable
   */
  public BigInteger getValue() {
    log.finest("Getting value of variable '" + name + "': " +
	       Util.bigIntegerToString(value));
    return value;
  }
    
  /**
   * Sets the callback script triggered by a write to the input stream.
   *
   * @param onStreamIn the callback script triggered by a write to
   *                   the input stream
   */
  public void setOnStreamIn(final String onStreamIn) {
    log.finer("Setting onStreamIn on '" + name + "' to: " + onStreamIn);
    this.onStreamIn = onStreamIn;
  }

  /**
   * Gets the callback script triggered by a write to the input stream.
   *
   * @return the callback script triggered by a write to the input stream
   */
  public String getOnStreamIn() {
    log.finest("Getting onStreamIn on '" + name + "': " + onStreamIn);
    return onStreamIn;
  }
    
  /**
   * Sets the callback script triggered by a write to the input
   * aggregate stream.
   *
   * @param onAggregateStreamIn the callback script triggered by a write
   * to the input aggregate stream
   */
  public void setOnAggregateStreamIn(final String onAggregateStreamIn) {
    log.finer("Setting onAggregateStreamIn on '" + name + "' to: " +
	      onAggregateStreamIn);
    this.onAggregateStreamIn = onAggregateStreamIn;
  }

  /**
   * Gets the callback script triggered by a write to the input
   * aggregate stream.
   *
   * @return the callback script triggered by a write to the input
   *         aggregate stream
   */
  public String getOnAggregateStreamIn() {
    log.finest("Getting onAggregateStreamIn on '" + name + "': " +
	       onAggregateStreamIn);
    return onAggregateStreamIn;
  }
    
  /**
   * Sets the callback script triggered by a write to the bit stream.
   *
   * @param onBitStream the callback script triggered by a write to
   *                    the bit stream
   */
  public void setOnBitStream(final String onBitStream) {
    log.finer("Setting onBitStream on '" + name + "' to: " + onBitStream);
    this.onBitStream = onBitStream;
  }

  /**
   * Gets the callback script triggered by a write to the bit stream.
   *
   * @return the callback script triggered by a write to the bit stream
   */
  public String getOnBitStream() {
    log.finest("Getting onBitStream on '" + name + "': " + onBitStream);
    return onBitStream;
  }
    
  /**
   * Sets the callback script triggered by a write to the output
   * aggregate stream.
   *
   * @param onAggregateStreamOut the callback script triggered by a write
   *                             to the output aggregate stream
   */
  public void setOnAggregateStreamOut(final String onAggregateStreamOut) {
    log.finer("Setting onAggregateStreamOut on '" + name +
	      "' to: " + onAggregateStreamOut);
    this.onAggregateStreamOut = onAggregateStreamOut;
  }

  /**
   * Gets the callback script triggered by a write to the output
   * aggregate stream.
   *
   * @return the callback script triggered by a write to the output
   *         aggregate stream
   */
  public String getOnAggregateStreamOut() {
    log.finest("Getting onAggregateStreamOut on '" + name +
	       "': " + onAggregateStreamOut);
    return onAggregateStreamOut;
  }
    
  /**
   * Sets the callback script triggered by a write to the output stream.
   *
   * @param onStreamOut the callback script triggered by a write to
   *                    the output stream
   */
  public void setOnStreamOut(final String onStreamOut) {
    log.finer("Setting onStreamOut on '" + name + "' to: " + onStreamOut);
    this.onStreamOut = onStreamOut;
  }

  /**
   * Gets the callback script triggered by a write to the output stream.
   *
   * @return the callback script triggered by a write to the output stream
   */
  public String getOnStreamOut() {
    log.finest("Getting onStreamOut on '" + name + "': " + onStreamOut);
    return onStreamOut;
  }
    
  /**
   * Sets the callback script triggered by a write to the (controlled)
   * output stream.
   *
   * @param onOutputStream the callback script triggered by a write to
   *                       the (controlled) output stream
   */
  public void setOnOutputStream(final String onOutputStream) {
    log.finer("Setting onOutputStream on '" + name +
	      "' to: " + onOutputStream);
    this.onOutputStream = onOutputStream;
  }

  /**
   * Gets the callback script triggered by a write to the (controlled)
   * output stream.
   *
   * @return the callback script triggered by a write to the (controlled)
   *         output stream
   */
  public String getOnOutputStream() {
    log.finest("Getting onOutputStream on '" + name + "': " + onOutputStream);
    return onOutputStream;
  }

  /**
   * Checks if the variable name is legal.
   *
   * @param     name               the name of the variable
   * @exception ProcessorException on illegal variable name
   */
  public static void checkVariableName(final String name
				       ) throws ProcessorException {
    if (name == null) {
      throw new ProcessorException("Variable name missing");
    }
    if (name.isEmpty()) {
      throw new ProcessorException("Empty variable name not allowed");
    }
    if (!Pattern.matches("[a-zA-Z_$][a-zA-Z\\d_$]*", name)) {
      throw new ProcessorException("Illegal character in variable name");
    }
  }
    
  /**
   * Resets the variable.
   */
  public void reset() {
    log.finer("Variable '" + name + "' reset");
    calculator = null;
    value = BigInteger.ZERO;
    onStreamIn = null;
    onAggregateStreamIn = null;
    onBitStream = null;
    onAggregateStreamOut = null;
    onStreamOut = null;
    onOutputStream = null;
  }

  // for description see Object
  @Override
  public String toString() {
    return "Variable";
  }

  /**
   * Main constructor.
   *
   * @param     name               the name of the variable
   * @exception ProcessorException on illegal variable name
   */
  public Variable(final String name) throws ProcessorException {
    log.fine("Variable creation started");

    checkVariableName(name);
    this.name = name;
    reset();
	
    log.fine("Variable creation completed");
  }
}
