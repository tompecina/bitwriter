/* Constants.java
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

import java.math.BigInteger;
import java.nio.charset.Charset;
import javax.xml.XMLConstants;

/**
 * Constants.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public final class Constants {

  /**
   * <code>BigInteger</code> constant <code>0xff</code>.
   */
  public static final BigInteger FF = BigInteger.valueOf(0xff);

  /**
   * Exit code: Success.
   */
  public static final int EXIT_CODE_SUCCESS = 0;

  /**
   * Exit code: Error in parameters.
   */
  public static final int EXIT_CODE_ERROR_IN_PARAMETERS = 1;

  /**
   * Exit code: Error in preset CRC models.
   */
  public static final int EXIT_CODE_ERROR_IN_PRESET_CRC_MODELS = 2;

  /**
   * Exit code: Processing error.
   */
  public static final int EXIT_CODE_PROCESSING_ERROR = 3;

  /**
   * Exit code: I/O error.
   */
  public static final int EXIT_CODE_IO_ERROR = 4;

  /**
   * Version of the input XML file.
   */
  public static final String FILE_XML_FILE_VERSION = "1.0";

  /**
   * Version of the CRC presets XML file.
   */
  public static final String CRC_XML_FILE_VERSION = "1.0";

  /**
   * Prefix of the Schema location.
   */
  public static final String SCHEMA_PREFIX = "http://www.pecina.cz/xsd/";

  /**
   * Default charset.
   */
  public static final String DEFAULT_CHARSET = Charset.defaultCharset().name();

  /**
   * XML preamble.
   */
  public static final String XML_PREAMBLE = String.format("<?xml version=\"1.0\" encoding=\"" + DEFAULT_CHARSET
      + "\"?>%n<file xmlns:xsi=\"%3$s\" xsi:noNamespaceSchemaLocation=\"%1$sbin-%2$s.xsd\" version=\"%2$s\">%n",
      SCHEMA_PREFIX, FILE_XML_FILE_VERSION, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

  /**
   * XML postamble.
   */
  public static final String XML_POSTAMBLE = String.format("%n</file>%n");

  /**
   * Script delimiter.
   */
  public static final String SCRIPT_PREFIX = "{{";

  /**
   * Script delimiter.
   */
  public static final String SCRIPT_SUFFIX = "}}";

  /**
   * Script delimiter.
   */
  public static final String ESCAPED_SCRIPT_PREFIX = "\\{\\{";

  /**
   * Script delimiter.
   */
  public static final String ESCAPED_SCRIPT_SUFFIX = "\\}\\}";

  // default constructor disabled
  private Constants() { }
}
