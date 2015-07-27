package cz.pecina.bin.bitwriter;

import java.math.BigInteger;
import junit.framework.TestCase;

public class TestScriptProcessor extends TestCase {

  private InputTreeProcessor inputTreeProcessor;
  private ScriptProcessor scriptProcessor;

  @Override
  protected void setUp() throws Exception {
    inputTreeProcessor = new InputTreeProcessor(System.out);
    scriptProcessor = new ScriptProcessor(inputTreeProcessor);
  }

  private void testResultAndClass(final String message,
				  final Object expected,
				  final Object actual) {
    assertEquals(message, expected, actual);
    assertEquals(message, expected.getClass(), actual.getClass());
  }

  public void testIsScript() {
    final String message = "Error in isScript";
    assertTrue(message,
	       ScriptProcessor.isScript("{{script}}"));
    assertTrue(message,
	       ScriptProcessor.isScript("  {{script}} "));
    assertTrue(message,
	       ScriptProcessor.isScript(" \n\n {{script\n}}\n"));
    assertTrue(message,
	       ScriptProcessor.isScript("{{scrip}t\n}}"));
    assertTrue(message,
	       ScriptProcessor.isScript("{{scrip}t}}"));
    assertFalse(message,
		ScriptProcessor.isScript(null));
    assertFalse(message,
		ScriptProcessor.isScript(""));
    assertFalse(message,
		ScriptProcessor.isScript(" "));
    assertFalse(message,
		ScriptProcessor.isScript("1"));
    assertFalse(message,
		ScriptProcessor.isScript("a"));
    assertFalse(message,
		ScriptProcessor.isScript("{{script\n}"));
    assertFalse(message,
		ScriptProcessor.isScript("{script\n}}"));
    assertFalse(message,
		ScriptProcessor.isScript("scrip{{t\n}}"));
    assertFalse(message,
		ScriptProcessor.isScript("{scrip{{t}}"));
    assertFalse(message,
		ScriptProcessor.isScript("{{script} }"));
    assertFalse(message,
		ScriptProcessor.isScript("no{{script}}"));
    assertFalse(message,
		ScriptProcessor.isScript("\nno {{script}}"));
    assertFalse(message,
		ScriptProcessor.isScript("{{script}}not"));
    assertFalse(message,
		ScriptProcessor.isScript("{{script}}\n n\not"));
  }

  public void testExtractScript() {
    final String message = "Error in extractScript";
    assertEquals(message,
		 "script",
		 ScriptProcessor.extractScript("{{script}}"));
    assertEquals(message,
		 "script",
		 ScriptProcessor.extractScript("  {{script}} "));
    assertEquals(message,
		 "script\n",
		 ScriptProcessor.extractScript(" \n\n {{script\n}}\n"));
    assertEquals(message,
		 "scrip}t\n",
		 ScriptProcessor.extractScript("{{scrip}t\n}}"));
    assertEquals(message,
		 "script\n",
		 ScriptProcessor.extractScript(" \n\n {{script\n}}\n"));
    assertNull(message,
	       ScriptProcessor.extractScript(null));
    assertNull(message,
	       ScriptProcessor.extractScript(""));
    assertNull(message,
	       ScriptProcessor.extractScript("{{script}}\n n\not"));
  }

  private void testEvalFail(final String expression) {
    Object result = null;
    try {
      result = scriptProcessor.eval(expression);
    } catch (Exception exception) {
      return;
    }
    fail("Error in eval(), expression '" +
	 ((expression == null) ? "(null)" : expression) +
	 "' did not throw exception, but returned: " + result);
  }
    
  private void testEvalAsIntFail(final String expression) {
    int result = 0;
    try {
      result = scriptProcessor.evalAsInt(expression);
    } catch (Exception exception) {
      return;
    }
    fail("Error in evalAsInt(), expression '" +
	 ((expression == null) ? "(null)" : expression) +
	 "' did not throw exception, but returned: " + result);
  }
    
