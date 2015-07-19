/* SetElement.java
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
import org.jdom2.Element;
import java.util.logging.Logger;

/**
 * Object representing a &lt;set&gt; element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SetElement extends VariableElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(SetElement.class.getName());

    // processes the element
    private void process() throws ProcessorException, IOException {
	log.fine("Processing <set> element");

	final Variable variable = getOrCreateVariable(element);
	final BigInteger value =
	    extractBigIntegerAttribute(element,
				       "value",
				       null,
				       null,
				       null,
				       processor.getScriptProcessor());
	if (value != null) {
	    variable.setValue(value);
	}
	final String onStreamIn =
	    element.getAttributeValue("on-stream-in");
	if (onStreamIn != null) {
	    variable.setOnStreamIn(onStreamIn);
	}
	final String onAggregateStreamIn =
	    element.getAttributeValue("on-aggregate-stream-in");
	if (onAggregateStreamIn != null) {
	    variable.setOnAggregateStreamIn(onAggregateStreamIn);
	}
	final String onBitStream =
	    element.getAttributeValue("on-bitstream");
	if (onBitStream != null) {
	    variable.setOnBitStream(onBitStream);
	}
	final String onAggregateStreamOut =
	    element.getAttributeValue("on-aggregate-stream-out");
	if (onAggregateStreamOut != null) {
	    variable.setOnAggregateStreamOut(onAggregateStreamOut);
	}
	final String onStreamOut =
	    element.getAttributeValue("on-stream-out");
	if (onStreamOut != null) {
	    variable.setOnStreamOut(onStreamOut);
	}
	final String onOutputStream =
	    element.getAttributeValue("on-output-stream");
	if (onOutputStream != null) {
	    variable.setOnOutputStream(onOutputStream);
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
    public SetElement(final InputTreeProcessor processor,
		      final Element element
		      ) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("<set> element creation started");

	process();
	
	log.fine("<set> element set up");
    }
}
