package cz.pecina.bin.bitwriter;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;

public class TestBitWriter extends TestCase {

  private static final String TEST_RESOURCES_PREFIX
    = "test/res/cz/pecina/bin/bitwriter/";

  @Override
  protected void setUp() throws Exception {
  }

  private class Result {
    int exitCode;
    byte[] out;
    String err;
  }

  private Result test(final String[] args, final String siString) {
    InputStream si = null;
    if (siString != null) {
      si = new ByteArrayInputStream(siString.getBytes());
    }
    final ByteArrayOutputStream sos = new ByteArrayOutputStream();
    final PrintStream so = new PrintStream(sos);
    final ByteArrayOutputStream ses = new ByteArrayOutputStream();
    final PrintStream se = new PrintStream(ses);
    final Result r = new Result();
    r.exitCode = BitWriter.process(args, si, so, se);
    r.out = sos.toByteArray();
    r.err = ses.toString();
    return r;
  }

  public void testProcess() {
    Result r;
	
    r = test(new String[] {"--erroneous-parameter"}, null);
    assertEquals("Error in process, erroneous parameter (1)",
		 Constants.EXIT_CODE_ERROR_IN_PARAMETERS,
		 r.exitCode);
    assertEquals("Error in process, erroneous parameter (2)",
		 0,
		 r.out.length);
    assertTrue("Error in process, erroneous parameter (3)",
	       (r.err.length() > 1));
	
    r = test(new String[] {"-?"}, null);
    assertEquals("Error in process, help (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, help (2)",
		 0,
		 r.out.length);
    assertTrue("Error in process, help (3)",
	       (r.err.length() > 1));
	
    r = test(new String[] {"--help"}, null);
    assertEquals("Error in process, help (4)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, help (5)",
		 0,
		 r.out.length);
    assertTrue("Error in process, help (6)",
	       (r.err.length() > 1));
	
    r = test(new String[] {"-V"}, null);
    assertEquals("Error in process, version (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, version (2)",
		 0,
		 r.out.length);
    assertEquals("Error in process, version (3)",
		 String.format("@VERSION@%n"),
		 r.err);
	
    r = test(new String[] {"--version"}, null);
    assertEquals("Error in process, version (4)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, version (5)",
		 0,
		 r.out.length);
    assertEquals("Error in process, version (6)",
		 String.format("@VERSION@%n"),
		 r.err);

    r = test(new String[] {"-l"}, null);
    assertEquals("Error in process, list models (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, list models (2)",
		 0,
		 r.out.length);
    assertTrue("Error in process, list models (3)",
	       (r.err.length() > 1));
	
    r = test(new String[] {"--list-crc-models"}, null);
    assertEquals("Error in process, list models (4)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, list models (5)",
		 0,
		 r.out.length);
    assertTrue("Error in process, list models (6)",
	       (r.err.length() > 1));

    r = test(new String[] {"-s", "<stream>0x55</stream>"}, null);
    assertEquals("Error in process, string (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, string (2)",
		 1,
		 r.out.length);
    assertEquals("Error in process, string (3)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, string (4)",
		 0,
		 r.err.length());

    r = test(new String[] {
	"--string",
	"<stream>0x55</stream>"},
      null);
    assertEquals("Error in process, string (5)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, string (6)",
		 1,
		 r.out.length);
    assertEquals("Error in process, string (7)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, string (8)", 0, r.err.length());

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "-s",
       "<stream>0xaa</stream>"},
	     null);
    assertEquals("Error in process, multiple strings (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, multiple strings (2)",
		 2,
		 r.out.length);
    assertEquals("Error in process, multiple strings (3)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, multiple strings (4)",
		 0xaa,
		 r.out[1] & 0xff);
    assertEquals("Error in process, multiple strings (5)",
		 0,
		 r.err.length());

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "<stream>0xaa</stream>"},
	     null);
    assertEquals("Error in process, multiple strings (5)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, multiple strings (6)",
		 2,
		 r.out.length);
    assertEquals("Error in process, multiple strings (7)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, multiple strings (8)",
		 0xaa,
		 (r.out[1] & 0xff));
    assertEquals("Error in process, multiple strings (9)",
		 0,
		 r.err.length());

    r = test(new String[] {"-s"}, null);
    assertEquals("Error in process, empty string (1)",
		 Constants.EXIT_CODE_ERROR_IN_PARAMETERS,
		 r.exitCode);
    assertEquals("Error in process, empty string (2)",
		 0,
		 r.out.length);
    assertTrue("Error in process, empty string (3)",
	       (r.err.length() > 1));

    r = test(new String[] {"-s", "error"}, null);
    assertEquals("Error in process, invalid string (1)",
		 Constants.EXIT_CODE_PROCESSING_ERROR,
		 r.exitCode);
    assertEquals("Error in process, invalid string (2)",
		 0,
		 r.out.length);
    assertTrue("Error in process, invalid string (3)",
	       (r.err.length() > 1));

    r = test(new String[]
      {"-s",
       "<stream extra-attribute=\"true\">0x55</stream>"},
	     null);
    assertEquals("Error in process, no schema (9)",
		 Constants.EXIT_CODE_PROCESSING_ERROR,
		 r.exitCode);
    assertEquals("Error in process, no schema (10)",
		 0,
		 r.out.length);
    assertTrue("Error in process, no schema (11)",
	       (r.err.length() > 0));

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "-c",
       TEST_RESOURCES_PREFIX + "crc.xml"},
	     null);
    assertEquals("Error in process, CRC file (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, CRC file (2)",
		 1,
		 r.out.length);
    assertEquals("Error in process, CRC file (3)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, CRC file (4)",
		 0,
		 r.err.length());

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "--crc-file",
       TEST_RESOURCES_PREFIX + "crc.xml"},
	     null);
    assertEquals("Error in process, CRC file (5)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, CRC file (6)",
		 1,
		 r.out.length);
    assertEquals("Error in process, CRC file (7)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, CRC file (8)",
		 0,
		 r.err.length());

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "-c",
       TEST_RESOURCES_PREFIX + "crc-missing.xml"},
	     null);
    assertEquals("Error in process, missing CRC file (1)",
		 Constants.EXIT_CODE_IO_ERROR,
		 r.exitCode);
    assertEquals("Error in process, missing CRC file (2)",
		 0,
		 r.out.length);
    assertTrue("Error in process, missing CRC file (3)",
	       (r.err.length() > 1));

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "-c",
       TEST_RESOURCES_PREFIX + "crc-bad.xml"},
	     null);
    assertEquals("Error in process, bad CRC file (1)",
		 Constants.EXIT_CODE_ERROR_IN_PRESET_CRC_MODELS,
		 r.exitCode);
    assertEquals("Error in process, bad CRC file (2)",
		 0,
		 r.out.length);
    assertTrue("Error in process, bad CRC file (3)",
	       (r.err.length() > 1));

    r = test(new String[]
      {"-c",
       "-s",
       "<stream>0x55</stream>"},
	     null);
    assertEquals("Error in process, missing CRC file parameter (1)",
		 Constants.EXIT_CODE_ERROR_IN_PARAMETERS,
		 r.exitCode);
    assertEquals("Error in process, missing CRC file parameter (2)",
		 0,
		 r.out.length);
    assertTrue("Error in process, missing CRC file parameter (3)",
	       (r.err.length() > 1));

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "-x"},
	     null);
    assertEquals("Error in process, hex mode (1)",
		 0,
		 r.exitCode);
    assertTrue("Error in process, hex mode (2)",
	       (r.out.length > 1));
    assertEquals("Error in process, hex mode (3)",
		 String.format("55%n"),
		 new String(r.out));
    assertEquals("Error in process, hex mode (4)",
		 0,
		 r.err.length());

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "--hex-mode"},
	     null);
    assertEquals("Error in process, hex mode (5)",
		 0,
		 r.exitCode);
    assertTrue("Error in process, hex mode (6)",
	       (r.out.length > 1));
    assertEquals("Error in process, hex mode (7)",
		 String.format("55%n"),
		 new String(r.out));
    assertEquals("Error in process, hex mode (8)",
		 0,
		 r.err.length());
	
    r = test(new String[]
      {"-s",
       "<stream>0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16</stream>",
       "-x"},
	     null);
    assertEquals("Error in process, hex mode (9)",
		 0,
		 r.exitCode);
    assertTrue("Error in process, hex mode (10)",
	       (r.out.length > 1));
    assertEquals("Error in process, hex mode (11)",
		 String.format(
			       "00 01 02 03 04 05 06 07" +
			       " 08 09 0a 0b 0c 0d 0e 0f%n10%n"),
		 new String(r.out));
    assertEquals("Error in process, hex mode (12)",
		 0,
		 r.err.length());

    r = test(new String[]
      {"-s",
       "<stream></stream>",
       "-x"},
	     null);
    assertEquals("Error in process, hex mode (13)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, hex mode (14)",
		 0,
		 r.out.length);
    assertEquals("Error in process, hex mode (15)",
		 0,
		 r.err.length());

    r = test(new String[]
      {"-s",
       "<stream></stream>"},
	     null);
    assertEquals("Error in process, empty stream (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, empty stream (2)",
		 0,
		 r.out.length);
    assertEquals("Error in process, empty stream (3)",
		 0,
		 r.err.length());

    r = test(new String[] {},
	     Constants.XML_PREAMBLE +
	     "<stream>0x55</stream>" +
	     Constants.XML_POSTAMBLE);
    assertEquals("Error in process, input from STDIN (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, input from STDIN (2)",
		 1,
		 r.out.length);
    assertEquals("Error in process, input from STDIN (3)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, input from STDIN (4)",
		 0,
		 r.err.length());

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>"
      },
	     Constants.XML_PREAMBLE +
	     "<stream>0xaa</stream>" +
	     Constants.XML_POSTAMBLE);
    assertEquals("Error in process, string and STDIN (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, string and STDIN (2)",
		 1,
		 r.out.length);
    assertEquals("Error in process, string and STDIN (3)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, string and STDIN (4)",
		 0,
		 r.err.length());

    r = test(new String[]
      {"-s",
       "<stream>0x55</stream>",
       "--",
       TEST_RESOURCES_PREFIX + "test1.xml"},
	     null);
    assertEquals("Error in process, string and file (1)",
		 0,
		 r.exitCode);
    assertEquals("Error in process, string and file (2)",
		 2,
		 r.out.length);
    assertEquals("Error in process, string and file (3)",
		 0x55,
		 (r.out[0] & 0xff));
    assertEquals("Error in process, string and file (4)",
		 0xaa,
		 (r.out[1] & 0xff));
    assertEquals("Error in process, string and file (5)",
		 0,
		 r.err.length());

    String fn = null;
    File f = null;
    try {
      f = File.createTempFile("test", ".tmp");
      fn = f.getName();
      f.delete();
    } catch (IOException exception) {
      fail("Error in process, output to file (1)");
    }
    try {
      r = test(new String[]
	{"-s",
	 "<stream>0x55</stream>",
	 "-o",
	 fn},
	       null);
      f = new File(fn);
      assertEquals("Error in process, output to file (2)",
		   0,
		   r.exitCode);
      assertEquals("Error in process, output to file (3)",
		   0,
		   r.out.length);
      assertEquals("Error in process, output to file (4)",
		   f.length(),
		   1);
      assertEquals("Error in process, output to file (5)",
		   (Util.fileToByteArray(fn)[0] & 0xff),
		   0x55);
      assertEquals("Error in process, output to file (6)",
		   0,
		   r.err.length());
    } catch (IOException exception) {
      f.delete();
      fail("Error in process, output to file (7)");
    }
    try {
      r = test(new String[]
	{"-s",
	 "<stream>0xaa</stream>",
	 "-o",
	 fn},
	       null);
      f = new File(fn);
      assertEquals("Error in process, output to file (8)",
		   0,
		   r.exitCode);
      assertEquals("Error in process, output to file (9)",
		   0,
		   r.out.length);
      assertEquals("Error in process, output to file (10)",
		   f.length(),
		   1);
      assertEquals("Error in process, output to file (11)",
		   (Util.fileToByteArray(fn)[0] & 0xff),
		   0xaa);
      assertEquals("Error in process, output to file (12)",
		   0,
		   r.err.length());
    } catch (IOException exception) {
      fail("Error in process, output to file (13)");
    } finally {
      f.delete();
    }
    try {
      r = test(new String[]
	{"-s",
	 "<stream>0x33</stream>",
	 "--output-file",
	 fn},
	       null);
      f = new File(fn);
      assertEquals("Error in process, output to file (14)",
		   0,
		   r.exitCode);
      assertEquals("Error in process, output to file (15)",
		   0,
		   r.out.length);
      assertEquals("Error in process, output to file (16)",
		   f.length(),
		   1);
      assertEquals("Error in process, output to file (17)",
		   (Util.fileToByteArray(fn)[0] & 0xff),
		   0x33);
      assertEquals("Error in process, output to file (18)",
		   0,
		   r.err.length());
    } catch (IOException exception) {
      fail("Error in process, output to file (19)");
    } finally {
      f.delete();
    }
  }
}
