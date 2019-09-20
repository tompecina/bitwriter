/* SetElement.java
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
 * Object representing a &lt;set&gt; element.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class SetElement extends VariableElement {

  // static logger
  private static final Logger log = Logger.getLogger(SetElement.class.getName());

  // processes the element
  private void process() throws ProcessorException, IOException {
    log.fine("Processing <set> element");

    final Variable variable = getOrCreateVariable(element);
    final BigInteger value = extractBigIntegerAttribute(element, "value", null, null, null, processor.getScriptProcessor());
    if (value != null) {
      variable.setValue(value);
    }
    if (element.hasAttribute("on-stream-in")) {
      final String onStreamIn = element.getAttribute("on-stream-in");
      if (onStreamIn.isEmpty()) {
        variable.setOnStreamIn(null);
      } else {
        variable.setOnStreamIn(onStreamIn);
      }
    }
    if (element.hasAttribute("on-aggregate-stream-in")) {
      final String onAggregateStreamIn = element.getAttribute("on-aggregate-stream-in");
      if (onAggregateStreamIn.isEmpty()) {
        variable.setOnAggregateStreamIn(null);
      } else {
        variable.setOnAggregateStreamIn(onAggregateStreamIn);
      }
    }
    if (element.hasAttribute("on-bitstream")) {
      final String onBitStream = element.getAttribute("on-bitstream");
      if (onBitStream.isEmpty()) {
        variable.setOnBitStream(null);
      } else {
        variable.setOnBitStream(onBitStream);
      }
    }
    if (element.hasAttribute("on-aggregate-stream-out")) {
      final String onAggregateStreamOut = element.getAttribute("on-aggregate-stream-out");
      if (onAggregateStreamOut.isEmpty()) {
        variable.setOnAggregateStreamOut(null);
      } else {
        variable.setOnAggregateStreamOut(onAggregateStreamOut);
      }
    }
    if (element.hasAttribute("on-stream-out")) {
      final String onStreamOut = element.getAttribute("on-stream-out");
      if (onStreamOut.isEmpty()) {
        variable.setOnStreamOut(null);
      } else {
        variable.setOnStreamOut(onStreamOut);
      }
    }
    if (element.hasAttribute("on-output-stream")) {
      final String onOutputStream = element.getAttribute("on-output-stream");
      if (onOutputStream.isEmpty()) {
        variable.setOnOutputStream(null);
      } else {
        variable.setOnOutputStream(onOutputStream);
      }
    }

    log.fine("<set> element processed");
  }

  // for description see Object
  @Override
  public String toString() {
    return "SetElement";
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
  public SetElement(final InputTreeProcessor processor, final Element element) throws ProcessorException, IOException {
    super(processor, element);
    log.fine("<set> element creation started");

    process();

    log.fine("<set> element set up");
  }
}
