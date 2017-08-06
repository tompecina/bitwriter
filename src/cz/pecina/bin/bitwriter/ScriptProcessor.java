/* ScriptProcessor.java
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
import java.util.Map;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.util.logging.Logger;

/**
 * Script-processing related methods.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ScriptProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(ScriptProcessor.class.getName());

  // fields
  protected InputTreeProcessor processor;
  protected ScriptEngineManager manager;
  protected ScriptEngine engine;
  protected Bindings bindings;
  protected Map<String,Variable> variables;
    
  /**
   * Tests if the expression is a script invocation.
   *
   * @param  expression expression to be checked
   * @return            <code>true</code> if expression is
   *                    a script invocation
   */
  public static boolean isScript(final String expression) {
    log.finer("Testing '" + expression + "' for script");
    if (expression == null) {
      return false;
    }
    final String trimmedExpression = expression.trim();
    return trimmedExpression.startsWith(Constants.SCRIPT_PREFIX) &&
      trimmedExpression.endsWith(Constants.SCRIPT_SUFFIX);
  }

  /**
   * Returns script without prefix and/or suffix.
   *
   * @param  expression expression to be processed
   * @return            script part of the expression 
   *                    or <code>null</code> if not a script invocation
   */
  public static String extractScript(final String expression) {
    log.finer("Extracting script from '" + expression + "'");
    if (!isScript(expression)) {
      return null;
    }
    final String trimmedExpression = expression.trim();
    return trimmedExpression.substring(
      Constants.SCRIPT_PREFIX.length(),
      (trimmedExpression.length() - Constants.SCRIPT_SUFFIX.length()));
  }

  /**
   * Puts value to variable 'val'.
   *
   * @param     value value of expression 'val'
   */
  public void putValue(final Object value) {
    bindings.put("val", value);
  }
    
  /**
   * Evaluatess script, without conversion of the returned object.
   *
   * @param     expression         expression to be evaluated
   * @return                       returned object
   * @exception ProcessorException on error during evalation
   */
  public Object eval(final String expression
		     ) throws ProcessorException {
    log.finer("eval() called with: " + expression);
    final ControlledOutputStream stream =
      processor.getControlledOutputStream();
    bindings.put("streamNumber", stream.getStreamNumber());
    bindings.put("streamLength", stream.getStreamLength());
    bindings.put("totalLength", stream.getTotalLength());
    for (String name: variables.keySet()) {
      log.finest("Putting variable '" + name + "' to bindings, value:" +
		 variables.get(name).getValue());
      bindings.put(name, variables.get(name).getValue());
    }
    Object result;
    try {
      result = engine.eval(expression, bindings);
    } catch (final ScriptException | NullPointerException exception) {
      throw new ProcessorException("Script error (1), exception: " +
				   exception.getMessage());
    } catch (final RuntimeException exception) {
      throw new ProcessorException("Script error (2), exception: " +
				   exception.getMessage());
    }
    for (String name: variables.keySet()) {
      Object value = bindings.get(name);
      log.finest("Retrieved variable '" + name +
		 "' from bindings, value: " + value);
      if (variables.get(name).getCalculator() == null) {
	if (value instanceof BigInteger) {
	  variables.get(name).setValue((BigInteger)value);
	} else {
	  if (value instanceof Double) {
	    value = Math.round((Double)value);
	  }
	  try {
	    variables.get(name).setValue(
					 new BigInteger(value.toString()));
	  } catch (final NumberFormatException |
		   NullPointerException exception) {
	    throw new ProcessorException("Script set variable '" + name +
					 "' to illegal value: " + value);
	  }
	}
      }

    }
    return result;
  }

  /**
   * Evaluatess script and converts result to <code>int</code>.
   *
   * @param     expression         expression to be evaluated
   * @return                       returned <code>int</code>
   * @exception ProcessorException on error during evalation
   */
  public int evalAsInt(final String expression
		       ) throws ProcessorException {
    int result = 0;
    if (isScript(expression)) {
      Object value = eval(extractScript(expression));
      try {
	if (value instanceof Double) {
	  value = Math.round((Double)value);
	}
	result = Integer.valueOf(value.toString());
      } catch (final NumberFormatException |
	       NullPointerException exception) {
	throw new ProcessorException(
	  "Script returned value that cannot be converted to int: " + value);
      }
    } else {
      result = Util.stringToInt(expression);
    }
    return result;
  }

  /**
   * Evaluate script and convert result to <code>long</code>.
   *
   * @param     expression         expression to be evaluated
   * @return                       returned <code>long</code>
   * @exception ProcessorException on error during evalation
   */
  public long evalAsLong(final String expression
			 ) throws ProcessorException {
    long result = 0;
    if (isScript(expression)) {
      Object value = eval(extractScript(expression));
      try {
	if (value instanceof Double) {
	  value = Math.round((Double)value);
	}
	result = Long.valueOf(value.toString());
      } catch (final NumberFormatException |
	       NullPointerException exception) {
	throw new ProcessorException(
          "Script returned value that cannot be converted to long: " + value);
      }
    } else {
      result = Util.stringToLong(expression);
    }
    return result;
  }

  /**
   * Evaluatess script and converts result to <code>BigInteger</code>.
   *
   * @param     expression         expression to be evaluated
   * @return                       returned <code>BigInteger</code>
   * @exception ProcessorException on error during evalation
   */
  public BigInteger evalAsBigInteger(final String expression
				     ) throws ProcessorException {
    BigInteger result;
    if (isScript(expression)) {
      Object value = eval(extractScript(expression));
      try {
	if (value instanceof Double) {
	  value = Math.round((Double)value);
	}
	result = new BigInteger(value.toString());
      } catch (final NumberFormatException |
	       NullPointerException exception) {
	throw new ProcessorException("Script returned value that cannot" +
				     " be converted to BigInteger: " + value);
      }
    } else {
      result = Util.stringToBigInteger(expression);
    }
    return result;
  }

  /**
   * Evaluates script and converts result to <code>float</code>.
   *
   * @param     expression         expression to be evaluated
   * @return                       returned <code>float</code>
   * @exception ProcessorException on error during evalation
   */
  public float evalAsFloat(final String expression
			   ) throws ProcessorException {
    float result = 0;
    if (isScript(expression)) {
      Object value = eval(extractScript(expression));
      try {
	if (value instanceof Double) {
	  result = ((Double)value).floatValue();
	} else if (value instanceof Float) {
	  result = (Float)value;
	} else {
	  result = Float.valueOf(value.toString());
	}
      } catch (final NumberFormatException |
	       NullPointerException exception) {
	throw new ProcessorException(
	  "Script returned value that cannot be converted to float: " + value);
      }
    } else {
      result = Util.stringToFloat(expression);
    }
    return result;
  }

  /**
   * Evaluates script and converts result to <code>double</code>.
   *
   * @param     expression         expression to be evaluated
   * @return                       returned <code>double</code>
   * @exception ProcessorException on error during evalation
   */
  public double evalAsDouble(final String expression
			     ) throws ProcessorException {
    double result = 0;
    if (isScript(expression)) {
      Object value = eval(extractScript(expression));
      try {
	if (value instanceof Double) {
	  result = (Double)value;
	} else if (value instanceof Float) {
	  result = ((Float)value).doubleValue();
	} else {
	  result = Double.valueOf(value.toString());
	}
      } catch (final NumberFormatException |
	       NullPointerException exception) {
	throw new ProcessorException(
	  "Script returned value that cannot be converted to double: " + value);
      }
    } else {
      result = Util.stringToDouble(expression);
    }
    return result;
  }

  /**
   * Evaluates script and converts result to a string.
   *
   * @param     expression         expression to be evaluated
   * @return                       returned string
   * @exception ProcessorException on error during evalation
   */
  public String evalAsString(final String expression
			     ) throws ProcessorException {
    if (isScript(expression)) {
      return eval(extractScript(expression)).toString();
    } else {
      return expression.trim();
    }
  }
    
  // for description see Object
  @Override
  public String toString() {
    return "ScriptProcessor";
  }

  /**
   * Main constructor.
   *
   * @param     processor          the <code>InputTreeProcessor</code>
   *                               object providing variables, connector
   *                               object, etc.
   * @exception ProcessorException on error during initialization
   *                               of bindings
   */
  public ScriptProcessor(final InputTreeProcessor processor
			 ) throws ProcessorException {
    log.fine("Creating script processor");

    this.processor = processor;
    variables = processor.getVariables();
    manager = new ScriptEngineManager();
    engine = manager.getEngineByName("JavaScript");
    bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
    try {
      bindings.put("BigInteger",
		   engine.eval("Java.type('java.math.BigInteger')"));
      bindings.put("connector", processor.getConnector());
      bindings.put("variables", variables);
    } catch (final ScriptException exception) {
      throw new ProcessorException("Cannot initialize the bindings");
    }

    log.fine("Script processor creation completed");
  }
}
