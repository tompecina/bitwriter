package cz.pecina.bin.bitwriter;

import java.math.BigInteger;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import junit.framework.TestCase;

public class TestUtil extends TestCase {

  @Override
  protected void setUp() {
  }

  public void testReflect() {
    BigInteger input;
    int width;
    BigInteger output;

    input = BigInteger.ZERO;
    output = BigInteger.ZERO;
    width = 1;
    assertEquals("Error in reflect(), value: " +
		 Util.bigIntegerToString(input) +
		 ", width: " + width,
		 output,
		 Util.reflect(input, width));

    input = BigInteger.ONE;
    output = BigInteger.ONE;
    width = 1;
    assertEquals("Error in reflect(), value: " +
		 Util.bigIntegerToString(input) +
		 ", width: " + width,
		 output,
		 Util.reflect(input, width));

    input = BigInteger.ONE;
    output = BigInteger.ONE.shiftLeft(4);
    width = 5;
    assertEquals("Error in reflect(), value: " +
		 Util.bigIntegerToString(input) +
		 ", width: " + width,
		 output,
		 Util.reflect(input, width));

    input = BigInteger.valueOf(0x55);
    output = BigInteger.valueOf(0xaa);
    width = 8;
    assertEquals("Error in reflect(), value: " +
		 Util.bigIntegerToString(input) +
		 ", width: " + width,
		 output,
		 Util.reflect(input, width));

    input = BigInteger.valueOf(0x55);
    output = input;
    width = 7;
    assertEquals("Error in reflect(), value: " +
		 Util.bigIntegerToString(input) +
		 ", width: " + width,
		 output,
		 Util.reflect(input, width));

    input = BigInteger.ONE.add(BigInteger.ONE);
    output = BigInteger.ONE.shiftLeft(38);
    width = 40;
    assertEquals("Error in reflect(), value: " +
		 Util.bigIntegerToString(input) +
		 ", width: " + width,
		 output,
		 Util.reflect(input, width));

    input = BigInteger.ZERO;
    width = 0;
    assertNull("Error in reflect(), value: " +
	       Util.bigIntegerToString(input) +
	       ", width: " + width,
	       Util.reflect(input, width));
  }

  public void testMakeMask() {
    int width;
    BigInteger output;

    output = BigInteger.ONE;
    width = 1;
    assertEquals("Error in makeMask(), width: " + width,
		 output,
		 Util.makeMask(width));

    output = Constants.FF;
    width = 8;
    assertEquals("Error in makeMask(), width: " + width,
		 output,
		 Util.makeMask(width));

    width = 0;
    assertNull("Error in makeMask(), width: " + width,
	       Util.makeMask(width));
  }

  public void testStreamToString() {
    InputStream is = new ByteArrayInputStream("test".getBytes());
    try {
      assertEquals("Test stream reading error",
		   "test",
		   Util.streamToString(is));
    } catch (Exception exception) {
      fail("Exception on stream reading: " + exception.getMessage());
    }
    is = new ByteArrayInputStream("Příliš žluťoučký kůň".getBytes());
    try {
      assertEquals("Test stream reading error",
		   "Příliš žluťoučký kůň",
		   Util.streamToString(is));
    } catch (Exception exception) {
      fail("Exception on stream reading: " + exception.getMessage());
    }
    try {
      Util.streamToString(null);
    } catch (Exception exception) {
      return;
    }
    fail("Empty input stream reading error");
  }

  public void testTf() {
    assertEquals("tf() error", "T", Util.tf(true));
    assertEquals("tf() error", "F", Util.tf(false));
  }

  public void testHyphensToUnderscores() {
    assertEquals("hyphensToUnderscores() error",
		 "A_A",
		 Util.hyphensToUnderscores("A-A"));
    assertNull("hyphensToUnderscores(null) error",
	       Util.hyphensToUnderscores(null));
  }

  private void testStringToBigInteger(final String string,
				      final BigInteger expected) {
    BigInteger actual = null;
    try {
      actual = Util.stringToBigInteger(string);
    } catch (Exception exception) {
      fail("stringToBigInteger() error, string: " + string +
	   ", exception: " + exception.getMessage());
    }
    assertEquals("stringToBigInteger() error, string: " + string +
		 ", yielded result: " + Util.bigIntegerToString(actual),
		 expected,
		 actual);
  }

  private void testStringToBigInteger(final String string,
				      final int expected) {
    testStringToBigInteger(string, BigInteger.valueOf(expected));
  }

  private void testStringToBigIntegerFail(final String string) {
    try {
      Util.stringToBigInteger(string);
    } catch (Exception exception) {
      return;
    }
    fail("stringToBigInteger() did not fail on: " + string);
  }

