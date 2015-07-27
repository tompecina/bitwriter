/* CheckSum.java
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
import java.util.logging.Logger;

/**
 * Checksum calculator.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CheckSum extends Calculator {

  // static logger
  private static final Logger log =
    Logger.getLogger(CheckSum.class.getName());

  // fields
  protected CheckSumModel model;
  protected BigInteger mask;

  // for description see Calculator
  @Override
  public BigInteger getRegister() {
    final BigInteger r = register.xor(model.getXorOut());
    log.finer("Getting register: " + Util.bigIntegerToString(r));
    return r;
  }

  // for description see Calculator
  @Override
  public void updateBit(final int b) {
    log.finest("Updating CheckSum with: " + b);
    if ((b & 1) == 1) {
      update(BigInteger.ONE);
    }
  }

  // for description see Calculator
  @Override
  public void updateBit(final boolean b) {
    log.finest("Updating CheckSum with: " + b);
    if (b) {
      update(BigInteger.ONE);
    }
  }

  // for description see Calculator
  @Override
  public void update(final byte[] d) {
    log.finest("Updating CheckSum with an array of length: " + d.length);
    for (byte b: d) {
      update(BigInteger.valueOf(b));
    }
  }

  // for description see Calculator
  @Override
  public void update(final int b) {
    log.finest("Updating CheckSum with: " + b);
    update(BigInteger.valueOf(b));
  }

  // for description see Calculator
  @Override
  public void update(final BigInteger b) {
    log.finest("Updating CheckSum with: " + Util.bigIntegerToString(b));
    register = register.add(b.and(mask)).and(mask);
  }

  // for description see Object
  @Override
  public String toString() {
    return "CheckSum";
  }

  /**
   * Main constructor.
   *
   * @param model model to be used for the calculator
   */
  public CheckSum(final CheckSumModel model) {
    log.fine("Creation of new CheckSum started");
	
    this.model = model;
    mask = Util.makeMask(model.getWidth());
    register = model.getXorIn().and(mask);

    log.fine("Creation of new CheckSum completed");
  }
}
