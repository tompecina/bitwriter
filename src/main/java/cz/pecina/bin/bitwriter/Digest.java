/* Digest.java
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Message digest calculator.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class Digest extends Calculator {

  // static logger
  private static final Logger log =
    Logger.getLogger(Digest.class.getName());

  // fields
  protected MessageDigest digest;

  // for description see Calculator
  @Override
  public BigInteger getRegister() {
    byte[] buffer;
    try {
      buffer = ((MessageDigest)(digest.clone())).digest();
    } catch (final CloneNotSupportedException exception) {
      buffer = new byte[1];
    }
    BigInteger r = BigInteger.ZERO;
    for (int i = 0; i < buffer.length; i++) {
      r = r.shiftLeft(8).or(BigInteger
			    .valueOf(buffer[i] & 0xff));
    }
    log.finer("Getting register: " + Util.bigIntegerToString(r));
    return r;
  }

  // for description see Calculator
  @Override
  public void updateBit(final int b) {
    log.finest("Updating digest with bit: " + (b & 1));
    digest.update((byte)(b & 1));
  }

  // for description see Calculator
  @Override
  public void updateBit(final boolean b) {
    log.finest("Updating Digest with bit: " + b);
    digest.update((byte)(b ? 1 : 0));
  }

  // for description see Calculator
  @Override
  public void update(final byte[] d) {
    log.finest("Updating Digest with an array of length: " + d.length);
    digest.update(d);
  }

  // for description see Calculator
  @Override
  public void update(final int b) {
    log.finest("Updating Digest with: " + b);
    digest.update((byte)b);
  }

  // for description see Calculator
  @Override
  public void update(final BigInteger b) {
    log.finest("Updating Digest with: " + Util.bigIntegerToString(b));
    digest.update((byte)(b.and(Constants.FF).intValue()));
  }

  // for description see Object
  @Override
  public String toString() {
    return "Digest";
  }

  /**
   * Main constructor.
   *
   * @param     model              model to be used for the calculator
   * @exception ProcessorException on error in parameters
   */
  public Digest(final String model) throws ProcessorException {
    log.fine("Creation of new Digest started");
	
    try {
      digest = MessageDigest.getInstance(model);
    } catch (final NoSuchAlgorithmException exception) {
      throw new ProcessorException("No such message digest algorithm");
    }
    try {
      digest.clone();
    } catch (final CloneNotSupportedException exception) {
      throw new ProcessorException("Algorithm '" + model +
				   "' cannot be used, no support for cloning");
    }
	
    log.fine("Creation of new Digest completed");
  }
}
