/* Calculator.java
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
import java.util.logging.Logger;

/**
 * Abstract superclass of all calculators.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 * @see Variable
 */
public abstract class Calculator {

    // static logger
    private static final Logger log =
	Logger.getLogger(Calculator.class.getName());

    // fields
    protected BigInteger register = BigInteger.ZERO;
    
    /**
     * Sets the register.
     *
     * @param value the new value of the register
     */
    public void setRegister(final BigInteger value) {
	log.finer("Setting register to: " + Util.bigIntegerToString(value));
	register = value;
    }

    /**
     * Gets the register.
     *
     * @return the current value of the register
     */
    public BigInteger getRegister() {
	log.finer("Getting register: " + Util.bigIntegerToString(register));
	return register;
    }

    /**
     * Updates the register from a one-bit source.
     *
     * @param b the input value
     */
    public abstract void updateBit(final int b);

    /**
     * Updates the register from a one-bit source.
     *
     * @param b the input value
     */
    public abstract void updateBit(final boolean b);

    /**
     * Updates the register.
     *
     * @param d the array of input values
     */
    public abstract void update(final byte[] d);

    /**
     * Updates the register.
     *
     * @param b the input value
     */
    public abstract void update(final int b);

    /**
     * Updates the register.
     *
     * @param b the input value
     */
    public abstract void update(final BigInteger b);

    // for description see Object
    @Override
    public String toString() {
	return "Calculator";
    }
}
