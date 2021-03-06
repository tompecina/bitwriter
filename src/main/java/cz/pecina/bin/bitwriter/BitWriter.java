/* BitWriter.java
 *
 * Copyright (C) 2015-19, Tomas Pecina <tomas@pecina.cz>
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
 *
 * The source code is available from <https://github.com/tompecina/bitwriter>.
 */

package cz.pecina.bin.bitwriter;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;

/**
 * Main class of the application.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class BitWriter {

  // static logger
  private static final Logger log = Logger.getLogger(BitWriter.class.getName());

  /**
   * The main method.
   *
   * @param args command-line arguments
   */
  public static void main(final String[] args) {
    log.fine("Application started");

    final int exitCode = process(args, System.in, System.out, System.err);

    log.fine("Application terminated with exit code: " + exitCode);
    System.exit(exitCode);
  }

  /**
   * Main processing method.
   *
   * @param  args   command-line arguments
   * @param  stdin  console input stream
   * @param  stdout console output stream
   * @param  stderr console error stream
   * @return        the exit code
   */
  public static int process(final String[] args, final InputStream stdin, final PrintStream stdout,
      final PrintStream stderr) {
    log.fine("Processing started");

    try {
      new BitWriter(args, stdin, stdout, stderr);
    } catch (final ParametersException exception) {
      stderr.println("Error in parameters: " + exception.getMessage());
      Parameters.usage(stderr);
      log.fine("Processing terminated abnormally, exception: " + exception.getMessage());
      return Constants.EXIT_CODE_ERROR_IN_PARAMETERS;
    } catch (final PresetCrcModelsException exception) {
      stderr.println("Error in CRC models file: " + exception.getMessage());
      log.fine("Processing terminated abnormally, exception: " + exception.getMessage());
      return Constants.EXIT_CODE_ERROR_IN_PRESET_CRC_MODELS;
    } catch (final ProcessorException exception) {
      stderr.println("Processing error: " + exception.getMessage());
      log.fine("Processing terminated abnormally, exception: " + exception.getMessage());
      return Constants.EXIT_CODE_PROCESSING_ERROR;
    } catch (final IOException exception) {
      stderr.println("I/O error: " + exception.getMessage());
      log.fine("Processing terminated abnormally, exception: " + exception.getMessage());
      return Constants.EXIT_CODE_IO_ERROR;
    }

    log.fine("Processing terminated normally");
    return Constants.EXIT_CODE_SUCCESS;
  }

  // for description see Object
  @Override
  public String toString() {
    return "BitWriter";
  }

  /**
   * Main constructor.
   *
   * @param     args                     command-line parameters
   * @param     stdin                    console input stream
   * @param     stdout                   console output stream
   * @param     stderr                   console error stream
   * @exception ParametersException      on error in command-line parameters
   * @exception ProcessorException       on processing error in parameters
   * @exception PresetCrcModelsException on error in preset CRC models
   * @exception IOException              on I/O error
   */
  public BitWriter(final String[] args, final InputStream stdin, final PrintStream stdout, final PrintStream stderr)
      throws ParametersException, ProcessorException, PresetCrcModelsException, IOException {
    log.fine("Processor started");

    final Parameters parameters = new Parameters();
    final CommandLine line = parameters.parse(args);

    if (line.hasOption("?")) {
      parameters.usage(stderr);
      return;
    }

    if (line.hasOption("V")) {
      stderr.println("1.0.5");
      return;
    }

    final InputStream crcModelsInputStream = (parameters.getCrcFileNameFlag()
        ? new FileInputStream(parameters.getCrcFileName()) : BitWriter.class.getResourceAsStream("crc.xml"));
    final PresetCrcModels presetCrcModels = new PresetCrcModels(crcModelsInputStream);

    if (parameters.getListCrcFlag()) {
      presetCrcModels.list(stderr);
      return;
    }

    OutputStream outputStream;
    if (parameters.getOutputFileNameFlag()) {
      outputStream = new FileOutputStream(parameters.getOutputFileName());
    } else {
      outputStream = stdout;
    }

    final List<InputStream> inputStreams = new ArrayList<>();
    if (parameters.getLiteralStrings() != null) {
      for (String literalString : parameters.getLiteralStrings()) {
        String s;
        if (literalString.startsWith("<?xml ")) {
          s = literalString.trim();
        } else {
          s = Constants.XML_PREAMBLE + literalString.trim() + Constants.XML_POSTAMBLE;
        }
        inputStreams.add(new ByteArrayInputStream(s.getBytes()));
      }
    }
    if (parameters.getFileNames() != null) {
      for (String inputFileName : parameters.getFileNames()) {
        inputStreams.add(new FileInputStream(inputFileName));
      }
    }
    if (inputStreams.isEmpty()) {
      inputStreams.add(stdin);
    }

    final InputTreeProcessor processor = new InputTreeProcessor(outputStream);
    for (InputStream inputStream : inputStreams) {
      processor.process(parameters, inputStream, presetCrcModels, stderr);
      inputStream.close();
    }
    processor.close();

    log.fine("Processor terminated normally");
  }
}
