/* IncludeElement.java
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
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;
import java.util.logging.Logger;

/**
 * Object representing an &lt;include&gt; element
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IncludeElement extends ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(IncludeElement.class.getName());

    // processes the element
    private void process() throws ProcessorException, IOException {
	log.fine("Processing <include> element");

	final int count =
	    extractIntAttribute(element,
				"repeat",
				0,
				Integer.MAX_VALUE,
				1,
				processor.getScriptProcessor());
	final String location =
	    extractStringAttribute(element,
				   "location",
				   null,
				   processor.getScriptProcessor());
	final long offset =
	    extractLongAttribute(element,
				 "offset",
				 0,
				 Long.MAX_VALUE,
				 0,
				 processor.getScriptProcessor());
	final long length =
	    extractLongAttribute(element,
				 "length",
				 0,
				 Long.MAX_VALUE,
				 Long.MAX_VALUE,
				 processor.getScriptProcessor());
	if ((location == null) || location.trim().isEmpty()) {
	    throw new ProcessorException(
	        "Missing location in the <include> element");
	}
	for (int iter = 0; iter < count; iter++) {
	    final InputStream fileInputStream = new FileInputStream(location);
	    if (offset != 0) {
		fileInputStream.skip(offset);
	    }
	    for (long i = 0; i < length; i++) {
		final int b = fileInputStream.read();
		if (b == -1) {
		    break;
		}
		write(BigInteger.valueOf(b));
	    }
	    fileInputStream.close();
	}
	log.fine("<include> element processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "IncludeElement";
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
    public IncludeElement(final InputTreeProcessor processor,
			  final Element element
			  ) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("<include> element creation started");

	process();
	
	log.fine("<include> element set up");
    }
}