  private static final String h =
    "e521f452e1aadc15e025d20c15d2f0a1e2f10e5c2d10e52f0a1d2f5e1f065a4";
  private static final String d =
    "5210256214508465854304304505010304630245214050545820123540056" +
    "658502501";
  private static final String o =
    "3201410232410214745652302156752102450325652017445210267741450" +
    "2632570547521256";
  private static final String b =
    "1011010101011100010101010100101001010101010101111101010101010" +
    "1011010101010101010111111111101010000101000010101010100101010" +
    "1010100101010101000101010100101010101011110010101";

  public void testStringToBigInteger() {
    testStringToBigInteger("0x0", 0);
    testStringToBigInteger("0x000000000", 0);
    testStringToBigInteger("+0x0", 0);
    testStringToBigInteger("-0x0", 0);
    testStringToBigInteger("  0x0 \n ", 0);
    testStringToBigInteger("0x" + h, new BigInteger(h, 16));
    testStringToBigInteger("+0x" + h, new BigInteger(h, 16));
    testStringToBigInteger("-0x" + h, new BigInteger(h, 16).negate());
    testStringToBigInteger("1", 1);
    testStringToBigInteger("+11", 11);
    testStringToBigInteger("-65", -65);
    testStringToBigInteger(d, new BigInteger(d));
    testStringToBigInteger("+" + d, new BigInteger(d));
    testStringToBigInteger("-" + d, new BigInteger(d).negate());
    testStringToBigInteger("0", 0);
    testStringToBigInteger("00000000000", 0);
    testStringToBigInteger("+011", 011);
    testStringToBigInteger("-065", -065);
    testStringToBigInteger("0" + o, new BigInteger(o, 8));
    testStringToBigInteger("+0" + o, new BigInteger(o, 8));
    testStringToBigInteger("-0" + o, new BigInteger(o, 8).negate());
    testStringToBigInteger("0b0", 0);
    testStringToBigInteger("+0b0", 0);
    testStringToBigInteger("-0b0", 0);
    testStringToBigInteger("0b000000000000000", 0);
    testStringToBigInteger("+0b11", 0b11);
    testStringToBigInteger("-0b01100101", -0b01100101);
    testStringToBigInteger("0b" + b, new BigInteger(b, 2));
    testStringToBigInteger("+0b" + b, new BigInteger(b, 2));
    testStringToBigInteger("-0b" + b, new BigInteger(b, 2).negate());
    testStringToBigIntegerFail(null);
    testStringToBigIntegerFail("0x");
    testStringToBigIntegerFail("+-0x5");
    testStringToBigIntegerFail("-+0x5");
    testStringToBigIntegerFail("--0x0");
    testStringToBigIntegerFail("+-5");
    testStringToBigIntegerFail("-+5");
    testStringToBigIntegerFail("--0");
    testStringToBigIntegerFail("+-0");
    testStringToBigIntegerFail("-+0");
    testStringToBigIntegerFail("--000");
    testStringToBigIntegerFail("+-0b");
    testStringToBigIntegerFail("+-0b5");
    testStringToBigIntegerFail("-+0b5");
    testStringToBigIntegerFail("--0b0");
  }

  private void testStringToInt(final String string,
			       final int expected) {
    int actual = 0;
    try {
      actual = Util.stringToInt(string);
    } catch (Exception exception) {
      fail("stringToInt() error, string: " + string +
	   ", exception: " + exception.getMessage());
    }
    assertEquals("stringToInt() error, string: " + string +
		 ", yielded result: " + actual,
		 expected,
		 actual);
  }

  private void testStringToIntFail(final String string) {
    try {
      Util.stringToInt(string);
    } catch (Exception exception) {
      return;
    }
    fail("stringToInt() did not fail on: " + string);
  }

