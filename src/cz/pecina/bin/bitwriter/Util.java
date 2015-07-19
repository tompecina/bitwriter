/* Util.java
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
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Static utility methods.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class Util {

    // static logger
    private static final Logger log =
	Logger.getLogger(Util.class.getName());

    /**
     * Reflects bits.
     *
     * @param  value value to be reflected
     * @param  width width of <code>value</code>
     * @return       reflected expression or <code>null</code>
     *               if illegal width
     */
    public static BigInteger reflect(final BigInteger value
				     , final int width) {
	log.finer("Reflecting value: " + bigIntegerToString(value) +
		  ", width: " + width);
	if (width < 1) {
	    return null;
	}
	BigInteger aux = value;
	BigInteger result = BigInteger.ZERO;
	for (int i = 0; i < width; i++) {
	    result = result.shiftLeft(1);
	    if (aux.testBit(0)) {
		result = result.setBit(0);
	    }
	    aux = aux.shiftRight(1);
	}
	return result;
    }

    /**
     * Creates a <code>BigInteger</code> mask.
     *
     * @param  width width of the mask
     * @return       the mask, or <code>null</code> if illegal width
     */
    public static BigInteger makeMask(final int width) {
	log.finer("Creating mask, width: " + width);
	if (width < 1) {
	    return null;
	}
	return BigInteger.ONE.shiftLeft(width).subtract(BigInteger.ONE);
    }

    /**
     * Reads a file into a byte array.
     *
     * @param     path        pathname of the file
     * @return                contents of the file
     * @exception IOException on error
     */
    public static byte[] fileToByteArray(final String path
					 ) throws IOException {
	if (path == null) {
	    throw new IOException("Null path");
	}
	try {
	    return Files.readAllBytes(Paths.get(path));
	} catch (IOException exception) {
	    throw new IOException("Failed to read file '" + path +
				  "', exception: " +
				  exception.getMessage());
	}
    }

    /**
     * Reads an UTF-8 encoded file into a string.
     *
     * @param     path        pathname of the file
     * @return                string contents of the file
     * @exception IOException on error
     */
    public static String fileToString(final String path
				      ) throws IOException {
	return new String(fileToByteArray(path), "UTF-8");
    }

    /**
     * Reads an UTF-8 encoded input stream into a string.
     *
     * @param     stream      the input stream
     * @return                string contents of the stream
     * @exception IOException on error
     */
    public static String streamToString(final InputStream stream
					) throws IOException {
	if (stream == null) {
	    throw new IOException("Null stream");
	}
	try {
	    return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
	} catch (NoSuchElementException | IllegalStateException exception) {
	    throw new IOException("Failed to read input stream");
	}
    }

    /**
     * Converts <code>true</code> to "T", <code>false</code> to "F".
     *
     * @param     b boolean value to be converted
     * @return      converted string
     */
    public static String TF(final boolean b) {
	return (b ? "T" : "F");
    }
    
    /**
     * Converts hyphens ("-") to underscores ("_").
     *
     * @param     s string to be converted
     * @return      converted string
     */
    public static String hyphensToUnderscores(final String s) {
	if (s == null) {
	    return null;
	}
	return s.replace("-", "_");
    }

    // patterns for stringToBigInteger
    private static final Pattern pattern16 =
	Pattern.compile("^([-+]?)0x([\\da-fA-F]+)$");
    private static final Pattern pattern10 =
	Pattern.compile("^([-+]?)([1-9]\\d*)$");
    private static final Pattern pattern8 =
	Pattern.compile("^([-+]?)0([0-7]+)$");
    private static final Pattern pattern2 =
	Pattern.compile("^([-+]?)0b([01]+)$");
    private static final Pattern pattern0 =
	Pattern.compile("^[-+]?0+$");

    /**
     * Converts a string to BigInteger. The format may be decimal, 
     * hexadecimal, octal or binary. Scripts are not evaluated.
     *
     * @param     string             string to be converted
     * @return                       BigInteger value of the string
     * @exception ProcessorException if the string does not represent 
     *                               a valid BigInteger value
     */
    public static BigInteger stringToBigInteger(final String string
						) throws ProcessorException {
	log.finer("Converting '" + string + "' to BigInteger");
	if (string == null) {
	    throw new ProcessorException("Null string");
	}
	final String trimmedString = string.trim();
	BigInteger r = null;
	Matcher matcher = pattern0.matcher(trimmedString);
	if (matcher.matches()) {
	    return BigInteger.ZERO;
	}
	MatchResult matchResult = null;
	matcher = pattern16.matcher(trimmedString);
	try {
	    if (matcher.matches()) {
		matchResult = matcher.toMatchResult();
		r = new BigInteger(matchResult.group(2), 16);
	    } else {
		matcher = pattern10.matcher(trimmedString);
		if (matcher.matches()) {
		    matchResult = matcher.toMatchResult();
		    r = new BigInteger(matchResult.group(2), 10);
		} else {
		    matcher = pattern8.matcher(trimmedString);
		    if (matcher.matches()) {
			matchResult = matcher.toMatchResult();
			r = new BigInteger(matchResult.group(2), 8);
		    } else {
			matcher = pattern2.matcher(trimmedString);
			if (matcher.matches()) {
			    matchResult = matcher.toMatchResult();
			    r = new BigInteger(matchResult.group(2), 2);
			} else {
			    throw new ProcessorException(
			        "Bad number format in stream (1): " +
				trimmedString);
			}
		    }
		}
	    }
	} catch (NumberFormatException exception) {
	    throw new ProcessorException("Bad number format (2): " +
					 trimmedString);
	}
	if (matchResult.group(1).equals("-")) {
	    r = r.negate();
	}
	return r;
    }

    /**
     * Converts a string to integer. The format may be decimal, hexadecimal,
     * octal or binary. Scripts are not evaluated.
     *
     * @param     string             string to be converted
     * @return                       integer value of the string
     * @exception ProcessorException if the string does not represent
     *                               a valid integer value
     */
    public static int stringToInt(final String string
				  ) throws ProcessorException {
	log.finer("Converting '" + string + "' to int");
	final String trimmedString = string.trim();
	final BigInteger r = stringToBigInteger(trimmedString);
	if ((r == null) ||
	    (r.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) ||
	    (r.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0)) {
	    throw new ProcessorException(
	        "Integer out of range: " + trimmedString);
	}
	return r.intValue();
    }

    /**
     * Converts a string to long. The format may be decimal, hexadecimal,
     * octal or binary. Scripts are not evaluated.
     *
     * @param     string             string to be converted
     * @return                       long value of the string
     * @exception ProcessorException if the string does not represent
     *                               a valid long value
     */
    public static long stringToLong(final String string
				    ) throws ProcessorException {
	log.finer("Converting '" + string + "' to long");
	final String trimmedString = string.trim();
	final BigInteger r = stringToBigInteger(trimmedString);
	if ((r == null) ||
	    (r.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) ||
	    (r.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0)) {
	    throw new ProcessorException(
	        "Long out of range: " + trimmedString);
	}
	return r.longValue();
    }

    /**
     * Converts a string to float. Scripts are not evaluated.
     *
     * @param     string             string to be converted
     * @return                       float value of the string
     * @exception ProcessorException if the string does not represent
     *                               a valid float value
     */
    public static float stringToFloat(final String string
				      ) throws ProcessorException {
	log.finer("Converting '" + string + "' to float");
	try {
	    return Float.valueOf(string.trim());
	} catch (NumberFormatException exception) {
	    throw new ProcessorException("Invalid float format: " + string);
	}
    }

    /**
     * Converts a string to double. Scripts are not evaluated.
     *
     * @param     string             string to be converted
     * @return                       double value of the string
     * @exception ProcessorException if the string does not represent
     *                               a valid double value
     */
    public static double stringToDouble(final String string
					) throws ProcessorException {
	log.finer("Converting '" + string + "' to double");
	try {
	    return Double.valueOf(string.trim());
	} catch (NumberFormatException exception) {
	    throw new ProcessorException(
	        "Invalid double format: " + string);
	}
    }

    /**
     * Converts a string to <code>boolean</code>, using the XML Schema
     * convention. Scripts are not evaluated.
     *
     * @param     string             string to be converted
     * @return                       boolean value of the string
     *                               (<code>true</code> if "true" or "1",
     *                               <code>false</code> if "false" or "0")
     * @exception ProcessorException if the string could not be converted
     */
    public static boolean stringToBoolean(final String string
					  ) throws ProcessorException {
	log.finer("Converting '" + string + "' to boolean");
	final String trimmedString = string.trim();
	if (trimmedString != null) {
	    if (trimmedString.equals("false") ||
		trimmedString.equals("0")) {
		return false;
	    } else if (trimmedString.equals("true") ||
		       trimmedString.equals("1")) {
		return true;
	    }
	}
	throw new ProcessorException("Illegal boolean value");
    }

    /**
     * Customized string representation of BigInteger.
     *
     * @param  value value to be converted
     * @return       string representatioin of <code>value</code>
     */
    public static String bigIntegerToString(final BigInteger value) {
	if (value == null) {
	    return "null";
	} else if (value.signum() < 0) {
	    return "-0x" + value.negate().toString(16);
	} else {
	    return "0x" + value.toString(16);
	}
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "Util";
    }

    // default constructor disabled
    private Util() {};
}
