/* RandomElement.java
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
import java.util.Random;
import java.io.IOException;
import org.jdom2.Element;
import java.util.logging.Logger;

/**
 * Object representing an &lt;random&gt; element
 *  (non-cryptographic pseudo-random number generator)
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class RandomElement extends ParsedElement {

    // static logger
    private static final Logger log =
	Logger.getLogger(RandomElement.class.getName());

    // processes the element
    private void process() throws ProcessorException, IOException {
	log.fine("Processing <random> element");

	final int width = extractIntAttribute(
	    element,
	    "width",
	    1,
	    Integer.MAX_VALUE,
	    8,
	    processor.getScriptProcessor());
	final int length = extractIntAttribute(
	    element,
	    "length",
	    0,
	    Integer.MAX_VALUE,
	    1,
	    processor.getScriptProcessor());
	final long seed = extractLongAttribute(
	    element,
	    "seed",
	    Long.MIN_VALUE,
	    Long.MAX_VALUE,
	    0,
	    processor.getScriptProcessor());
	Random random = null;
	if (element.getAttribute("seed") != null) {
	    random = new Random(seed);
	} else {
	    random = new Random();
	}
	final byte[] buffer = new byte[1];
	final BigInteger mask = Util.makeMask(width);
	for (int iter = 0; iter < length; iter++) {
	    BigInteger r = BigInteger.ZERO;
	    for (int i = 0; i < width; i += 8) {
		random.nextBytes(buffer);
		r = r.shiftLeft(8).or(BigInteger.valueOf(buffer[0] & 0xff));
	    }
	    write(r.and(mask));
	}
	log.fine("<random> element processed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "RandomElement";
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
    public RandomElement(final InputTreeProcessor processor,
			 final Element element
			 ) throws ProcessorException, IOException {
	super(processor, element);
	log.fine("<random> element creation started");

	process();
	
	log.fine("<random> element set up");
    }
}