  public void testStringToInt() {
    testStringToInt("0x0", 0);
    testStringToInt("0x000000000", 0);
    testStringToInt("+0x0", 0);
    testStringToInt("-0x0", 0);
    testStringToInt("  0x0 \n ", 0);
    testStringToInt("1", 1);
    testStringToInt("+11", 11);
    testStringToInt("-65", -65);
    testStringToInt("0", 0);
    testStringToInt("00000000000", 0);
    testStringToInt("+011", 011);
    testStringToInt("-065", -065);
    testStringToInt("0b0", 0);
    testStringToInt("+0b0", 0);
    testStringToInt("-0b0", 0);
    testStringToInt("0b000000000000000", 0);
    testStringToInt("+0b11", 0b11);
    testStringToInt("-0b01100101", -0b01100101);
    testStringToIntFail("{{1}}");
    testStringToIntFail(null);
    testStringToIntFail("0x");
    testStringToIntFail("+-0x5");
    testStringToIntFail("-+0x5");
    testStringToIntFail("--0x0");
    testStringToIntFail("+-5");
    testStringToIntFail("-+5");
    testStringToIntFail("--0");
    testStringToIntFail("+-0");
    testStringToIntFail("-+0");
    testStringToIntFail("--000");
    testStringToIntFail("+-0b");
    testStringToIntFail("+-0b5");
    testStringToIntFail("-+0b5");
    testStringToIntFail("--0b0");
    testStringToIntFail("0x" + h);
    testStringToIntFail("+0x" + h);
    testStringToIntFail("-0x" + h);
    testStringToIntFail(d);
    testStringToIntFail("+" + d);
    testStringToIntFail("-" + d);
    testStringToIntFail("0" + o);
    testStringToIntFail("+0" + o);
    testStringToIntFail("-0" + o);
    testStringToIntFail("0b" + b);
    testStringToIntFail("+0b" + b);
    testStringToIntFail("-0b" + b);
  }

  private void testStringToLong(final String string,
				final long expected) {
    long actual = 0;
    try {
      actual = Util.stringToLong(string);
    } catch (Exception exception) {
      fail("stringToLong() error, string: " + string +
	   ", exception: " + exception.getMessage());
    }
    assertEquals("stringToLong() error, string: " + string +
		 ", yielded result: " + actual, expected, actual);
  }

  private void testStringToLongFail(final String string) {
    try {
      Util.stringToLong(string);
    } catch (Exception exception) {
      return;
    }
    fail("stringToLong() did not fail on: " + string);
  }

  public void testStringToLong() {
    testStringToLong("0x0", 0);
    testStringToLong("0x000000000", 0);
    testStringToLong("+0x0", 0);
    testStringToLong("-0x0", 0);
    testStringToLong("  0x0 \n ", 0);
    testStringToLong("1", 1);
    testStringToLong("+11", 11);
    testStringToLong("-65", -65);
    testStringToLong("0", 0);
    testStringToLong("00000000000", 0);
    testStringToLong("+011", 011);
    testStringToLong("-065", -065);
    testStringToLong("0b0", 0);
    testStringToLong("+0b0", 0);
    testStringToLong("-0b0", 0);
    testStringToLong("0b000000000000000", 0);
    testStringToLong("+0b11", 0b11);
    testStringToLong("-0b01100101", -0b01100101);
    testStringToLongFail("{{1}}");
    testStringToLongFail(null);
    testStringToLongFail("0x");
    testStringToLongFail("+-0x5");
    testStringToLongFail("-+0x5");
    testStringToLongFail("--0x0");
    testStringToLongFail("+-5");
    testStringToLongFail("-+5");
    testStringToLongFail("--0");
    testStringToLongFail("+-0");
    testStringToLongFail("-+0");
    testStringToLongFail("--000");
    testStringToLongFail("+-0b");
    testStringToLongFail("+-0b5");
    testStringToLongFail("-+0b5");
    testStringToLongFail("--0b0");
    testStringToLongFail("0x" + h);
    testStringToLongFail("+0x" + h);
    testStringToLongFail("-0x" + h);
    testStringToLongFail(d);
    testStringToLongFail("+" + d);
    testStringToLongFail("-" + d);
    testStringToLongFail("0" + o);
    testStringToLongFail("+0" + o);
    testStringToLongFail("-0" + o);
    testStringToLongFail("0b" + b);
    testStringToLongFail("+0b" + b);
    testStringToLongFail("-0b" + b);
  }

  private void testStringToFloat(final String string,
				 final float expected) {
    float actual = 0;
    try {
      actual = Util.stringToFloat(string);
    } catch (Exception exception) {
      fail("stringToFloat() error, string: " + string +
	   ", exception: " + exception.getMessage());
    }
    assertEquals("stringToFloat() error, string: " + string +
		 ", yielded result: " + actual, expected, actual, 1e-6);
  }

  private void testStringToFloatFail(final String string) {
    try {
      Util.stringToFloat(string);
    } catch (Exception exception) {
      return;
    }
    fail("stringToFloat() did not fail on: " + string);
  }