  private void testEvalAsLongFail(final String expression) {
    long result = 0;
    try {
      result = scriptProcessor.evalAsLong(expression);
    } catch (Exception exception) {
      return;
    }
    fail("Error in evalAsLong(), expression '" +
	 ((expression == null) ? "(null)" : expression) +
	 "' did not throw exception, but returned: " + result);
  }
    
  private void testEvalAsBigIntegerFail(final String expression) {
    BigInteger result = null;
    try {
      result = scriptProcessor.evalAsBigInteger(expression);
    } catch (Exception exception) {
      return;
    }
    fail("Error in evalAsBigInteger(), expression '" +
	 ((expression == null) ? "(null)" : expression) +
	 "' did not throw exception, but returned: " + result);
  }
    
  private void testEvalAsFloatFail(final String expression) {
    float result = 0;
    try {
      result = scriptProcessor.evalAsFloat(expression);
    } catch (Exception exception) {
      return;
    }
    fail("Error in evalAsFloat(), expression '" +
	 ((expression == null) ? "(null)" : expression) +
	 "' did not throw exception, but returned: " + result);
  }
    
  private void testEvalAsDoubletFail(final String expression) {
    double result = 0;
    try {
      result = scriptProcessor.evalAsDouble(expression);
    } catch (Exception exception) {
      return;
    }
    fail("Error in evalAsDouble(), expression '" +
	 ((expression == null) ? "(null)" : expression) +
	 "' did not throw exception, but returned: " + result);
  }
    
  private void testEvalAsStringFail(final String expression) {
    String result = null;
    try {
      result = scriptProcessor.evalAsString(expression);
    } catch (Exception exception) {
      return;
    }
    fail("Error in evalAsString(), expression '" +
	 ((expression == null) ? "(null)" : expression) +
	 "' did not throw exception, but returned: " + result);
  }
    
