/* ScriptElement.java
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

import org.w3c.dom.Element;
import java.util.logging.Logger;

/**
 * Object representing a &lt;script&gt; element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ScriptElement extends ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(ScriptElement.class.getName());

    // processes the element
    private void process() throws ProcessorException {
	log.fine("Processing <script> element");

	final int count = extractIntAttribute(
	    element,
	    "repeat",
	    0,
	    Integer.MAX_VALUE,
	    1,
	    processor.getScriptProcessor());
	for (int iter = 0; iter < count; iter++) {
	    processor.getScriptProcessor().eval(element
	        .getTextContent().trim());
	}
	log.fine("<script> element processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "ScriptElement";
    }

    /**
     * Main constructor.
     *
     * @param     processor          the input tree processor object
     * @param     element            the <code>Element</code> object in
     *                               the XML file
     * @exception ProcessorException on error in parameters
     */
    public ScriptElement(final InputTreeProcessor processor,
			 final Element element
			 ) throws ProcessorException {
	super(processor, element);
	log.fine("<script> element creation started");

	process();
	
	log.fine("<script> element set up");
    }
}
