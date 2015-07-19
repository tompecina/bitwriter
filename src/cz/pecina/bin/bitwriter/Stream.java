/* Stream.java
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

/**
 * Stream interface.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public interface Stream extends AutoCloseable {

    /**
     * Endianness values.
     */
    public enum Endianness {BIG, LITTLE};

    /**
     * Sets the default values for the stream.
     *
     * @exception IOException on I/O error
     */
    public void setDefaults();

    /**
     * Resets the stream.
     */
    public void reset();
    

    /**
     * Write a <code>BigInteger</code> value to the stream,
     * regardless of the width.
     *
     * @param     value       data to be written
     * @exception IOException on I/O error
     */
    public void write(final BigInteger value) throws IOException;

    /**
     * Flushes the stream.
     *
     * @exception IOException on I/O error
     */
    public void flush() throws IOException;
    
    /**
     * Closes the stream.
     *
     * @exception IOException on I/O error
     */
    public void close() throws IOException;
}
