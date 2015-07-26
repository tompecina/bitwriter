/* Parameters.java
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Parse command line and extract parameters.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Parameters {

    // static logger
    private static final Logger log =
	Logger.getLogger(Parameters.class.getName());

    // options
    private static final Options options = new Options();
    static {
	log.fine("Building options");
    	options.addOption(
    	    Option.builder("?")
    	         .longOpt("help")
    	         .desc("show usage information")
    	         .build()
    	    );
    	options.addOption(
    	    Option.builder("V")
    	         .longOpt("version")
    	         .desc("show version")
    	         .build()
    	    );
    	options.addOption(
    	    Option.builder("c")
    	         .longOpt("crc-file")
    	         .hasArg()
    	         .argName("FILE")
    	         .desc("XML file describing predefined CRC models")
    	         .build()
    	    );
    	options.addOption(
    	    Option.builder("l")
    	         .longOpt("list-crc-models")
    	         .desc("list all avaiable CRC models")
    	         .build()
    	    );
    	options.addOption(
    	    Option.builder("s")
    	         .longOpt("string")
    	         .hasArgs()
    	         .argName("STRING")
    	         .desc("input from literal STRING")
    	         .build()
    	    );
    	options.addOption(
    	    Option.builder("x")
    	         .longOpt("hex-mode")
    	         .desc("produce hex output")
    	         .build()
    	    );
    	options.addOption(
    	    Option.builder("o")
    	         .longOpt("output-file")
    	         .hasArg()
    	         .argName("FILE")
    	         .desc("output file (STDOUT if none provided)")
    	         .build()
    	    );
	log.fine("Options set up");
    }
    
    /**
     * Prints usage information.
     *
     * @param stream print stream for the output
     */
    public static void usage(final PrintStream stream) {
	log.fine("Printing usage information");
	final HelpFormatter helpFormatter = new HelpFormatter();
	PrintWriter printWriter = new PrintWriter(stream);
	helpFormatter.printHelp(
	    printWriter,
	    helpFormatter.getWidth(),
	    "bitwriter [options] [--] [input-file]...",
	    null,
	    options,
	    helpFormatter.getLeftPadding(),
	    helpFormatter.getDescPadding(),
	    "(C) 2015 Tomáš Pecina <tomas@pecina.cz>, license: GNU/GPL");
	printWriter.close();
    }

    // parsed parameters
    protected boolean outputFileNameFlag;
    protected String outputFileName;
    protected boolean crcFileNameFlag;
    protected String crcFileName;
    protected boolean listCrcFlag;
    protected String[] literalStrings;
    protected boolean hexMode;
    protected String[] fileNames;

    /**
     * Gets the output file option.
     *
     * @return <code>true</code> if output file present
     */
    public boolean getOutputFileNameFlag() {
	log.finer("Getting outputFileNameFlag: " + outputFileNameFlag);
	return outputFileNameFlag;
    }
    
    /**
     * Gets the output file name.
     *
     * @return output file name
     */
    public String getOutputFileName() {
	log.finer("Getting outputFileName: " + outputFileName);
	return outputFileName;
    }
    
    /**
     * Gets the CRC models file option.
     *
     * @return <code>true</code> if CRC models file present
     */
    public boolean getCrcFileNameFlag() {
	log.finer("Getting crcFileNameFlag: " + crcFileNameFlag);
	return crcFileNameFlag;
    }
    
    /**
     * Gets the name of the CRC models file.
     *
     * @return CRC file name
     */
    public String getCrcFileName() {
	log.finer("Getting crcFileName: " + crcFileName);
	return crcFileName;
    }
    
    /**
     * Gets the list CRC models option.
     *
     * @return list CRC models option present
     */
    public boolean getListCrcFlag() {
	log.finer("Getting listCrcFlag: " + listCrcFlag);
	return listCrcFlag;
    }
    
    /**
     * Gets the array of literal input strings.
     *
     * @return array of literal input strings
     */
    public String[] getLiteralStrings() {
	log.finer("Getting literalStrings: " + literalStrings);
	return literalStrings;
    }
    
    /**
     * Sets the hexadecimal mode.
     *
     * @param hexMode <code>true</code> if hexadecimal mode is to be active
     */
    public void setHexMode(final boolean hexMode) {
	log.fine("Setting hexMode to: " + hexMode);
	this.hexMode = hexMode;
    }
    
    /**
     * Returns <code>true</code> if hexadecimal mode is active.
     *
     * @return <code>true</code> if hexadecimal mode is active
     */
    public boolean isHexMode() {
	log.finer("Getting hexMode: " + hexMode);
	return hexMode;
    }
    
    /**
     * Gets file names.
     *
     * @return file names as string array
     */
    public String[] getFileNames() {
	log.finer("Getting fileNames: " + fileNames);
	return fileNames;
    }
    
    /**
     * Gets the number of file names.
     *
     * @return number of file names
     */
    public int numberFileNames() {
	int n = 0;
	if (fileNames != null) {
	    n = fileNames.length;
	}
	log.finer("Getting number of fileNames: " + n);
	return n;
    }
    
    /**
     * Gets a file name.
     *
     * @param  n file name index
     * @return   file names as a string array
     */
    public String getFileName(final int n) {
	log.finer("Getting fileName[" + n + "]: " + fileNames[n]);
	return fileNames[n];
    }
    
    /**
     * Parse the command line.
     *
     * @param args command-line arguments
     * @return     line parsed command line
     */
    public CommandLine parse(final String args[]) throws ParametersException {
	log.fine("Command line parsing started");

	final CommandLineParser parser = new DefaultParser();
	CommandLine line;
	try {
	    line = parser.parse(options, args);
	} catch (final ParseException exception) {
	    throw new ParametersException(
	        "Failed to parse the command line, exception: " +
		exception.getMessage());
	}

	fileNames = line.getArgs();

	outputFileNameFlag = line.hasOption("o");
	if (outputFileNameFlag) {
	    outputFileName = line.getOptionValue("o");
	}

	crcFileNameFlag = line.hasOption("c");
	if (crcFileNameFlag) {
	    crcFileName = line.getOptionValue("c");
	}

	if (line.hasOption("s")) {
	    literalStrings = line.getOptionValues("s");
	}

	listCrcFlag = line.hasOption("l");

	hexMode = line.hasOption("x");

	log.fine("Command line parsing completed");
	return line;
    }

    // for description see Object
    @Override
    public String toString() {
	return "Parameters";
    }

    /**
     * Empty constructor.
     */
    public Parameters() {
	log.fine("Empty Parameters object set up");
    }
}
