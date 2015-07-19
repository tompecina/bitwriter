/* CrcModel.java
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

import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * CRC model.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CrcModel {

    // static logger
    private static final Logger log =
	Logger.getLogger(CrcModel.class.getName());

    // fields
    protected String id;
    protected List<String> names = new ArrayList<>();
    protected String description;
    protected Polynomial polynomial;
    protected boolean reflectIn;
    protected BigInteger xorIn;
    protected boolean reflectOut;
    protected BigInteger xorOut;
    protected BigInteger check;

    /**
     * Sets the model ID.
     *
     * @param     id                 ID of the model
     * @exception ProcessorException if invalid <code>id</code> supplied
     */
    public void setId(final String id) throws ProcessorException {
	log.finer("Setting ID to: " + id);
	if ((id == null) || id.isEmpty()) {
	    throw new ProcessorException(
	        "Null or empty string not allowed for CRC model ID");
	}
	this.id = id;
    }
    
    /**
     * Gets the model ID.
     *
     * @return ID of the model
     */
    public String getId() {
	log.finer("Getting ID: " + id);
	return id;
    }
    
    /**
     * Adds a new name (alias) of the model.
     *
     * @param     name               name (alias) of the model
     * @exception ProcessorException if invalid <code>name</code> supplied
     */
    public void addName(final String name) throws ProcessorException {
	log.finer("Adding name: " + name);
	if ((name == null) || name.isEmpty()) {
	    throw new ProcessorException(
	        "Null or empty string not allowed for CRC model name");
	}
	names.add(name);
    }
    
    /**
     * Gets the list of model names (aliases).
     *
     * @return list of model names
     */
    public List<String> getNames() {
	log.finest("Getting names: " + names);
	return names;
    }
    
    /**
     * Sets the (optional) model description.
     *
     * @param     description        description of the model
     * @exception ProcessorException if invalid <code>description</code>
     *                               supplied
     */
    public void setDescription(final String description
			       ) throws ProcessorException {
	log.finer("Setting description to: " + description);
	if ((description == null) || description.isEmpty()) {
	    throw new ProcessorException(
	        "Null or empty string not allowed for CRC model description");
	}
	this.description = description;
    }
    
    /**
     * Gets the (optional) model description.
     *
     * @return model description
     */
    public String getDescription() {
	log.finest("Getting description: " + description);
	return description;
    }
    
    /**
     * Sets the generator polynomial of the model.
     *
     * @param     polynomial         the generator polynomial of the model
     * @exception ProcessorException if <code>null</code> polynomial supplied
     */
    public void setPolynomial(final Polynomial polynomial
			      ) throws ProcessorException {
	log.finer("Setting polynomial object, polynomial: " +
		  Util.bigIntegerToString(polynomial.getPolynomial()) +
		  ", width: " + polynomial.getWidth());
	if (polynomial == null) {
	    throw new ProcessorException("Null not allowed for CRC polynomial");
	}
	this.polynomial = polynomial;
    }
    
    /**
     * Gets the generator polynomial of the model.
     *
     * @return the generator polynomial of the model
     */
    public Polynomial getPolynomial() {
	log.finest("Getting polynomial object, polynomial: " +
		   Util.bigIntegerToString(polynomial.getPolynomial()) +
		   ", width: " + polynomial.getWidth());
	return polynomial;
    }
    
    /**
     * Sets the input reflection of the model.
     *
     * @param reflectIn the input reflection of the model
     */
    public void setReflectIn(final boolean reflectIn) {
	log.finer("Setting reflectIn to: " + reflectIn);
	this.reflectIn = reflectIn;
    }
    
    /**
     * Gets the input reflection of the model.
     *
     * @return the input reflection of the model
     */
    public boolean getReflectIn() {
	log.finest("Getting reflectIn: " + reflectIn);
	return reflectIn;
    }
    
    /**
     * Sets the inital mask of the model.
     *
     * @param     xorIn              the inital mask of the model
     * @exception ProcessorException if <code>null</code> supplied
     */
    public void setXorIn(final BigInteger xorIn) throws ProcessorException {
	log.finer("Setting xorIn to: " + Util.bigIntegerToString(xorIn));
	if (xorIn == null) {
	    throw new ProcessorException("Null not allowed for CRC xorIn");
	}
	this.xorIn = xorIn;
    }
    
    /**
     * Gets the inital mask of the model.
     *
     * @return the inital mask of the model
     */
    public BigInteger getXorIn() {
	log.finest("Getting xorIn: " + Util.bigIntegerToString(xorIn));
	return xorIn;
    }
    
    /**
     * Sets the output reflection of the model.
     *
     * @param reflectOut the output reflection of the model
     */
    public void setReflectOut(final boolean reflectOut) {
	log.finer("Setting reflectOut to: " + reflectOut);
	this.reflectOut = reflectOut;
    }
    
    /**
     * Gets the output reflection of the model.
     *
     * @return the output reflection of the model
     */
    public boolean getReflectOut() {
	log.finest("Getting reflectOut: " + reflectOut);
	return reflectOut;
    }
    
    /**
     * Sets the final mask of the model.
     *
     * @param     xorOut             the final mask of the model
     * @exception ProcessorException if <code>null</code> supplied
     */
    public void setXorOut(final BigInteger xorOut) throws ProcessorException {
	log.finer("Setting xorOut to: " + Util.bigIntegerToString(xorOut));
	if (xorOut == null) {
	    throw new ProcessorException("Null not allowed for CRC xorOut");
	}
	this.xorOut = xorOut;
    }

    /**
     * Gets the final mask of the model.
     *
     * @return the final mask of the model
     */
    public BigInteger getXorOut() {
	log.finest("Getting xorOut: " + Util.bigIntegerToString(xorOut));
	return xorOut;
    }
    
    /**
     * Sets the (optional) check value of the model.
     *
     * The check value is the CRC calculated on "123456789" in ASCII
     * (i.e., {0x30, 0x31,... 0x39}).
     *
     * @param     check              the (optional) check value of the model
     * @exception ProcessorException if <code>null</code> supplied
     */
    public void setCheck(final BigInteger check) throws ProcessorException {
	log.finer("Setting check to: " + Util.bigIntegerToString(check));
	if (check == null) {
	    throw new ProcessorException(
	        "Null not allowed for CRC check value");
	}
	this.check = check;
    }

    /**
     * Checks for the presence of the check value.
     *
     * The check value is the CRC calculated on "123456789" in ASCII
     * (i.e., {0x30, 0x31,... 0x39}).
     *
     * @return <code>true</code> if the check value is available
     */
    public boolean hasCheck() {
	log.finer("Checking for presence of check: " + (check != null));
	return (check != null);
    }
    
    /**
     * Gets of the check value.
     *
     * The check value is the CRC calculated on "123456789" in ASCII
     * (i.e., {0x30, 0x31,... 0x39}).
     *
     * @return the check value
     */
    public BigInteger getCheck() {
	log.finer("Getting check: " + Util.bigIntegerToString(check));
	return check;
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "CrcModel";
    }

    /**
     * Main constructor.
     *
     * @param     polynomial         generator polynomial of the model
     * @param     reflectIn          input reflection parameter of the model
     * @param     xorIn              initial mask
     * @param     reflectOut         output reflection parameter of the model
     * @param     xorOut             final mask
     * @exception ProcessorException on error in parameters
     */
    public CrcModel(final Polynomial polynomial,
		    final boolean reflectIn,
		    final BigInteger xorIn,
		    final boolean reflectOut,
		    final BigInteger xorOut
		    ) throws ProcessorException {
	log.fine(String.format(
	    "CRC model called, with width: %d, polynomial: %s" +
	    ", reflectIn: %s, xorIn: %s, reflectOut: %s, xorOut: %s",
	    polynomial.getWidth(),
	    Util.bigIntegerToString(polynomial.getPolynomial()),
	    reflectIn,
	    Util.bigIntegerToString(xorIn),
	    reflectOut,
	    Util.bigIntegerToString(xorOut)));

	setPolynomial(polynomial);
	setReflectIn(reflectIn);
	setXorIn(xorIn);
	setReflectOut(reflectOut);
	setXorOut(xorOut);

	log.fine("Creation of new CRC model completed");
    }

    /**
     * Simplified constructor, with <code>true</code> reflections and
     * <code>~0</code> inital mask.
     *
     * @param     polynomial         generator polynomial of the model
     * @exception ProcessorException on error in width
     */
    public CrcModel(final Polynomial polynomial) throws ProcessorException {
	this(polynomial, true, BigInteger.ZERO.not(), true, BigInteger.ZERO);
    }

    /**
     * Empty constructor.
     */
    public CrcModel() {
 	log.fine("Empty CRC model created");
   }
}
