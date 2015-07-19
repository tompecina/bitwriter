/* Crc.java
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
 * CRC calculator.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Crc extends Calculator {

    // static logger
    private static final Logger log = Logger.getLogger(Crc.class.getName());

    // fields
    protected CrcModel model;
    protected BigInteger mask;
    protected int shift;
    protected BigInteger[] table;

    // for description see Calculator
    @Override
    public void setRegister(final BigInteger value) {
	log.finer("Setting register to: " + Util.bigIntegerToString(value));
	register = value.xor(model.getXorIn()).and(mask);
    }

    // for description see Calculator
    @Override
    public BigInteger getRegister() {
	BigInteger n = register;
	if (model.getReflectOut()) {
	    n = Util.reflect(n, model.getPolynomial().getWidth());
	}
	final BigInteger r = n.xor(model.getXorOut());
	log.finer("CRC value requested, returning: " +
		  Util.bigIntegerToString(r));
	return r;
    }

    // for description see Calculator
    @Override
    public void updateBit(final int b) {
	log.finest("Updating CRC with: " + b);
	if (register.testBit(model.getPolynomial().getWidth() - 1) ==
	    (b == 0)) {
	    register = register.shiftLeft(1).xor(model.getPolynomial()
	               .getPolynomial()).and(mask);
	} else {
	    register = register.shiftLeft(1).and(mask);
	}
    }

    // for description see Calculator
    @Override
    public void updateBit(final boolean b) {
	log.finest("Updating CRC with: " + b);
	updateBit(b ? 1 : 0);
    }

    // for description see Calculator
    @Override
    public void update(final byte[] d) {
	log.finer("Updating CRC with an array of length: " + d.length);
	if (model.getReflectIn()) {
	    register = Util.reflect(register,
				    model.getPolynomial().getWidth() + shift);
	    for (byte b: d) {
	    	final int i = (register.shiftRight(shift)
			       .intValue() ^ b) & 0xff;
	    	register = register.shiftRight(8).xor(table[i])
		           .and(mask.shiftLeft(shift));
	    }
	    register = Util.reflect(register,
				    model.getPolynomial().getWidth() +
				    shift).and(mask);
	} else {
	    register = register.shiftLeft(shift);
	    for (byte b: d) {
	    	final int i = (register.shiftRight(model.getPolynomial()
			       .getWidth() + shift - 8).intValue() ^ b) & 0xff;
	    	register = register.shiftLeft(8 - shift).xor(table[i])
		           .and(mask.shiftLeft(shift));
	    }
	    register = register.shiftRight(shift);
	}
    }

    // for description see Calculator
    @Override
    public void update(final int b) {
	log.finest("Updating CRC with: " + b);
	if (model.getReflectIn()) {
	    register = Util.reflect(register, model.getPolynomial()
				    .getWidth() + shift);
	    final int i = (register.shiftRight(shift).intValue() ^ b) & 0xff;
	    register = register.shiftRight(8).xor(table[i])
		       .and(mask.shiftLeft(shift));
	    register = Util.reflect(register, model.getPolynomial()
				    .getWidth() + shift).and(mask);
	} else {
	    register = register.shiftLeft(shift);
	    final int i = (register.shiftRight(model.getPolynomial()
			   .getWidth() + shift - 8).intValue() ^ b) & 0xff;
	    register = register.shiftLeft(8 - shift).xor(table[i])
		       .and(mask.shiftLeft(shift));
	    register = register.shiftRight(shift);
	}
    }

    // for description see Calculator
    @Override
    public void update(final BigInteger b) {
	log.finest("Updating CRC with: " + Util.bigIntegerToString(b));
	update(b.and(Constants.FF).intValue());
    }

    // for description see Object
    @Override
    public String toString() {
	return "Crc";
    }

    /**
     * Main constructor.
     *
     * @param model model to be used for the calculator
     */
    public Crc(final CrcModel model) {
	log.fine("CRC object creation started, with polynomial: " +
		 Util.bigIntegerToString(model.getPolynomial()
					 .getPolynomial()) +
		 ", width: " +
		 model.getPolynomial().getWidth());
	this.model = model;
	mask = Util.makeMask(model.getPolynomial().getWidth());
	shift = 0;
	if (model.getPolynomial().getWidth() < 8) {
	    shift = 8 - model.getPolynomial().getWidth();
	}
	register = model.getXorIn().and(mask);
	table = new BigInteger[0x100];
	BigInteger p = model.getPolynomial().getPolynomial().shiftLeft(shift);
	BigInteger m = mask.shiftLeft(shift);
	for (int i = 0; i < 0x100; i++) {
	    BigInteger n = BigInteger.valueOf(i);
	    if (model.getReflectIn()) {
		n = Util.reflect(n, 8);
	    }
	    n = n.shiftLeft(model.getPolynomial().getWidth() + shift - 8);
	    for (int j = 0; j < 8; j++) {
		if (n.testBit(model.getPolynomial().getWidth() + shift - 1)) {
		    n = n.shiftLeft(1).xor(p);
		} else {
		    n = n.shiftLeft(1);
		}
	    }
	    if (model.getReflectIn()) {
		n = Util.reflect(n.shiftRight(shift),
				 model.getPolynomial().getWidth())
		                 .shiftLeft(shift);
	    }
	    table[i] = n.and(m);
	}
	log.fine("CRC object creation completed");
    }
}
