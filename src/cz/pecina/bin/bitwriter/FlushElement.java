/* FlushElement.java
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

import java.io.IOException;
import org.jdom2.Element;
import java.util.logging.Logger;

/**
 * Object representing a &lt;flush&gt; element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class FlushElement extends ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(FlushElement.class.getName());

    // processes the element
    private void process() throws ProcessorException, IOException {
	log.fine("Processing <flush> element");

	processor.getInStream().flush();

	log.fine("Flush <element> processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "FlushElement";
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
    public FlushElement(final InputTreeProcessor processor,
			final Element element
			) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("<flush> element creation started");

	process();
	
	log.fine("<flush> element set up");
    }
}
