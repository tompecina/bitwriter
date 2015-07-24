/* ShowElement.java
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
import org.w3c.dom.Element;
import java.util.logging.Logger;

/**
 * Object representing a &lt;show&gt; element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ShowElement extends VariableElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(ShowElement.class.getName());

    // processes the element
    private void process() throws ProcessorException, IOException {
	log.fine("Processing <show> element");

	final String variableName = element.getAttribute("name").trim();
	Variable variable = null;
	if (!variableName.isEmpty()) {
	    try {		    
		variable = getVariable(element);
	    } catch (final ProcessorException exception) {
		throw new ProcessorException(
		    "Error in input file, variable '" + variableName +
		    "' not allowed");
	    }
	    if (variable == null) {
		throw new ProcessorException(
		    "Error in input file, variable '" + variableName +
		    "' does not exist");
	    }			
	}
	BigInteger value = null;
	final String stringValue = element.getAttribute("value").trim();
	if (!stringValue.isEmpty()) {
	    value = extractBigIntegerAttribute(
	        element,
		"value",
		null,
		null,
		null,
		processor.getScriptProcessor());
	    if (value == null) {
		throw new ProcessorException(
		    "Error in input file, illegal value '" +
		    stringValue + "'");
	    }
	}
	if ((variable != null) && (value != null)) {
	    throw new ProcessorException(
	        "Error in input file," +
		" 'name' and 'value' are incompatible in <show> element");
	}
	if ((variable == null) && (value == null)) {
	    throw new ProcessorException(
	        "Error in input file, either 'name' or 'value'" +
		" must be specified in <show> element");
	}
	if (variable != null) {
	    value = variable.getValue();
	}
	if (variable != null) {
	    processor.getStderr().println(
	        String.format("%s: %s (%s)",
			      variable.getName(),
			      value.toString(),
			      Util.bigIntegerToString(value)));
	} else {
	    processor.getStderr().println(
	        String.format("%s (%s)",
			      value.toString(),
			      Util.bigIntegerToString(value)));
	}
	
	log.fine("<show> element processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "ShowElement";
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
    public ShowElement(final InputTreeProcessor processor,
		       final Element element
		       ) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("<show> element creation started");

	process();
	
	log.fine("<show> element set up");
    }
}
