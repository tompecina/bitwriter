/* Polynomial.java
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
 * CRC generator polynomial.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Polynomial {

    // static logger
    private static final Logger log =
	Logger.getLogger(Polynomial.class.getName());

    /**
     * Polynomial notations.
     */
    public enum Notation {NORMAL, FULL, REVERSED, KOOPMAN};
    
    // fields
    protected int width;
    protected BigInteger polynomial;

    /**
     * Gets the bit-width of the polynomial.
     *
     * @return the bit-width of the polynomial (ie, the degree minus one)
     */
    public int getWidth() {
	log.finer("Getting width: " + width);
	return width;
    }

    /**
     * Gets the polynomial in the desired notation.
     *
     * @param  notation the notation
     * @return          the polynomial, in the desired notation,
     *                  or <code>null</code> if conversion is impossible 
     */
    public BigInteger getPolynomial(final Notation notation) {
	log.finest("Getting polynomial: " +
		   Util.bigIntegerToString(polynomial) +
		   ", in notation: " + notation);
	BigInteger r;
	switch (notation) {
	    case NORMAL:
		r = polynomial;
	    case FULL:
		r = polynomial.setBit(width);
	    case REVERSED:
		r = Util.reflect(polynomial, width);
	    case KOOPMAN:
	    default:
		r = (polynomial.testBit(0) ?
		     null :
		     polynomial.setBit(width).shiftRight(1));
	}
	log.finest("Result: " + Util.bigIntegerToString(r));
	return r;
    }

    /**
     * Gets the polynomial in the nornaml notation.
     *
     * @return the polynomial in the normal notation
     */
    public BigInteger getPolynomial() {
	log.finest("Getting polynomial: " +
		   Util.bigIntegerToString(polynomial));
	return polynomial;
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "Polynomial";
    }

    /**
     * Main constructor.
     *
     * @param     polynomial the polynomial
     * @param     notation   the notation
     * @param     width      the bit-width of the polynomial
     * @exception PolynomialException on error in parameters
     */
    public Polynomial(final BigInteger polynomial,
		      final Notation notation,
		      final int width
		      ) throws PolynomialException {
	log.fine("Polynomial object creation started, with polynomial: " +
		 Util.bigIntegerToString(polynomial) +
		 ", notation: " + notation + ", width: " + width);
	if (polynomial.signum() <= 0) {
	    throw new PolynomialException(
	        "Non-positive polynomial not allowed");
	}
	if (width < 0) {
	    throw new PolynomialException(
	        "Negative polynomial width not allowed");
	}
	switch (notation) {
	    case NORMAL:
		if (width == 0) {
		    throw new PolynomialException("Polynomial width must" +
		        " be specified for this notation");
		}
		this.polynomial = polynomial;
		this.width = width;
		if (polynomial.bitLength() > width) {
		    throw new PolynomialException(
		        "Illegal polynomial/width combination");
		}
		break;
	    case FULL:
		this.width = polynomial.bitLength();
		this.polynomial = polynomial.clearBit(this.width--);
		if ((width != 0) && (width != this.width)) {
		    throw new PolynomialException(
		        "Polynomial/width mismatch");
		}
		break;
	    case REVERSED:
		if (width == 0) {
		    throw new PolynomialException("Polynomial width must" +
		        " be specified for this notation");
		}
		if (polynomial.bitLength() > width) {
		    throw new PolynomialException(
		        "Illegal polynomial/width combination");
		}
		this.polynomial = Util.reflect(polynomial, width);
		this.width = width;
		break;
	    case KOOPMAN:
		this.width = polynomial.bitLength();
		if ((width != 0) && (width != this.width)) {
		    throw new PolynomialException(
		        "Polynomial/width mismatch");
		}
		this.polynomial = polynomial.shiftLeft(1)
		    .clearBit(this.width).setBit(0);
		break;
	}
	log.fine("Polynomial object created, polynomial: "  +
		 Util.bigIntegerToString(this.polynomial) +
		 ", width: " + this.width);
    }

    /**
     * Simplified constructor, ommitting width.
     *
     * @param     polynomial the polynomial
     * @param     notation   the notation
     * @exception PolynomialException on error in parameters
     */
    public Polynomial(final BigInteger polynomial,
		      final Notation notation
		      ) throws PolynomialException {
	this(polynomial, notation, 0);
    }
}
