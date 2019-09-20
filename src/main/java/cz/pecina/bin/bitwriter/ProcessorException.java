/* ProcessorException.java
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

import java.util.logging.Logger;

/**
 * Catch-all exception thrown on various processing errors.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class ProcessorException extends Exception {

  // static logger
  private static final Logger log = Logger.getLogger(ProcessorException.class.getName());

  // for description see Object
  @Override
  public String toString() {
    return "ProcessorException";
  }

  /**
   * Main constructor.
   *
   * @param message description of the reason
   */
  public ProcessorException(final String message) {
    super(message);
    log.fine("ProcessorException created: " + message);
  }
}
