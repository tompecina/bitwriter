/* PresetCrcModels.java
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

import java.util.ArrayList;
import java.math.BigInteger;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.XMLConstants;
import org.xml.sax.SAXException;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.logging.Logger;

/**
 * Preset CRC models, normally fetched from a file before processing.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PresetCrcModels extends ArrayList<CrcModel> {

    // static logger
    private static final Logger log =
	Logger.getLogger(PresetCrcModels.class.getName());

    // XML file version
    private static final String CRC_XML_FILE_VERSION = "1.0";

    /**
     * Generates a lists of all models and sends it to a print stream.
     *
     * @param printStream the print stream
     */
    public void list(final PrintStream printStream) {
	for (CrcModel model: this) {
	    printStream.print(String.format("ID: %s Alias/es:",
					    model.getId()));
	    String joiner = " ";
	    for (String name: model.getNames()) {
		printStream.print(joiner + name);
		joiner = ",";
	    }
	    if (model.getDescription() != null) {
		printStream.println(" Description: " + model.getDescription());
	    }
	    printStream.println(String.format(
	        " Width: %d Poly: %s RefIn: %s XorIn:" +
		" %s RefOut: %s XorOut: %s",
		model.getPolynomial().getWidth(),
		Util.bigIntegerToString(model.getPolynomial().getPolynomial()),
		Util.TF(model.getReflectIn()),
		Util.bigIntegerToString(model.getXorIn()),
		Util.TF(model.getReflectOut()),
		Util.bigIntegerToString(model.getXorOut())));
	}
    }

    /**
     * Finds a CRC model according to ID or name (alias).
     *
     * @param  name the search string
     * @return      the CRC model or <code>null</code> if not found 
     */
    public CrcModel getExtended(final String name) {
	final String trimmedName = name.trim();
	for (CrcModel model: this) {
	    if (model.getId().equalsIgnoreCase(trimmedName)) {
		return model;
	    }
	    for (String alias: model.getNames()) {
		if (alias.equalsIgnoreCase(trimmedName)) {
		    return model;
		}
	    }
	}
	return null;
    }

    // processes the file
    private void process(final String crcModelsString
			 ) throws PresetCrcModelsException {
	log.fine("Parsing started");

	try {
	    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
		.newSchema(new StreamSource(getClass()
		.getResourceAsStream("crc-" + CRC_XML_FILE_VERSION +
		".xsd"))).newValidator().validate(new StreamSource(
		new StringReader(crcModelsString)));
	} catch (SAXException | IOException exception) {
	    throw new PresetCrcModelsException(
	        "Error in CRC models file (1), exception: " +
		exception.getMessage());
	}
	Document doc;
	try {
	    doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		  .parse(new ByteArrayInputStream(
		  crcModelsString.getBytes()));
	} catch (FactoryConfigurationError |
		 ParserConfigurationException |
		 SAXException |
		 IllegalArgumentException exception) {
	    throw new PresetCrcModelsException(
	        "Error in CRC models file (2), exception: " +
		exception.getMessage());
	} catch (IOException exception) {
	    throw new PresetCrcModelsException(
	        "Error in CRC models file (3), exception: " +
		exception.getMessage());
	}
	final Element crcElement = doc.getDocumentElement();
	if (!crcElement.getTagName().equals("crc")) {
	    throw new PresetCrcModelsException(
	        "Error in CRC models file, no <crc> tag");
	}
	if (!CRC_XML_FILE_VERSION.equals(
	     crcElement.getAttribute("version"))) {
	    throw new PresetCrcModelsException(
	        "Error in CRC models file, version mismatch");
	}
	final NodeList modelElements =
	    crcElement.getElementsByTagName("model");
	for (int i = 0; i < modelElements.getLength(); i++) {
	    final Element modelElement = (Element)(modelElements.item(i));
	    log.finer("Processing model: " + modelElement.getAttribute("id"));
	    try {
		final CrcModel model = new CrcModel();
		model.setId(modelElement.getAttribute("id"));
		final NodeList nameElements =
		    modelElement.getElementsByTagName("name");
		for (int j = 0; j < nameElements.getLength(); j++) {
		    final Element nameElement =
			(Element)(nameElements.item(j));
		    log.finest("Processing name: " +
			       nameElement.getTextContent().trim());
		    model.addName(nameElement.getTextContent().trim());
		}
		final Element descriptionElement =
		    (Element)(modelElement.getElementsByTagName("description")
		    .item(0));
		if (descriptionElement != null) {
		    model.setDescription(descriptionElement.getTextContent()
		        .trim());
		}
		final Element polynomialElement =
		    (Element)(modelElement.getElementsByTagName("polynomial")
		    .item(0));
		final BigInteger polynomial =
		    new BigInteger(polynomialElement.getTextContent().trim(),
				   16);
		log.finer("Polynomial: " +
			  Util.bigIntegerToString(polynomial));
		final Polynomial.Notation notation =
		    Polynomial.Notation.valueOf(
		    ParsedElement.extractStringArrayAttribute(
		        polynomialElement,
			"notation",
			ParsedElement.POLYNOMIAL_NOTATIONS,
			null,
			null).toUpperCase());
		if (notation == null) {
		    throw new PresetCrcModelsException(
		        "Notation must be specified in" +
			" preset CRC models file");
		}
		int width = 0;
		final String stringWidth =
		    modelElement.getElementsByTagName("width").item(0)
		    .getTextContent().trim();
		if (stringWidth != null) {
		    width = Integer.parseInt(stringWidth);
		}
		model.setPolynomial(
		    new Polynomial(polynomial, notation, width));
		model.setReflectIn(Util.stringToBoolean(modelElement
		    .getElementsByTagName("reflect-in").item(0)
		    .getTextContent().trim()));
		model.setReflectOut(Util.stringToBoolean(modelElement
		    .getElementsByTagName("reflect-out").item(0)
		    .getTextContent().trim()));
		model.setXorIn(new BigInteger(modelElement
		    .getElementsByTagName("xor-in").item(0)
		    .getTextContent().trim(), 16));
		model.setXorOut(new BigInteger( modelElement
		    .getElementsByTagName("xor-out").item(0)
		    .getTextContent().trim(), 16));
		final Element checkElement =
		    (Element)(modelElement.getElementsByTagName("check")
		    .item(0));
		if (checkElement != null) {
		    model.setCheck(new BigInteger(checkElement
		        .getTextContent().trim(), 16));
		}
		add(model);
		if (model.hasCheck()) {
		    log.finest("Checking the value for: " +
			       model.getId());
		    final Crc c = new Crc(model);
		    c.update("123456789".getBytes());
		    if (c.getRegister().equals(model.getCheck())) {
			log.finest("Check passed");
		    } else {
			throw new PresetCrcModelsException(
			    "Model '" + model.getId() +
			    "' failed integrity check");
		    }
		}	
		log.finer("Read: " + model.getId());
	    } catch (ProcessorException |
		     PolynomialException |
		     NumberFormatException |
		     NullPointerException exception) {
		throw new PresetCrcModelsException(
		    "Error in CRC models file (7), exception: " +
		    exception.getMessage());
	    }
	}
	
	log.fine("Parsing completed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "PresetCrcModels";
    }

    /**
     * Main constructor.
     *
     * @param     crcModelsString          string containing the 
     *                                     models XML file
     * @exception PresetCrcModelsException on errors in the models file
     */
    public PresetCrcModels(final String crcModelsString
			   ) throws PresetCrcModelsException {
	log.fine("Reading CRC model presets from an XML string");

	process(crcModelsString);

	log.fine("CRC model presets set up");
    }
}
