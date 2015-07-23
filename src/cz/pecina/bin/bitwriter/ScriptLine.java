/* ScriptLine.java
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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.logging.Logger;

/**
 * Parser for parsing of lines containing both script and non-script terms.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ScriptLine implements Iterable<String>, Iterator<String> {

    // static logger
    private static final Logger log =
	Logger.getLogger(ScriptLine.class.getName());

    // fields
    protected String line;
    protected boolean tokenize;

    // for description see Iterable
    @Override
    public Iterator<String> iterator() {
	return this;
    }
    
    // for description see Iterator
    @Override
    public boolean hasNext() {
	return !line.isEmpty();
    }

    private static final Pattern startsWithScriptPrefixPattern =
	Pattern.compile("(?s)^(" +
			Constants.ESCAPED_SCRIPT_PREFIX + ".*?" +
			Constants.ESCAPED_SCRIPT_SUFFIX + ")\\s*(.*)$");
    private static final Pattern doesNotStartWithScriptPrefixPatternTokenize =
	Pattern.compile("(?s)^(\\S+)\\s*(.*)$");
    private static final Pattern
	doesNotStartWithScriptPrefixPatternDoNotTokenize =
	Pattern.compile("(?s)^(.)\\s*(.*)$");

    // for description see Iterator
    @Override
    public String next() throws NoSuchElementException {
	log.finer("Next chunk requested, line: " + line);
	log.finest("Tokenize: " + tokenize);
	if (line.isEmpty()) {
	    throw new NoSuchElementException(
	        "Trying to access non-existent script line element");
	}
	Matcher matcher = startsWithScriptPrefixPattern.matcher(line);
	if (!matcher.matches()) {
	    log.finest("Matching line without script prefix: " + line);
	    matcher = (tokenize ? doesNotStartWithScriptPrefixPatternTokenize :
	        doesNotStartWithScriptPrefixPatternDoNotTokenize).matcher(line);
	    if (!matcher.matches()) {
		throw new NoSuchElementException("Error in script: " + line);
	    }
	}
	final MatchResult matchResult = matcher.toMatchResult();
	line = matchResult.group(2).trim();
	log.finer("Chunk: " + matchResult.group(1) + ", new line: " + line);
	return matchResult.group(1);
    }

    // for description see Object
    @Override
    public String toString() {
	return "ScriptLine";
    }

    /**
     * Main constructor.
     *
     * @param     line               the input line containing the text
     *                               to be parsed
     * @param     tokenize           if <code>true</code>, the input line
     *                               is split into script and tokens terms
     *                               separated by whitespace characters,
     *                               if <code>false</code>, the input line is
     *                               split into scripts and single characters
     * @exception ProcessorException on error in parameters
     */
    public ScriptLine(final String line,
		      final boolean tokenize
		      ) throws ProcessorException {
	log.fine("Creating script line parser");

	if (line == null) {
	    throw new ProcessorException("Null line cannot be parsed");
	}
	this.line = line.trim();
	this.tokenize = tokenize;

	log.fine("Script line parser creation completed");
    }

    /**
     * Simplified constructor with <code>tokenize</code> set to
     * <code>true</code>.
     *
     * @param     line               the input line containing the text
     *                               to be parsed
     * @exception ProcessorException on error in parameters
     */
    public ScriptLine(final String line) throws ProcessorException {
	this(line, true);
    }
}
