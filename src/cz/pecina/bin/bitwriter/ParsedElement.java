/* ParsedElement.java
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
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import java.util.logging.Logger;

/**
 * Abstract class representing all XML elements.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(ParsedElement.class.getName());

    /**
     * String array of all variable types.
     */
    public static final String[] VARIABLE_TYPES = {
	"stream-in", "aggregate-stream-in", "bitstream",
	"aggregate-stream-out", "stream-out", "output-stream"};
    /**
     * String array of all party models.
     */
    public static final String[] PARITY_MODELS = {"even", "odd"};

    /**
     * String array of all polynomial notations.
     */
    public static final String[] POLYNOMIAL_NOTATIONS = {
	"normal", "full", "reversed", "koopman"};

    /**
     * String array of all endianness types.
     */
    public static final String[] ENDIANESS_TYPES = {"big", "little"};

    // fields
    protected InputTreeProcessor processor;
    protected Element element;

    /**
     * Gets all element children of an element as a list.
     *
     * @param  element the element to be processed
     * @return         list of children of the element
     */
    public static List<Element> getChildren(final Element element) {
	final List<Element> list = new ArrayList<>();
	final NodeList nodes = element.getChildNodes();
	for (int i = 0; i < nodes.getLength(); i++) {
	    if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
		list.add((Element)(nodes.item(i)));
	    }
	}
	return list;
    }
    
    /**
     * Consolidates adjacent text nodes. Element breaks a token, comments,
     * PIs etc. do not.
     *
     * @param  element element to be processed
     * @return         list of children <code>Node</code>s
     */
    public static List<Node> children(final Element element) {
	final List<Node> list = new ArrayList<>();
	final NodeList nodes = element.getChildNodes();
	for (int i = 0; i < nodes.getLength(); i++) {
	    list.add(nodes.item(i));
	}
	return list;
    }

    /**
     * Extracts integer value from an attribute, evaluating scripts.
     * These must be properly marked.
     *
     * @param     element            element to be processoed
     * @param     attributeName      name of the attribute
     * @param     minValue           minimum value (posibly
     *                               <code>Integer.MIN_VALUE</code>)
     * @param     maxValue           maximum value
     *                               (posibly <code>Integer.MAX_VALUE</code>)
     * @param     defaultValue       default value if attribute not present
     *                               (empty attribute is an error)
     * @param     scriptProcessor    instance of the script processor
     *                               to be used
     * @return                       integer value of the attribute
     * @exception ProcessorException if the string does not represent a valid
     *                               integer value or on error in the script
     */
    public static int extractIntAttribute(
        final Element element,
	final String attributeName,
	final int minValue,
	final int maxValue,
	final int defaultValue,
	final ScriptProcessor scriptProcessor
	) throws ProcessorException {
	if (!element.hasAttribute(attributeName)) {
	    return defaultValue;
	}
	int r = 0;
	final String s = element.getAttribute(attributeName);
	if (scriptProcessor != null) {
	    r = scriptProcessor.evalAsInt(s);
	} else {
	    r = Util.stringToInt(s);
	}
	if ((r < minValue) || (r > maxValue)) {
	    throw new ProcessorException(
	        "Attribute '" + attributeName + "' out of range");
	}
	return r;
    }

    /**
     * Extracts long value from an attribute, evaluating scripts.
     * These must be properly marked.
     *
     * @param     element            element to be processoed
     * @param     attributeName      name of the attribute
     * @param     minValue           minimum value
     *                               (posibly <code>Long.MIN_VALUE</code>)
     * @param     maxValue           maximum value
     *                               (posibly <code>Long.MAX_VALUE</code>)
     * @param     defaultValue       default value if attribute not present
     *                               (empty attribute is an error)
     * @param     scriptProcessor    instance of the script processor
     *                               to be used
     * @return                       long value of the attribute
     * @exception ProcessorException if the string does not represent a valid
     *                               long value or on error in the script
     */
    public static long extractLongAttribute(
        final Element element,
	final String attributeName,
	final long minValue,
	final long maxValue,
	final long defaultValue,
	final ScriptProcessor scriptProcessor
	) throws ProcessorException {
	if (!element.hasAttribute(attributeName)) {
	    return defaultValue;
	}
	long r = 0;
	final String s = element.getAttribute(attributeName);
	if (scriptProcessor != null) {
	    r = scriptProcessor.evalAsLong(s);
	} else {
	    r = Util.stringToLong(s);
	}
	if ((r < minValue) || (r > maxValue)) {
	    throw new ProcessorException(
	        "Attribute '" + attributeName + "' out of range");
	    }
	return r;
    }

    /**
     * Extracts BigInteger value from an attribute, evaluating scripts.
     * These must be properly marked.
     *
     * @param     element            element to be processoed
     * @param     attributeName      name of the attribute
     * @param     minValue           minimum value
     *                               (<code>null</code> if none)
     * @param     maxValue           maximum value
     *                               (<code>null</code> if none)
     * @param     defaultValue       default value if attribute not present
     *                               (empty attribute is an error)
     * @param     scriptProcessor    instance of the script processor
     *                               to be used
     * @return                       BigInteger value of the attribute
     * @exception ProcessorException if the string does not represent a valid
     *                               BigInteger value or on error in the script
     */
    public static BigInteger extractBigIntegerAttribute(
        final Element element,
	final String attributeName,
	final BigInteger minValue,
	final BigInteger maxValue,
	final BigInteger defaultValue,
	final ScriptProcessor scriptProcessor
	) throws ProcessorException {
	log.finest(String.format(
	    "extractBigIntegerAttribute called, for element: %s," +
	    " attribute: %s, minValue: %s, maxValue: %s, defaultValue: %s",
	    element, attributeName, minValue, maxValue, defaultValue));
	if (!element.hasAttribute(attributeName)) {
	    return defaultValue;
	}
	BigInteger r;
	final String s = element.getAttribute(attributeName);
	if (scriptProcessor != null) {
	    r = scriptProcessor.evalAsBigInteger(s);
	} else {
	    r = Util.stringToBigInteger(s);
	}
	if (((minValue != null) && (r.compareTo(minValue) < 0)) ||
	    ((maxValue != null) && (r.compareTo(maxValue) > 0))) {
	    throw new ProcessorException(
	        "Attribute '" + attributeName + "' out of range");
	}
	log.finest("extractBigIntegerAttribute returns: " + r);
	return r;
    }

    /**
     * Extracts boolean value from an attribute, evaluating scripts.
     * These must be properly marked.
     *
     * @param     element            element to be processoed
     * @param     attributeName      name of the attribute
     * @param     defaultValue       default value if attribute not present
     *                               (empty attribute is an error)
     * @param     scriptProcessor    instance of the script processor
     *                               to be used
     * @return                       boolean value of the attribute
     * @exception ProcessorException if the string does not represent a valid
     *                               integer value or on error in the script
     */
    public static boolean extractBooleanAttribute(
        final Element element,
	final String attributeName,
	final boolean defaultValue,
	final ScriptProcessor scriptProcessor
	) throws ProcessorException {
	if (!element.hasAttribute(attributeName)) {
	    return defaultValue;
	}
	boolean r = false;
	String s = element.getAttribute(attributeName);
	if (scriptProcessor != null) {
	    s = scriptProcessor.evalAsString(s);
	}
	try {
	    r = Util.stringToBoolean(s);
	} catch (final ProcessorException exception) {
	    throw new ProcessorException(
	        "Error in attribute '" + attributeName + "'");
	}
	return r;
    }
    
    /**
     * Extracts boolean value from an attribute, evaluating scripts.
     * These must be properly marked.
     *
     * @param     element            element to be processoed
     * @param     attributeName      name of the attribute
     * @param     falseString        string representing <code>false</code>
     * @param     trueString         string representing <code>true</code>
     * @param     defaultValue       default value if attribute not present
     *                               (empty attribute is an error)
     * @param     scriptProcessor    instance of the script processor
     *                               to be used
     * @return                       boolean value of the attribute
     * @exception ProcessorException if the string does not match
     *                               <code>falseString</code> or
     *                               <code>trueString</code> or on error
     *                               in the script
     */
    public static boolean extractBooleanAttribute(
        final Element element,
	final String attributeName,
	final String falseString,
	final String trueString,
	final boolean defaultValue,
	final ScriptProcessor scriptProcessor
	) throws ProcessorException {
	if (!element.hasAttribute(attributeName)) {
	    return defaultValue;
	}
	boolean r = false;
	String s = element.getAttribute(attributeName);
	if (scriptProcessor != null) {
	    s = scriptProcessor.evalAsString(s);
	}
	if (s.equals(falseString)) {
	    r = false;
	} else if (s.equals(trueString)) {
	    r = true;
	} else {
	    throw new ProcessorException("Error in attribute '" +
					 attributeName + "'");
	}
	return r;
    }
    
    /**
     * Extracts string value from an attribute according to options,
     *  evaluating scripts. These must be properly marked.
     *
     * @param     element            element to be processoed
     * @param     attributeName      name of the attribute
     * @param     defaultValue       default value if attribute not present
     *                               (empty attribute is a legal value)
     * @param     scriptProcessor    instance of the script processor
     *                               to be used, or <code>null</code>
     *                               if scripting is not allowed
     * @return                       string value of the attribute
     * @exception ProcessorException on error in the script
     */
    public static String extractStringAttribute(
        final Element element,
	final String attributeName,
	final String defaultValue,
	final ScriptProcessor scriptProcessor
	) throws ProcessorException {
	if (!element.hasAttribute(attributeName)) {
	    return defaultValue;
	}
	String r = element.getAttribute(attributeName);
	if (scriptProcessor != null) {
	    r = scriptProcessor.evalAsString(
	        element.getAttribute(attributeName).trim());
	}
	return r;
    }
    
    /**
     * Extracts string value from an attribute according to a list of options,
     * evaluating scripts. These must be properly marked.
     *
     * @param     element            element to be processoed
     * @param     attributeName      name of the attribute
     * @param     options            array of allowed strings
     * @param     scriptProcessor    instance of the script processor
     *                               to be used, or <code>null</code>
     *                               if scripting is not allowed
     * @return                       string value of the attribute
     * @exception ProcessorException if the string does not match
     *                               <code>options</code> or on error
     *                               in the script
     */
    public static String extractStringArrayAttribute(
        final Element element,
	final String attributeName,
	final String[] options,
	final String defaultValue,
	final ScriptProcessor scriptProcessor
	) throws ProcessorException {
	if (!element.hasAttribute(attributeName)) {
	    return defaultValue;
	}
	String r = element.getAttribute(attributeName);
	if (scriptProcessor != null) {
	    r = scriptProcessor.evalAsString(r.trim());
	}
        for (String option: options) {
	    if (r.equals(option)) {
		return r;
	    }
	}
	throw new ProcessorException(
	    "Error in input file, attribute value, element '" +
	    element.getTagName() + "', attribute '" +
	    attributeName + "', value '" + r + "'");
    }

    /**
     * Writes value to InStream.
     *
     * @param     value       value to be written
     * @exception IOException on I/O error
     */
    public void write(final BigInteger value) throws IOException {
	processor.getInStream().write(value);
    }

    // for description see Object
    @Override
    public String toString() {
	return "ParsedElement";
    }

    /**
     * Main constructor.
     *
     * @param     processor          the input tree processor object
     * @param     element            the <code>Element</code> object in
     *                               the XML file
     * @exception ProcessorException on error in parameters
     */
    public ParsedElement(final InputTreeProcessor processor,
			 final Element element
			 ) throws ProcessorException {
    	log.fine("Parsed element creation started");

    	this.processor = processor;
    	this.element = element;
	
    	log.fine("Parsed element set up");
    }
}
