/* PutElement.java
 *
 * Copyright (C) 2015-19, Tomas Pecina <tomas@pecina.cz>
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
 *
 * The source code is available from <https://github.com/tompecina/bitwriter>.
 */

package cz.pecina.bin.bitwriter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * Object representing a &lt;put&gt; element.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class PutElement extends VariableElement {

  // static logger
  private static final Logger log = Logger.getLogger(PutElement.class.getName());

  // processes the element
  private void process() throws ProcessorException, IOException {
    log.fine("Processing <put> element");

    final String variableName = element.getAttribute("name").trim();
    Variable variable = null;
    if (!variableName.isEmpty()) {
      try {
        variable = getVariable(element);
      } catch (final ProcessorException exception) {
        throw new ProcessorException("Error in input file, variable '" + variableName + "' not allowed");
      }
      if (variable == null) {
        throw new ProcessorException("Error in input file, variable '" + variableName + "' does not exist");
      }
    }
    BigInteger value = null;
    final String stringValue = element.getAttribute("value").trim();
    if (!stringValue.isEmpty()) {
      value = extractBigIntegerAttribute(element, "value", null, null, null, processor.getScriptProcessor());
      if (value == null) {
        throw new ProcessorException("Error in input file, illegal value '" + stringValue + "'");
      }
    }
    if ((variable != null) && (value != null)) {
      throw new ProcessorException("Error in input file, 'name' and 'value' are incompatible in <put> element");
    }
    if ((variable == null) && (value == null)) {
      throw new ProcessorException("Error in input file, either 'name' or 'value' must be specified in <put> element");
    }
    if (variable != null) {
      value = variable.getValue();
    }
    final String type = extractStringAttribute(element, "type", "stream-in", processor.getScriptProcessor());
    if (variable != null) {
      log.finest("Putting variable '" + variable.getName() + "', type '" + type + "', value: "
          + Util.bigIntegerToString(value));
      if (extractBooleanAttribute(element, "reset", true, processor.getScriptProcessor())) {
        variable.reset();
      }
    } else {
      log.finest("Putting value: " + Util.bigIntegerToString(value));
    }
    switch (type) {
      case "stream-in":
        processor.getInStream().write(value);
        break;
      case "aggregate-stream-in":
        processor.getInAggregateStream().write(value);
        break;
      case "bitstream":
        processor.getBitStream().write(value);
        break;
      case "aggregate-stream-out":
        processor.getOutAggregateStream().write(value);
        break;
      case "stream-out":
        processor.getOutStream().write(value);
        break;
      case "output-stream":
        processor.getControlledOutputStream().write(value);
        break;
      default:
        throw new ProcessorException("Error in input file, <put> variable has wrong type: '" + type + "'");
    }

    log.fine("<put> element processed");
  }

  // for description see Object
  @Override
  public String toString() {
    return "PutElement";
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
  public PutElement(final InputTreeProcessor processor, final Element element) throws ProcessorException, IOException {
    super(processor, element);
    log.fine("<put> element creation started");

    process();

    log.fine("<put> element set up");
  }
}
