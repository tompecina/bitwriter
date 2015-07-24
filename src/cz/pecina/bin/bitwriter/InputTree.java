/* InputTree.java
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

import javax.xml.XMLConstants;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.logging.Logger;

/**
 * Input XML tree parser.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class InputTree {

    // static logger
    private static final Logger log =
	Logger.getLogger(InputTree.class.getName());

    // fields
    protected Element rootElement;
    
    /**
     * Gets the root element of the parsed tree.
     */
    public Element getRootElement() {
	log.finer("Getting root element");
	return rootElement;
    }

    // for description see Object
    @Override
    public String toString() {
	return "InputTree";
    }

    /**
     * Main constructor.
     *
     * @param     inputString        the string containing the XML tree
     *                               to be parsed
     * @param     validate           <code>false</code> turns off XML Schema
     *                               validation (primarily for testing
     *                               purposes)
     * @exception ProcessorException on parsing error
     */
    public InputTree(final String inputString,
		     final boolean validate
		     ) throws ProcessorException {
	log.fine("Parsing input string");

	if (inputString == null) {
	    throw new ProcessorException("Null string cannot be parsed");
	}
	if (validate) {
	    try {
		SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
		    .newSchema(new StreamSource(InputTree.class
		    .getResourceAsStream("bin-" +
		    Constants.FILE_XML_FILE_VERSION +
		    ".xsd"))).newValidator()
		    .validate(new StreamSource(
		    new StringReader(inputString)));
	    } catch (final SAXException | IOException exception) {
		throw new ProcessorException(
		    "Invalid input file (1), exception: " +
		    exception.getMessage());
	    }
	}
	Document doc;
	try {
	    doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		  .parse(new ByteArrayInputStream(inputString.getBytes()));
	} catch (final FactoryConfigurationError |
		 ParserConfigurationException |
		 SAXException |
		 IllegalArgumentException exception) {
	    throw new ProcessorException(
	        "Invalid input file (2), exception: " +
		exception.getMessage());
	} catch (final IOException exception) {
	    throw new ProcessorException(
	        "Invalid input file (3), exception: " +
		exception.getMessage());
	}
	rootElement = doc.getDocumentElement();
	if (!rootElement.getTagName().equals("file")) {
	    throw new ProcessorException(
	        "Invalid input file, no <file> tag");
	}
	if (!Constants.FILE_XML_FILE_VERSION.equals(
	        rootElement.getAttribute("version"))) {
	    throw new ProcessorException(
	        "Invalid input file, version mismatch");
	}
	log.fine("Parsing completed");
    }
}