  public void testEval() {
    Object result = null;
    String expression = null;
    String message = "eval with expression: ";
    try {
      expression = "1 + 1";
      result = scriptProcessor.eval(expression);
      testResultAndClass(message + expression, 2, result);

      expression = "1.0 + 1.0";
      result = scriptProcessor.eval(expression);
      testResultAndClass(message + expression, 2d, result);

      expression = "streamNumber";
      result = scriptProcessor.eval(expression);
      testResultAndClass(message + expression, 0, result);

      expression = "streamLength";
      result = scriptProcessor.eval(expression);
      testResultAndClass(message + expression, 0, result);

      expression = "totalLength";
      result = scriptProcessor.eval(expression);
      testResultAndClass(message + expression, 0, result);

      final BigInteger five = BigInteger.valueOf(5);
      final Variable t = new Variable("t");
      t.setValue(five);
      inputTreeProcessor.getVariables().put("t", t);
      expression = "t";
      result = scriptProcessor.eval(expression);
      testResultAndClass(message + expression, five, result);
      expression = "t + 1";
      result = scriptProcessor.eval(expression);
      testResultAndClass(message + expression, 6d, result);

      expression = "t = 3";
      result = scriptProcessor.eval(expression);
      assertEquals(message + expression,
		   3,
		   inputTreeProcessor.getVariables()
		   .get("t").getValue().intValue());

      scriptProcessor.putValue(6);
      expression = "val";
      result = scriptProcessor.eval(expression);
      assertEquals(message + expression, 6, result);
	    
      expression = "''";
      result = scriptProcessor.eval(expression);
      testResultAndClass(message + expression, "", result);

      expression = "";
      result = scriptProcessor.eval(expression);
      assertNull(message + "(empty)", result);

      testEvalFail(null);
      testEvalFail("e");
	    
      message = "evalAsInt with expression: ";
	    
      expression = "1";
      assertEquals(message + expression,
		   1,
		   scriptProcessor.evalAsInt(expression));

      expression = "{{1}}";
      assertEquals(message + expression,
		   1,
		   scriptProcessor.evalAsInt(expression));

      expression = "{{\n 1 \n}}";
      assertEquals(message + expression,
		   1,
		   scriptProcessor.evalAsInt(expression));

      expression = "{{ 1.2 }}";
      assertEquals(message + expression,
		   1,
		   scriptProcessor.evalAsInt(expression));

      expression = "{{ 1.7 }}";
      assertEquals(message + expression,
		   2,
		   scriptProcessor.evalAsInt(expression));

      expression = "{{ -1.2 }}";
      assertEquals(message + expression,
		   -1,
		   scriptProcessor.evalAsInt(expression));

      expression = "{{ -1.9 }}";
      assertEquals(message + expression,
		   -2,
		   scriptProcessor.evalAsInt(expression));

      expression = "{{ var x = 9; x / 3.1 }}";
      assertEquals(message + expression,
		   3,
		   scriptProcessor.evalAsInt(expression));

      expression = "{{ var x = 9;\nx / 3.1 }}";
      assertEquals(message + expression,
		   3,
		   scriptProcessor.evalAsInt(expression));

      testEvalAsIntFail(null);
      testEvalAsIntFail("");
      testEvalAsIntFail(" ");
      testEvalAsIntFail("\n");
      testEvalAsIntFail("e");
      testEvalAsIntFail("541254852132562");
	    
      message = "evalAsLong with expression: ";
	    
      expression = "1";
      assertEquals(message + expression,
		   1,
		   scriptProcessor.evalAsLong(expression));

      expression = "{{1}}";
      assertEquals(message + expression,
		   1,
		   scriptProcessor.evalAsLong(expression));

      expression = "{{\n 1 \n}}";
      assertEquals(message + expression,
		   1,
		   scriptProcessor.evalAsLong(expression));

      expression = "{{ 1.2 }}";
      assertEquals(message + expression,
		   1,
		   scriptProcessor.evalAsLong(expression));

      expression = "{{ 1.7 }}";
      assertEquals(message + expression,
		   2,
		   scriptProcessor.evalAsLong(expression));

      expression = "{{ -1.2 }}";
      assertEquals(message + expression,
		   -1,
		   scriptProcessor.evalAsLong(expression));

      expression = "{{ -1.9 }}";
      assertEquals(message + expression,
		   -2,
		   scriptProcessor.evalAsLong(expression));

      expression = "{{ var x = 9; x / 3.1 }}";
      assertEquals(message + expression,
		   3,
		   scriptProcessor.evalAsLong(expression));

      expression = "{{ var x = 9;\nx / 3.1 }}";
      assertEquals(message + expression,
		   3,
		   scriptProcessor.evalAsLong(expression));

      testEvalAsLongFail(null);
      testEvalAsLongFail("");
      testEvalAsLongFail(" ");
      testEvalAsLongFail("\n");
      testEvalAsLongFail("e");
      testEvalAsLongFail("541254852132562544158652145952");
	    
      message = "evalAsBigInteger with expression: ";
	    
      expression = "1";
      assertEquals(message + expression,
		   BigInteger.ONE,
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{1}}";
      assertEquals(message + expression,
		   BigInteger.ONE,
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{\n 1 \n}}";
      assertEquals(message + expression,
		   BigInteger.ONE,
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{ 1.2 }}";
      assertEquals(message + expression,
		   BigInteger.ONE,
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{ 1.7 }}";
      assertEquals(message + expression,
		   BigInteger.valueOf(2),
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{ -1.2 }}";
      assertEquals(message + expression,
		   BigInteger.valueOf(-1),
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{ -1.9 }}";
      assertEquals(message + expression,
		   BigInteger.valueOf(-2),
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{ var x = 9; x / 3.1 }}";
      assertEquals(message + expression,
		   BigInteger.valueOf(3),
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{ var x = 9;\nx / 3.1 }}";
      assertEquals(message + expression,
		   BigInteger.valueOf(3),
		   scriptProcessor.evalAsBigInteger(expression));

      final BigInteger big = BigInteger.ONE.shiftLeft(500);
      scriptProcessor.putValue(big);
      expression = "{{ val }}";
      assertEquals(message + expression,
		   big,
		   scriptProcessor.evalAsBigInteger(expression));
	    
      expression = "{{ val.negate().negate() }}";
      assertEquals(message + expression,
		   big,
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{ BigInteger.valueOf(10) }}";
      assertEquals(message + expression,
		   BigInteger.TEN,
		   scriptProcessor.evalAsBigInteger(expression));

      expression = "{{ BigInteger.valueOf(5).multiply(" +
	"BigInteger.ONE.add(BigInteger.ONE)) }}";
      assertEquals(message + expression,
		   BigInteger.TEN,
		   scriptProcessor.evalAsBigInteger(expression));

      testEvalAsBigIntegerFail(null);
      testEvalAsBigIntegerFail("");
      testEvalAsBigIntegerFail(" ");
      testEvalAsBigIntegerFail("\n");
      testEvalAsBigIntegerFail("e");

      final double delta = 1e-6;
	    
      message = "evalAsFloat with expression: ";
	    
      expression = "1.0";
      assertEquals(message + expression,
		   1.0,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{1.0}}";
      assertEquals(message + expression,
		   1.0,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{\n 1.0 \n}}";
      assertEquals(message + expression,
		   1.0,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ 1.2 }}";
      assertEquals(message + expression,
		   1.2,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ 1.7 }}";
      assertEquals(message + expression,
		   1.7,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ -1.2 }}";
      assertEquals(message + expression,
		   -1.2,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ -1.9 }}";
      assertEquals(message + expression,
		   -1.9,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ var x = 9; x / 3.1 }}";
      assertEquals(message + expression,
		   (9 / 3.1),
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ var x = 9;\nx / 3.1 }}";
      assertEquals(message + expression,
		   (9 / 3.1),
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      testEvalAsFloatFail(null);
      testEvalAsFloatFail("");
      testEvalAsFloatFail(" ");
      testEvalAsFloatFail("\n");
      testEvalAsFloatFail("e");
	    
      message = "evalAsDouble with expression: ";
	    
      expression = "1.0";
      assertEquals(message + expression,
		   1.0,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{1.0}}";
      assertEquals(message + expression,
		   1.0,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{\n 1.0 \n}}";
      assertEquals(message + expression,
		   1.0,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ 1.2 }}";
      assertEquals(message + expression,
		   1.2,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ 1.7 }}";
      assertEquals(message + expression,
		   1.7,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ -1.2 }}";
      assertEquals(message + expression,
		   -1.2,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ -1.9 }}";
      assertEquals(message + expression,
		   -1.9,
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ var x = 9; x / 3.1 }}";
      assertEquals(message + expression,
		   (9 / 3.1),
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      expression = "{{ var x = 9;\nx / 3.1 }}";
      assertEquals(message + expression,
		   (9 / 3.1),
		   scriptProcessor.evalAsFloat(expression),
		   delta);

      testEvalAsFloatFail(null);
      testEvalAsFloatFail("");
      testEvalAsFloatFail(" ");
      testEvalAsFloatFail("\n");
      testEvalAsFloatFail("e");
	    
      message = "evalAsString with expression: ";
	    
      expression = "";
      assertEquals(message + expression,
		   "",
		   scriptProcessor.evalAsString(expression));

      expression = " ";
      assertEquals(message + expression,
		   "",
		   scriptProcessor.evalAsString(expression));

      expression = "1";
      assertEquals(message + expression,
		   "1",
		   scriptProcessor.evalAsString(expression));

      expression = "{{1}} \n";
      assertEquals(message + expression,
		   "1",
		   scriptProcessor.evalAsString(expression));

      expression = " 1\n  \n ";
      assertEquals(message + expression,
		   "1",
		   scriptProcessor.evalAsString(expression));

      expression = "\n{{ 1\n  \n }}";
      assertEquals(message + expression,
		   "1",
		   scriptProcessor.evalAsString(expression));

      expression = " \n'  ' \n ";
      assertEquals(message + expression,
		   "'  '",
		   scriptProcessor.evalAsString(expression));

      expression = "{{ \n'  ' \n }}";
      assertEquals(message + expression,
		   "  ",
		   scriptProcessor.evalAsString(expression));

      testEvalAsStringFail(null);
	    
    } catch (Exception exception) {
      fail("Error in eval(), expression: " + expression +
	   ", exception: " + exception.getMessage());
    }
  }
}
