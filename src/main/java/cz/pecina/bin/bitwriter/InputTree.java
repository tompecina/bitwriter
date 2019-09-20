/* InputTree.java
 *
 * Copyright (C) 2015-19, Tomáš Pecina <tomas@pecina.cz>
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

import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.logging.Logger;

/**
 * Input XML tree parser.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class InputTree {

  // static logger
  private static final Logger log =
    Logger.getLogger(InputTree.class.getName());

  // fields
  protected Element rootElement;
    
  /**
   * Gets the root element of the parsed tree.
   *
   * @return the root element
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
   * @param     inputStream        input stream containing the XML data
   *                               to be parsed
   * @exception ProcessorException on parsing error
   */
  public InputTree(final InputStream inputStream
		   ) throws ProcessorException {
    log.fine("Parsing input data");

    final Document doc = XmlParser.parse(
      inputStream,
      "bin-" + Constants.FILE_XML_FILE_VERSION + ".xsd",
      true);
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
