/* XmlParser.java
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
import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import java.util.logging.Logger;

/**
 * Validating XML parser.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class XmlParser {

  // static logger
  private static final Logger log =
    Logger.getLogger(XmlParser.class.getName());

  // validation error handler
  private static class Handler implements ErrorHandler {
    @Override
    public void warning(final SAXParseException exception
			) throws SAXException {
    }
    @Override
    public void error(final SAXParseException exception
		      ) throws SAXException {
      throw exception;
    }
    @Override
    public void fatalError(final SAXParseException exception
			   ) throws SAXException {
      throw exception;
    }
  }
    
  /**
   * Parses XML data.
   *
   * @param     inputStream        input stream containing the XML data
   *                               to be parsed
   * @param     schema             name of the XML Schema
   * @param     validate           <code>false</code> turns off XML Schema
   *                               validation (primarily for testing
   *                               purposes)
   * @return                       the parsed XML tree
   * @exception ProcessorException on parsing error
   */
  public static Document parse(final InputStream inputStream,
			       final String schema,
			       final boolean validate
			       ) throws ProcessorException {
    log.fine("Parsing input data");

    if (inputStream == null) {
      throw new ProcessorException("Null data cannot be parsed");
    }
    try {
      final DocumentBuilderFactory builderFactory = DocumentBuilderFactory
	.newInstance();
      builderFactory.setNamespaceAware(true);
      builderFactory.setCoalescing(true);
      builderFactory.setIgnoringComments(true);
      builderFactory.setIgnoringElementContentWhitespace(true);
      if (validate) {
	builderFactory.setSchema(SchemaFactory.newInstance(
	XMLConstants.W3C_XML_SCHEMA_NS_URI)
	.newSchema(new StreamSource(XmlParser.class
	.getResourceAsStream(schema))));
      }
      final DocumentBuilder builder = builderFactory.newDocumentBuilder();
      builder.setErrorHandler(new Handler());
      final Document doc = builder.parse(inputStream);
      log.fine("Parsing completed");
      return doc;
    } catch (final FactoryConfigurationError |
	     ParserConfigurationException |
	     SAXException |
	     IllegalArgumentException exception) {
      throw new ProcessorException("Invalid input file (1), exception: " +
				   exception.getMessage());
    } catch (final IOException exception) {
      throw new ProcessorException("Invalid input file (2), exception: " +
				   exception.getMessage());
    }
  }

  // default constructor disabled
  private XmlParser() {};
}