  public void testStringToFloat() {
    testStringToFloat("0", 0);
    testStringToFloat("0.0", 0);
    testStringToFloat("+0", 0);
    testStringToFloat("-0", 0);
    testStringToFloat("  0 \n ", 0);
    testStringToFloat("1", 1);
    testStringToFloat("+11", 11);
    testStringToFloat("-65", -65);
    testStringToFloat("0", 0);
    testStringToFloat("00000000000", 0);
    testStringToFloat("+011", 11);
    testStringToFloat("-065", -65);
    testStringToFloatFail("{{1}}");
    testStringToFloatFail(null);
    testStringToFloatFail("0x");
    testStringToFloatFail("+-0x5");
    testStringToFloatFail("-+0x5");
    testStringToFloatFail("--0x0");
    testStringToFloatFail("+-5");
    testStringToFloatFail("-+5");
    testStringToFloatFail("--0");
    testStringToFloatFail("+-0");
    testStringToFloatFail("-+0");
    testStringToFloatFail("--000");
    testStringToFloatFail("+-0b");
    testStringToFloatFail("+-0b5");
    testStringToFloatFail("-+0b5");
    testStringToFloatFail("--0b0");
    testStringToFloatFail("0x" + h);
    testStringToFloatFail("+0x" + h);
    testStringToFloatFail("-0x" + h);
    testStringToFloatFail("0b" + b);
    testStringToFloatFail("+0b" + b);
    testStringToFloatFail("-0b" + b);
  }

  private void testStringToDouble(final String string,
				  final double expected) {
    double actual = 0;
    try {
      actual = Util.stringToDouble(string);
    } catch (Exception exception) {
      fail("stringToDouble() error, string: " + string +
	   ", exception: " + exception.getMessage());
    }
    assertEquals("stringToDouble() error, string: " + string +
		 ", yielded result: " + actual, expected, actual, 1e-6);
  }

  private void testStringToDoubleFail(final String string) {
    try {
      Util.stringToDouble(string);
    } catch (Exception exception) {
      return;
    }
    fail("stringToDouble() did not fail on: " + string);
  }

  public void testStringToDouble() {
    testStringToDouble("0", 0);
    testStringToDouble("0.0", 0);
    testStringToDouble("+0", 0);
    testStringToDouble("-0", 0);
    testStringToDouble("  0 \n ", 0);
    testStringToDouble("1", 1);
    testStringToDouble("+11", 11);
    testStringToDouble("-65", -65);
    testStringToDouble("0", 0);
    testStringToDouble("00000000000", 0);
    testStringToDouble("+011", 11);
    testStringToDouble("-065", -65);
    testStringToDoubleFail("{{1}}");
    testStringToDoubleFail(null);
    testStringToDoubleFail("0x");
    testStringToDoubleFail("+-0x5");
    testStringToDoubleFail("-+0x5");
    testStringToDoubleFail("--0x0");
    testStringToDoubleFail("+-5");
    testStringToDoubleFail("-+5");
    testStringToDoubleFail("--0");
    testStringToDoubleFail("+-0");
    testStringToDoubleFail("-+0");
    testStringToDoubleFail("--000");
    testStringToDoubleFail("+-0b");
    testStringToDoubleFail("+-0b5");
    testStringToDoubleFail("-+0b5");
    testStringToDoubleFail("--0b0");
    testStringToDoubleFail("0x" + h);
    testStringToDoubleFail("+0x" + h);
    testStringToDoubleFail("-0x" + h);
    testStringToDoubleFail("0b" + b);
    testStringToDoubleFail("+0b" + b);
    testStringToDoubleFail("-0b" + b);
  }

  private void testStringToBoolean(final String string,
				   final boolean expected) {
    boolean actual = false;
    try {
      actual = Util.stringToBoolean(string);
    } catch (Exception exception) {
      fail("stringToBooleant() error, string: " + string +
	   ", exception: " + exception.getMessage());
    }
    assertEquals("stringToBoolean() error, string: " + string +
		 ", yielded result: " + actual, expected, actual);
  }

  private void testStringToBooleanFail(final String string) {
    try {
      Util.stringToBoolean(string);
    } catch (Exception exception) {
      return;
    }
    fail("stringToBoolean() did not fail on: " + string);
  }

  public void testStringToBoolean() {
    testStringToBoolean(" true", true);
    testStringToBoolean("1  ", true);
    testStringToBoolean("\nfalse", false);
    testStringToBoolean("0 \n ", false);
    testStringToBooleanFail(null);
    testStringToBooleanFail("");
    testStringToBooleanFail(" ");
    testStringToBooleanFail("{{true}}");
    testStringToBooleanFail("x");
    testStringToBooleanFail("0+0");
    testStringToBooleanFail("'true'");
  }

  public void testBigIntegerToString() {
    final String message = "testBigIntegerToString() error";
    assertEquals(message,
		 "null",
		 Util.bigIntegerToString(null));
    assertEquals(message,
		 "0x0",
		 Util.bigIntegerToString(BigInteger.ZERO));
    assertEquals(message,
		 "0x1",
		 Util.bigIntegerToString(BigInteger.ONE));
    assertEquals(message,
		 "-0x1",
		 Util.bigIntegerToString(BigInteger.ONE.negate()));
  }
}
