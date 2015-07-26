package cz.pecina.bin.bitwriter;

import java.net.URL;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import junit.framework.TestCase;

public class TestProcessor extends TestCase {

    protected URL crcFileURL;
    protected PresetCrcModels presetCrcModels;
    protected Parameters parameters;

    @Override
    protected void setUp() throws PresetCrcModelsException,
				  ParametersException,
				  IOException {
	crcFileURL = BitWriter.class.getResource("crc.xml");
	presetCrcModels = new PresetCrcModels(
	    BitWriter.class.getResourceAsStream("crc.xml"));
	parameters = new Parameters();
    }

    private String resourceToString(final String name
				    ) throws IOException {
	BufferedReader reader;
	try {
	    reader = new BufferedReader(
	        new FileReader(
		    new File(getClass().getResource(name).toURI())));
	} catch (NullPointerException |
		 IOException |
		 URISyntaxException exception) {
	    System.err.println(name);
	    return null;
	}
	final StringBuilder string = new StringBuilder();
	while (true) {
	    String s;
	    try {
		s = reader.readLine();
	    } catch (IOException exception) {
		break;
	    }
	    if (s == null) {
		break;
	    }
	    string.append(s + "\n");
	}
	reader.close();
	return string.toString();
    }
	
    private boolean testFile(final String fileName) {
	final String inPrefix = "input/";
	final String outPrefix = "output/";
	String outputString = null;
	try {
	    outputString = resourceToString(
	        (outPrefix + fileName).replace(".xml", ".hex"));
	} catch (IOException exception) {
	    fail("Failed to process file '" + (outPrefix + fileName) + "'");
	}
	int outputLength = 0;
	byte[] outputBytes = null;
	if (outputString.length() > 0) {
	    final String [] outputStringArray =
		outputString.trim().split("\\s+");
	    outputLength = outputStringArray.length;
	    if (outputStringArray[0].length() == 0) {
		outputLength = 0;
	    } else {
		outputBytes = new byte[outputLength];
		for (int i = 0; i < outputLength; i++) {
		    outputBytes[i] = (byte)(Integer.valueOf(
		        outputStringArray[i], 16) & 0xff);
		}
	    }
	}
	final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	try (InputStream inputStream = getClass()
	     .getResourceAsStream(inPrefix + fileName)) {
	    (new InputTreeProcessor(outputStream))
		.process(parameters,
			 inputStream,
			 presetCrcModels,
			 System.err);
	} catch (ProcessorException| IOException  exception) {
	    return false;
	}
	final byte[] outputByteArray = outputStream.toByteArray();
	if (outputByteArray.length != outputLength) {
	    return false;
	}
	for (int i = 0; i < outputLength; i++) {
	    if (outputByteArray[i] != outputBytes[i]) {
	    	return false;
	    }
	}
	return true;
    }

    private int testFileFail(final String fileName) {
	final String prefix = "input/";
	try (InputStream inputStream = getClass()
	     .getResourceAsStream(prefix + fileName)) {
	    (new InputTreeProcessor(new ByteArrayOutputStream()))
		.process(parameters,
			 inputStream,
			 presetCrcModels,
			 System.err);
	    return 1;
	} catch (Exception exception) {
	    return ((exception instanceof ProcessorException) ? 0 : 2);
	}
    }

    public void testInputTreeProcessor() {
    	final String prefix = "test/res/cz/pecina/bin/bitwriter/input";
    	final Pattern testedPattern =
	    Pattern.compile("^([^-]+)-([^-]+)-([^.]+)\\.xml$"); 
    	final Pattern failPattern = Pattern.compile("^.*-fail.xml$"); 
    	final File inputDir = new File(prefix);
    	for (String fileName: inputDir.list()) {
    	    final Matcher testedMatcher = testedPattern.matcher(fileName);
    	    final Matcher failMatcher = failPattern.matcher(fileName);
    	    if (!testedMatcher.matches()) {
    		continue;
    	    }
    	    final MatchResult result = testedMatcher.toMatchResult();
    	    if (failMatcher.matches()) {
    		final int r = testFileFail(fileName);
    		if (r == 1) {
    		    fail("Test file '" + fileName + "' did not fail");
    		} else if (r == 2) {
    		    fail("Test file '" + fileName +
			 "' failed with uncaught exception");
    		}
    	    } else {
    		assertTrue("Test file '" + fileName +
			   "' failed", testFile(fileName));
    	    }
    	}
    }
    
    public void testInternalModels() {
	assertNotNull("crcFileURL is null", crcFileURL);
	assertNotNull("models is null", presetCrcModels);
    }

    public void testAllModels() {
    	for (CrcModel model: presetCrcModels) {
    	    if (!model.hasCheck()) {
    	    	continue;
    	    }
    	    final Crc c = new Crc(model);
    	    c.update("123456789".getBytes());
    	    assertTrue("AllModels failed on model: " + model.getId(),
		       c.getRegister().equals(model.getCheck()));
    	}
    }
}
