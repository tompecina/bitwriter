/* VariableElement.java
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

import java.io.IOException;
import org.w3c.dom.Element;
import java.util.logging.Logger;

/**
 * Abstract superclass of all variable-related elements.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class VariableElement extends ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(VariableElement.class.getName());

    /**
     * Gets the variable name contained in the element's "name" attribute.
     *
     * @param     element            the element
     * @return                       name of the variable
     * @exception ProcessorException on error in parameters
     */
    protected String getVariableName(final Element element
				     ) throws ProcessorException {
	log.finer("Getting variable name from element");
	final String name = extractStringAttribute(
	    element, "name", null, processor.getScriptProcessor());
	Variable.checkVariableName(name);
	log.finer("Name: " + name);
	return name;
    }

    /**
     * Gets the variable according to the name contained in the element's
     * "name" attribute or creates it if the variable does not exist.
     *
     * @param     element            the element
     * @return                       the variable
     * @exception ProcessorException on error in parameters
     */
    protected Variable getOrCreateVariable(final Element element
					   ) throws ProcessorException {
	log.finer("Getting or creating variable from element");
	final String name = getVariableName(element);
	Variable variable = processor.getVariables().get(name);
	if (variable == null) {
	    variable = new Variable(name);
	    processor.getVariables().put(name, variable);
	}
	return variable;
    }
    
    /**
     * Gets the variable according to the name contained in the element's
     * "name" attribute.
     *
     * @param     element            the element
     * @return                       the variable or <code>null</code>
     *                               if the variable does not exist
     * @exception ProcessorException on error in parameters
     */
    protected Variable getVariable(final Element element
				   ) throws ProcessorException {
	log.finer("Getting variable from element");
	return processor.getVariables().get(getVariableName(element));
    }

    /**
     * Sets the variable type according to attributes contained in
     * the element.
     *
     * @param     variable           the variable
     * @param     element            the element
     * @param     defaultType        the default type of the variable
     * @exception ProcessorException on error in parameters
     */
    protected void setVariableType(final Variable variable,
				   final Element element,
				   final String defaultType
				   ) throws ProcessorException {
	log.finer("Setting type to variable '" + variable.getName() + "'");
	variable.setType(Variable.Type.valueOf(
	    Util.hyphensToUnderscores(extractStringArrayAttribute(
	        element,
		"type",
		VARIABLE_TYPES,
		defaultType,
		processor.getScriptProcessor()).toUpperCase())));
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "VariableElement";
    }

    /**
     * Factory method.
     *
     * @param     processor          the input tree processor object
     * @param     element            the <code>Element</code> object in
     *                               the XML file
     * @param     outerLevel         <code>true</code> if on the outer
     *                               level, <code>false</code>
     *                               if in a stream
     * @exception ProcessorException on error in parameters
     * @exception IOException        on I/O error
     */
    public static VariableElement create(final InputTreeProcessor processor,
					 final Element element,
					 final boolean outerLevel
					 ) throws ProcessorException,
						  IOException {
	log.fine("Variable element creation started");
	switch (element.getTagName()) {
	    case "set":
		return new SetElement(processor, element);
	    case "parity":
		return new ParityElement(processor, element);
	    case "sum":
		return new SumElement(processor, element);
	    case "crc":
		return new CrcElement(processor, element);
	    case "digest":
		return new DigestElement(processor, element);
	    case "show":
		return new ShowElement(processor, element);
	    case "reset":
		return new ResetElement(processor, element);
	    case "release":
		return new ReleaseElement(processor, element);
	    case "put":
		if (outerLevel) {
		    throw new ProcessorException(
		        "<put> element not allowed on the outer level");
		} else {
		    return new PutElement(processor, element);
		}
	    default:
		throw new ProcessorException(
	            "Error in input file, illegal element <" +
		    element.getTagName() + ">");
	}
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
    public VariableElement(final InputTreeProcessor processor,
			   final Element element
			   ) throws ProcessorException, IOException {
    	super(processor, element);
    	log.fine("Variable element creation started");

    	log.fine("Variable element set up");
    }
}
