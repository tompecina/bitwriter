/* FloatElement.java
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

import java.math.BigInteger;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import java.util.logging.Logger;

/**
 * Object representing a floating-point element, ie, &lt;float&gt;
 * or &lt;double&gt;.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class FloatElement extends ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(FloatElement.class.getName());

    // processes the element
    private void process(final boolean isDouble
			 ) throws ProcessorException, IOException {
	log.fine("Processing floating-point element,");
	final int count = extractIntegerAttribute(element,
					      "repeat",
					      0,
					      null,
					      1,
					      processor.getScriptProcessor());
	for (int iter = 0; iter < count; iter++) {
	    for (Node content: children(element)) {
		if (content instanceof Text) {
		    final String trimmedText = ((Text)content)
			.getTextContent().trim();
		    if (trimmedText.isEmpty()) {
			continue;
		    }
		    for (String split: new ScriptLine(trimmedText)) {
			log.finer("Processing segment: " + split);
			final BigInteger value;
			if (ScriptProcessor.isScript(split)) {
			    if (isDouble) {
				value = BigInteger.valueOf(
				    Double.doubleToLongBits(
				    processor.getScriptProcessor()
				    .evalAsDouble(split)));
			    } else {
				value = BigInteger.valueOf(
				    Float.floatToIntBits(
				    processor.getScriptProcessor()
				    .evalAsFloat(split)));
			    }
			} else {
			    try {
				if (isDouble) {
				    value = BigInteger.valueOf(
				        Double.doubleToLongBits(
					Util.stringToDouble(split)));
				} else {
				    value = BigInteger.valueOf(
				        Float.floatToIntBits(
					Util.stringToFloat(split)));
				}
			    } catch (final NumberFormatException |
				     NullPointerException exception) {
				throw new ProcessorException(
				    "Illegal number format (2): " + split);
			    }
			}
			write(value);
		    }
		} else if (content instanceof Element) {
		    final Element innerElement = (Element)content;
		    switch (innerElement.getTagName()) {
			case "flush":
			    new FlushElement(processor, innerElement);
			    break;
			case "script":
			    new ScriptElement(processor, innerElement);
			    break;
			default:
			    VariableElement.create(processor,
						   innerElement,
						   false);
			    break;
		    }
		}
	    }
	}
	log.fine("Floating-point element processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "FloatElement";
    }

    /**
     * Main constructor.
     *
     * @param     processor          the input tree processor object
     * @param     element            the <code>Element</code> object in
     *                               the XML file
     * @param     isDouble           <code>true</code> for &lt;double&gt;,
     *                               <code>false</code> for &lt;float&gt;
     * @exception ProcessorException on error in parameters
     * @exception IOException        on I/O error
     */
    public FloatElement(final InputTreeProcessor processor,
			final Element element,
			final boolean isDouble
			) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("Floating-point element creation started");

	process(isDouble);
	
	log.fine("Floating-point element set up");
    }
}
