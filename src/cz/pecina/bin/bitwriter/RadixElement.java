/* RadixElement.java
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
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;
import java.util.logging.Logger;

/**
 * Object representing a radix element, ie, &lt;hex&gt;, &lt;dec&gt;,
 * &lt;oct&gt;, &lt;bin&gt; or &lt;bits&gt;.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class RadixElement extends ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(RadixElement.class.getName());

    // processes the element
    private void process(final int radix
			 ) throws ProcessorException, IOException {
	log.fine("Processing radix element, radix: " + radix);
	if ((radix != 16) &&
	    (radix != 10) &&
	    (radix != 8) &&
	    (radix != 2) &&
	    (radix != 0)) {
	    throw new ProcessorException("Illegal radix: " + radix);
	}
	final int count = extractIntAttribute(
	    element,
	    "repeat",
	    0,
	    Integer.MAX_VALUE,
	    1,
	    processor.getScriptProcessor());
	for (int iter = 0; iter < count; iter++) {
	    for (Content content: consolidate(element.getContent())) {
		if (content instanceof Text) {
		    final String trimmedText =
			((Text)content).getTextTrim();
		    if (trimmedText.length() == 0) {
			continue;
		    }
		    if (radix != 0) {
			for (String split: new ScriptLine(trimmedText)) {
			    if (ScriptProcessor.isScript(split)) {
				final BigInteger value =
				    processor.getScriptProcessor()
				    .evalAsBigInteger(split);
				write(value);
			    } else if (radix == 16) {
				if ((split.length() % 2) != 0) {
				    throw new ProcessorException(
				        "Error in input file, illegal" +
					" hex string: " + split);
				}
				for (int i = 0; i < split.length(); i += 2) {
				    try {
					write(new BigInteger(split.substring(
					    i,
					    (i + 2)),
					    radix));
				    } catch (NumberFormatException |
					     NullPointerException exception) {
					throw new ProcessorException(
				            "Illegal number format (1): " +
					    split);
				    }
				}
			    } else {
				try {
				    write(new BigInteger(split, radix));
				} catch (NumberFormatException |
					 NullPointerException exception) {
				    throw new ProcessorException(
				        "Illegal number format (2): " +
					split);
				}
			    }
			}
		    } else {
			for (String split:
			     new ScriptLine(trimmedText, false)) {
			    final BigInteger value =
				processor.getScriptProcessor()
				.evalAsBigInteger(split);
			    final boolean isScript =
				ScriptProcessor.isScript(split);
			    if (!isScript || (value != null)) {
				if (value.compareTo(BigInteger.ZERO)
				    == 0) {
				    write(value);
				} else if (value.compareTo(BigInteger.ONE)
					   == 0) {
				    write(value);
				} else {
				    if (isScript) {
					throw new ProcessorException(
					    "Script returned illegal" +
					    " bit value: " + value);
				    } else {
					throw new ProcessorException(
				            "Error in input file," +
					    " illegal character in <bits>");
				    }
				}
			    }
			}
		    }
		} else if (content instanceof Element) {
		    final Element innerElement = (Element)content;
		    switch (innerElement.getName()) {
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
	log.fine("Radix element processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "RadixElement";
    }

    /**
     * Main constructor.
     *
     * @param     processor          the input tree processor object
     * @param     element            the <code>Element</code> object in
     *                               the XML file
     * @param     radix              the radix (16, 10, 8, 2 or 0
     *                               for &lt;bits&gt;)
     * @exception ProcessorException on error in parameters
     * @exception IOException        on I/O error
     */
    public RadixElement(final InputTreeProcessor processor,
			final Element element,
			final int radix
			) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("Radix element creation started");

	process(radix);
	
	log.fine("Radix element set up");
    }
}
