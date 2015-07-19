/* CheckSumModel.java
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
 * Checksum model.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CheckSumModel {

    // static logger
    private static final Logger log =
	Logger.getLogger(CheckSumModel.class.getName());

    // fields
    protected int width;
    protected BigInteger xorIn;
    protected BigInteger xorOut;

    /**
     * Sets the bit-width of the checksum.
     *
     * @param     width              the bit-width of the checksum
     * @exception ProcessorException on error in width
     */
    public void setWidth(final int width) throws ProcessorException {
	log.finer("Setting width to: " + width);
	if (width < 1) {
	    throw new ProcessorException("Illegal checksum width");
	}
	this.width = width;
    }
    
    /**
     * Gets the bit-width of the checksum.
     *
     * @return the bit-width of the checksum
     */
    public int getWidth() {
	log.finer("Getting width: " + width);
	return width;
    }
    
    /**
     * Sets the initial mask of the checksum.
     *
     * @param     xorIn              the initial mask of the checksum
     * @exception ProcessorException if <code>null</code> mask supplied
     */
    public void setXorIn(final BigInteger xorIn) throws ProcessorException {
	log.finer("Setting xorIn to: " + Util.bigIntegerToString(xorIn));
	if (xorIn == null) {
	    throw new ProcessorException(
	        "Illegal checksum parameters, xorIn == null");
	}
	this.xorIn = xorIn;
    }
    
    /**
     * Gets the initial mask of the checksum.
     *
     * @return the inital mask of the checksum
     */
    public BigInteger getXorIn() {
	log.finer("Getting xorIn: " + Util.bigIntegerToString(xorIn));
	return xorIn;
    }

    /**
     * Sets the final mask of the checksum.
     *
     * @param     xorOut             the final mask of the checksum
     * @exception ProcessorException if <code>null</code> mask supplied
     */
    public void setXorOut(final BigInteger xorOut) throws ProcessorException {
	log.finer("Setting xorOut to: " + Util.bigIntegerToString(xorOut));
	if (xorOut == null) {
	    throw new ProcessorException(
	        "Illegal checksum parameters, xorOut == null");
	}
	this.xorOut = xorOut;
    }

    /**
     * Gets the final mask of the checksum.
     *
     * @return the inital mask of the checksum
     */
    public BigInteger getXorOut() {
	log.finer("Getting xorOut: " + Util.bigIntegerToString(xorOut));
	return xorOut;
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "CheckSumModel";
    }

    /**
     * Main constructor.
     *
     * @param     width              bit-width of the checksum
     * @param     xorIn              initial mask
     * @param     xorOut             final mask
     * @exception ProcessorException on error in parameters
     */
    public CheckSumModel(final int width,
			 final BigInteger xorIn,
			 final BigInteger xorOut
			 ) throws ProcessorException {
	log.fine("Creation of new CheckSumModel started");

	setWidth(width);
	setXorIn(xorIn);
	setXorOut(xorOut);

    	log.fine("Creation of new CheckSumModel completed");
    }

    /**
     * Simplified constructor, with zero masks.
     *
     * @param     width              bit-width of the checksum
     * @exception ProcessorException on error in width
     */
    public CheckSumModel(final int width) throws ProcessorException {
	this(width, BigInteger.ZERO, BigInteger.ZERO);
    }

    /**
     * Empty constructor.
     */
    public CheckSumModel() {
	log.fine("Empty CheckSumModel created");
    }
}
