/* TextElement.java
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
 * Object representing a &lt;text&gt; element
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TextElement extends ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(TextElement.class.getName());

    // processes the element
    private void process() throws ProcessorException, IOException {
	log.fine("Processing <text> element");

	final int count = extractIntegerAttribute(
	    element,
	    "repeat",
	    0,
	    null,
	    1,
	    processor.getScriptProcessor());
	final boolean trim = extractBooleanAttribute(
	    element,
	    "trim",
	    true,
	    processor.getScriptProcessor());
	final String charset = extractStringAttribute(
	    element,
	    "charset",
	    Constants.DEFAULT_CHARSET,
	    processor.getScriptProcessor());
	for (int iter = 0; iter < count; iter++) {
	    for (Node content: children(element)) {
		if (content instanceof Text) {
		    String text;
		    if (trim) {
			text = ((Text)content).getTextContent().trim();
		    } else {
			text = ((Text)content).getTextContent();
		    }
		    if (charset.equals("raw")) {
			for (int i = 0; i < text.length(); i++) {
			    write(BigInteger.valueOf(text.codePointAt(i)));
			}
		    } else {
			final byte[] bytes = text.getBytes(charset);
			for (byte b: bytes) {
			    write(BigInteger.valueOf(b & 0xff));
			}
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
	log.fine("<text> element processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "TextElement";
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
    public TextElement(final InputTreeProcessor processor,
		       final Element element
		       ) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("<text> element creation started");

	process();
	
	log.fine("<text> element set up");
    }
}
