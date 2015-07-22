/* CrcElement.java
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
import java.io.IOException;
import org.w3c.dom.Element;
import java.util.logging.Logger;

/**
 * Object representing a &lt;crc&gt; element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CrcElement extends VariableElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(CrcElement.class.getName());

    // processes the element
    private void process() throws ProcessorException, IOException {
	log.fine("Processing <crc> element");

	final Variable variable = getOrCreateVariable(element);
	variable.reset();
	setVariableType(variable, element, "stream-out");
	int width = 0;
	BigInteger polynomial = null;
	boolean reflectIn = true;
	BigInteger xorIn = BigInteger.ONE.negate();
	boolean reflectOut = true;
	BigInteger xorOut = BigInteger.ZERO;
	if (element.hasAttribute("model")) {
	    final String modelName = processor.getScriptProcessor()
		.evalAsString(element.getAttribute("model"));
	    log.fine("CRC model attribute found, loading model for: " +
		     modelName);
	    final CrcModel crcModel =
		processor.getPresetCrcModels().getExtended(modelName);
	    if (crcModel == null) {
		throw new ProcessorException("Undefined CRC model '" +
					     modelName + "'");
	    }
	    width = crcModel.getPolynomial().getWidth();
	    polynomial = crcModel.getPolynomial().getPolynomial();
	    reflectIn = crcModel.getReflectIn();
	    xorIn = crcModel.getXorIn();
	    reflectOut = crcModel.getReflectOut();
	    xorOut = crcModel.getXorOut();
	    log.fine(String.format(
	        "CRC model parameters copied from preset, width:" +
		" %d, poly: %s, reflectIn: %s, xorIn: %s, reflectOut:" +
		" %s, xorOut: %s",
		width,
		Util.bigIntegerToString(polynomial),
		reflectIn,
		Util.bigIntegerToString(xorIn),
		reflectOut,
		Util.bigIntegerToString(xorOut)));
	}
	width = extractIntAttribute(element,
				    "width",
				    1,
				    Integer.MAX_VALUE,
				    width,
				    processor.getScriptProcessor());
	polynomial = extractBigIntegerAttribute(
	    element,
	    "polynomial",
	    BigInteger.valueOf(2),
	    null, polynomial,
	    processor.getScriptProcessor());
	final Polynomial.Notation notation =
	    Polynomial.Notation.valueOf(extractStringArrayAttribute(
	        element,
		"notation",
		POLYNOMIAL_NOTATIONS,
		"normal",
		processor.getScriptProcessor()).toUpperCase());
	Polynomial polynomialObject = null;
	try {
	    polynomialObject = new Polynomial(polynomial, notation, width);
	} catch (PolynomialException exception) {
	    throw new ProcessorException(
	        "Bad polynomial, exception: " + exception.getMessage());
	}
	reflectIn = extractBooleanAttribute(element,
					    "reflect-in",
					    reflectIn,
					    processor.getScriptProcessor());
	reflectOut = extractBooleanAttribute(element,
					     "reflect-out",
					     reflectOut,
					     processor.getScriptProcessor());
	xorIn = extractBigIntegerAttribute(element,
					   "xor-in",
					   null,
					   null,
					   xorIn,
					   processor.getScriptProcessor());
	xorOut = extractBigIntegerAttribute(element,
					    "xor-out",
					    null,
					    null,
					    xorOut,
					    processor.getScriptProcessor());
	log.fine(String.format(
	    "CRC model parameters as updated from input file:" +
	    " width: %d, poly: %s, reflectIn: %s, xorIn: %s," +
	    " reflectOut: %s, xorOut: %s",
	    width,
	    Util.bigIntegerToString(polynomial),
	    reflectIn,
	    Util.bigIntegerToString(xorIn),
	    reflectOut,
	    Util.bigIntegerToString(xorOut)));
	try {
	    variable.setCalculator(new Crc(new CrcModel(
	        polynomialObject, reflectIn, xorIn, reflectOut, xorOut)));
	} catch (NumberFormatException | NullPointerException exception) {
	    throw new ProcessorException("Illegal value in CRC model");
	}
	
	log.fine("<crc> element processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "CrcElement";
    }

    /**
     * Main constructor.
     *
     * @param     processor          the input tree processor object
     * @param     element            the <code>Element</code> object in
     *                               the XML file
     * @exception ProcessorException on error in parameters
     * @exception IOException        on I/O error
     */
    public CrcElement(final InputTreeProcessor processor,
		      final Element element
		      ) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("<crc> element creation started");

	process();
	
	log.fine("<crc> element set up");
    }
}
