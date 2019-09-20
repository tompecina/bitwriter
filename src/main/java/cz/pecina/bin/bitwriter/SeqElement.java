/* SeqElement.java
 *
 * Copyright (C) 2015-19, Tomáš Pecina <tomas@pecina.cz>
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
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import java.util.logging.Logger;

/**
 * Combined object representing a &lt;seq&gt; or &lt;stream&gt; element.
 *
 * @author Tomáš Pecina
 * @version 1.0.5
 */
public class SeqElement extends ParsedElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(SeqElement.class.getName());

  // processes the element
  private void process() throws ProcessorException, IOException {
    log.fine("Processing <seq>/<stream> element");

    if (element.getTagName().equals("stream")) {
      processor.getControlledOutputStream().setDiscard(
        extractBooleanAttribute(element,
				"discard",
				false,
				processor.getScriptProcessor()));
      processor.getOutStream().setWidthOut(
        extractIntegerAttribute(element,
				"width-out",
				1,
				null,
				8,
				processor.getScriptProcessor()));
      processor.getOutAggregateStream().setWidthOutAggregate(
        extractIntegerAttribute(element,
				"width-aggregate-out",
				1,
				null,
				8,
				processor.getScriptProcessor()));
      processor.getOutAggregateStream().setEndiannessOut(
        Stream.Endianness.valueOf(extractStringArrayAttribute(
	  element,
	  "endianness-out",
	  ENDIANESS_TYPES,
	  "big",
	  processor.getScriptProcessor()).toUpperCase()));
      processor.getBitStream().setReflectOut(extractBooleanAttribute(
        element,
	"reflect-out",
	false,
	processor.getScriptProcessor()));
      final int widthIn = extractIntegerAttribute(
        element,
	"width-in",
	1,
	null,
	8,
	processor.getScriptProcessor());
      processor.getInAggregateStream().setWidthInAggregate(
        extractIntegerAttribute(element,
				"width-aggregate-in",
				1,
				null,
				widthIn,
				processor.getScriptProcessor()));
      processor.getInStream().setWidthIn(widthIn);
      processor.getInAggregateStream().setReflectIn(
        extractBooleanAttribute(element,
				"reflect-in",
				false,
				processor.getScriptProcessor()));
      processor.getInStream().setEndiannessIn(
        Stream.Endianness.valueOf(extractStringArrayAttribute(
	  element,
	  "endianness-in",
	  ENDIANESS_TYPES,
	  "big",
	  processor.getScriptProcessor()).toUpperCase()));
    }
    final int count = extractIntegerAttribute(element,
					      "repeat",
					      0,
					      null,
					      1,
					      processor.getScriptProcessor());
    for (int iter = 0; iter < count; iter++) {
      for (Node content: children(element)) {
	if (content instanceof Text) {
	  final String trimmedText = ((Text)content)
	    .getTextContent().trim();
	  if (trimmedText.isEmpty()) {
	    continue;
	  }
	  for (String split: new ScriptLine(trimmedText)) {
	    final BigInteger value =
	      processor.getScriptProcessor()
	      .evalAsBigInteger(split);
	    if (value != null) {
	      write(value);
	    }
	  }
	} else if (content instanceof Element) {
	  final Element innerElement = (Element)content;
	  switch (innerElement.getTagName()) {
	    case "hex":
	      new RadixElement(processor, innerElement, 16);
	      break;
	    case "dec":
	      new RadixElement(processor, innerElement, 10);
	      break;
	    case "oct":
	      new RadixElement(processor, innerElement, 8);
	      break;
	    case "bin":
	      new RadixElement(processor, innerElement, 2);
	      break;
	    case "bits":
	      new RadixElement(processor, innerElement, 0);
	      break;
	    case "float":
	      new FloatElement(processor, innerElement, false);
	      break;
	    case "double":
	      new FloatElement(processor, innerElement, true);
	      break;
	    case "text":
	      new TextElement(processor, innerElement);
	      break;
	    case "random":
	      new RandomElement(processor, innerElement);
	      break;
	    case "include":
	      new IncludeElement(processor, innerElement);
	      break;
	    case "flush":
	      new FlushElement(processor, innerElement);
	      break;
	    case "seq":
	      new SeqElement(processor, innerElement);
	      break;
	    case "script":
	      new ScriptElement(processor, innerElement);
	      break;
	    default:
	      VariableElement.create(processor,
				     innerElement,
				     false);
	      break;
	  }
	}
      }
      if (element.getTagName().equals("stream")) {
	processor.getInStream().flush();
	processor.getControlledOutputStream().resetStream();
	log.finer("Resetting stream");
      }
    }
    log.fine("<seq>/<stream> element processed");
  }
    
  // for description see Object
  @Override
  public String toString() {
    return "SeqElement";
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
  public SeqElement(final InputTreeProcessor processor,
		    final Element element
		    ) throws ProcessorException, IOException {
    super(processor, element);
    log.fine("<seq>/<stream> element creation started");

    process();
	
    log.fine("<seq>/<stream> element set up");
  }
}
