/* Parity.java
 *
 * Copyright (C) 2015, Tomas Pecina <tomas@pecina.cz>
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
 * Parity calculator.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Parity extends Calculator {

    // static logger
    private static final Logger log =
	Logger.getLogger(Parity.class.getName());
    
    /**
     * Parity models
     */
    public enum ParityModel {EVEN, ODD};

    // fields
    protected ParityModel model;

    // for description see Calculator
    @Override
    public void setRegister(final BigInteger value) {
	log.finer("Setting register to: " +
		  Util.bigIntegerToString(value));
	register = value.and(BigInteger.ONE);
    }

    // for description see Calculator
    @Override
    public void updateBit(final int b) {
	log.finest("Updating Parity with: " + b);
	if ((b & 1) == 1) {
	    register = register.flipBit(0);
	}
    }

    // for description see Calculator
    @Override
    public void updateBit(final boolean b) {
	log.finest("Updating Parity with: " + b);
	if (b) {
	    register = register.flipBit(0);
	}
    }

    // for description see Calculator
    @Override
    public void update(final byte[] d) {
	log.finest("Updating Parity with an array of length: " + d.length);
	for (byte b: d) {
	    update(b);
	}
    }

    // for description see Calculator
    @Override
    public void update(final int b) {
	log.finest("Updating Parity with: " + b);
	if ((((((b ^ ((b << 4) | (b >> 4))) + 0x41) | 0x7c) + 2) >> 7) == 1) {
	    register = register.flipBit(0);
	}
    }

    // for description see Calculator
    @Override
    public void update(final BigInteger b) {
	log.finest("Updating Parity with: " + Util.bigIntegerToString(b));
	for (int i = b.bitLength() - 1; i >= 0; i--) {
	    if (b.testBit(i)) {
		register = register.flipBit(0);
	    }
	}
    }

    // for description see Calculator
    @Override
    public BigInteger getRegister() {
	final BigInteger r =
	    ((register.testBit(0) == (model == ParityModel.ODD)) ?
	     BigInteger.ZERO : BigInteger.ONE);
	log.finer("Getting register: " + Util.bigIntegerToString(r));
	return r;
    }

    // for description see Object
    @Override
    public String toString() {
	return "Parity";
    }

    /**
     * Main constructor.
     *
     * @param model model to be used for the calculator
     */
    public Parity(final ParityModel model) {
	log.fine("Creation of new Parity started");
	
	this.model = model;

    	log.fine("Creation of new Parity completed");
    }
}
