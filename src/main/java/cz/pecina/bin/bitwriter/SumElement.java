/* SumElement.java
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
 * Object representing a &lt;sum&gt; element.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class SumElement extends VariableElement {

  // static logger
  private static final Logger log = Logger.getLogger(SumElement.class.getName());

  // processes the element
  private void process() throws ProcessorException, IOException {
    log.fine("Processing <sum> element");

    final Variable variable = getOrCreateVariable(element);
    variable.reset();
    setVariableType(variable, element, "stream-out");
    final int width = extractIntegerAttribute(element, "width", 1, Integer.MAX_VALUE, 8, processor.getScriptProcessor());
    if (width < 1) {
      throw new ProcessorException("Illegal checksum width: " + width);
    }
    final BigInteger xorIn = extractBigIntegerAttribute(
        element, "xor-in", null, null, BigInteger.ZERO, processor.getScriptProcessor());
    final BigInteger xorOut = extractBigIntegerAttribute(
        element, "xor-out", null, null, BigInteger.ZERO, processor.getScriptProcessor());
    variable.setCalculator(new CheckSum(new CheckSumModel(width, xorIn, xorOut)));

    log.fine("<sum> element processed");
  }

  // for description see Object
  @Override
  public String toString() {
    return "SumElement";
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
  public SumElement(final InputTreeProcessor processor, final Element element) throws ProcessorException, IOException {
    super(processor, element);
    log.fine("<sum> element creation started");

    process();

    log.fine("<sum> element set up");
  }
}
