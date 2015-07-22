/* InputTreeProcessor.java
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
import java.util.Map;
import java.util.TreeMap;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import org.w3c.dom.Element;
import java.util.logging.Logger;

/**
 * Central object transforming input into output.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class InputTreeProcessor implements AutoCloseable {

    // static logger
    private static final Logger log =
	Logger.getLogger(InputTreeProcessor.class.getName());

    // fields
    protected Parameters parameters;
    protected String inputString;
    protected PrintStream stderr;
    protected PresetCrcModels presetCrcModels;
    protected ScriptProcessor scriptProcessor;
    protected InStream inStream;
    protected InAggregateStream inAggregateStream;
    protected BitStream bitStream;
    protected OutAggregateStream outAggregateStream;
    protected OutStream outStream;
    protected ControlledOutputStream controlledOutputStream;
    protected final Map<String,Variable> variables = new TreeMap<>();
    protected Connector connector;
    
    /**
     * Gets the parameters object.
     *
     * @return the parameters object
     */
    public Parameters getParameters() {
	log.finer("Getting parameters");
	return parameters;
    }

    /**
     * Gets the error output print stream.
     *
     * @return the error output print stream
     */
    public PrintStream getStderr() {
	log.finer("Getting stderr");
	return stderr;
    }

    /**
     * Gets the script processor object.
     *
     * @return the script processor object
     */
    public ScriptProcessor getScriptProcessor() {
	log.finer("Getting script processor");
	return scriptProcessor;
    }

    /**
     * Gets the preset CRC models object.
     *
     * @return the preset CRC models object
     */
    public PresetCrcModels getPresetCrcModels() {
	log.finer("Getting preset CRC models");
	return presetCrcModels;
    }

    /**
     * Gets the input stream.
     *
     * @return the input stream
     */
    public InStream getInStream() {
	log.finer("Getting inStream");
	return inStream;
    }

    /**
     * Gets the input aggregate stream.
     *
     * @return the input aggregate stream
     */
    public InAggregateStream getInAggregateStream() {
	log.finer("Getting inAggregateStream");
	return inAggregateStream;
    }

    /**
     * Gets the bit stream.
     *
     * @return the bit stream
     */
    public BitStream getBitStream() {
	log.finer("Getting bitStream");
	return bitStream;
    }

    /**
     * Gets the output aggregate stream.
     *
     * @return the output aggregate stream
     */
    public OutAggregateStream getOutAggregateStream() {
	log.finer("Getting outAggregateStream");
	return outAggregateStream;
    }

    /**
     * Gets the output stream.
     *
     * @return the output stream
     */
    public OutStream getOutStream() {
	log.finer("Getting outStream");
	return outStream;
    }

    /**
     * Gets the (controlled) output stream.
     *
     * @return the (controlled) output stream
     */
    public ControlledOutputStream getControlledOutputStream() {
	log.finer("Getting controlledOutputStream");
	return controlledOutputStream;
    }

    /**
     * Gets the connector object.
     *
     * @return the connector object
     * @see Connector
     */
    public Connector getConnector() {
	log.finer("Getting connector");
	return connector;
    }

    /**
     * Gets the variables map.
     *
     * @return the variables map
     */
    public Map<String,Variable> getVariables() {
	log.finer("Getting the variables");
	return variables;
    }

    /**
     * Trigger method called on write's to the streams.
     *
     * @param     type               type of the variable that activated
     *                               the trigger
     * @param     value              the value written to the stream
     * @exception ProcessorException on expression evaluation error
     */
    public void trigger(final Variable.Type type,
			final BigInteger value
			) throws ProcessorException {
	log.finer("Value written to " + type + ": " +
		  Util.bigIntegerToString(value) +
		  " (" + value.getClass() + ")");
	scriptProcessor.putValue(value);
	for (Variable variable: variables.values()) {
	    final Calculator calculator = variable.getCalculator();
	    if ((calculator != null) && (variable.getType() == type)) {
		log.finest("Updating calculator for: " + variable.getName());
		if (type == Variable.Type.BITSTREAM) {
		    calculator.updateBit(value.testBit(0));
		} else {
		    calculator.update(value);
		}
		variable.setValue(calculator.getRegister());
		log.finest("Variable updated from calculator, new value: " +
			   Util.bigIntegerToString(variable.getValue()));
	    }
	    String expression;
	    switch (type) {
		case STREAM_IN:
		    expression = variable.getOnStreamIn();
		    break;
		case AGGREGATE_STREAM_IN:
		    expression = variable.getOnAggregateStreamIn();
		    break;
		case BITSTREAM:
		    expression = variable.getOnBitStream();
		    break;
		case AGGREGATE_STREAM_OUT:
		    expression = variable.getOnAggregateStreamOut();
		    break;
		case STREAM_OUT:
		    expression = variable.getOnStreamOut();
		    break;
		case OUTPUT_STREAM:
		default:
		    expression = variable.getOnOutputStream();
		    break;
	    }
	    if (expression != null) {
		final String trimmedExpression = expression.trim();
		if (!trimmedExpression.isEmpty()) {
		    variable.setValue(
		        scriptProcessor.evalAsBigInteger(trimmedExpression));
		}
	    }
	}
    }
 
    // for description see AutoCloseable
    @Override
    public void close() throws IOException {
	inStream.close();
    }

    // processes the input tree
    public void process(final Parameters parameters,
			final String inputString,
			final PresetCrcModels presetCrcModels,
			final PrintStream stderr
			) throws ProcessorException, IOException {
	log.fine("Parsing input tree");

	this.parameters = parameters;
	this.inputString = inputString;
	this.presetCrcModels = presetCrcModels;
	this.stderr = stderr;
	controlledOutputStream.setHexMode(parameters.isHexMode());

	scriptProcessor = new ScriptProcessor(this);
	
	final InputTree inputTree = new InputTree(inputString,
						  parameters.getValidate());

	final Element rootElement = inputTree.getRootElement();

	if (!rootElement.getTagName().equals("file")) {
	    throw new ProcessorException("Root element must be <file>");
	}
	new FileElement(this, rootElement);

	log.fine("Processing completed");
    }
    
    // for description see Object
    @Override
    public String toString() {
	return "InputTreeProcessor";
    }

    /**
     * Main constructor.
     *
     * @param     outputStream       the output stream
     * @exception ProcessorException on error in parameters
     * @exception IOException        on I/O error
     */
    public InputTreeProcessor(final OutputStream outputStream
			      ) throws ProcessorException, IOException {
	log.fine("Input tree processor creation started");

	controlledOutputStream = new ControlledOutputStream(this, outputStream);
	outStream = new OutStream(this, controlledOutputStream);
	outAggregateStream = new OutAggregateStream(this, outStream);
	bitStream = new BitStream(this, outAggregateStream);
	inAggregateStream = new InAggregateStream(this, bitStream);
	inStream = new InStream(this, inAggregateStream);
	connector = new Connector(this);
	
	log.fine("Input tree processor set up");
    }
}
