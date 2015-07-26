package cz.pecina.bin.bitwriter;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import junit.framework.TestCase;

public class TestProcessor extends TestCase {

    protected PresetCrcModels presetCrcModels;
    protected Parameters parameters;

    @Override
    protected void setUp() throws PresetCrcModelsException,
				  ParametersException,
				  IOException {
	presetCrcModels = new PresetCrcModels(
	    BitWriter.class.getResourceAsStream("crc.xml"));
	parameters = new Parameters();
    }

    private boolean testFile(final String fileName) {
	final String inPrefix = "input/";
	final String outPrefix = "output/";
	final List<Integer> outputBytes = new ArrayList<>();
	try (InputStreamReader streamReader = new InputStreamReader(
	     getClass().getResourceAsStream((outPrefix + fileName)
	     .replace(".xml", ".hex")))) {
	    final StreamTokenizer outputTokenizer =
		new StreamTokenizer(streamReader);
	    outputTokenizer.resetSyntax();
	    outputTokenizer.wordChars('a', 'f');
	    outputTokenizer.wordChars('A', 'F');
	    outputTokenizer.wordChars('0', '9');
	    outputTokenizer.whitespaceChars(0, ' ');
	    while (outputTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
		outputBytes.add(Integer.valueOf(outputTokenizer.sval, 16));
	    }
	} catch (IOException exception) {
	    fail("Failed to tokenize file '" + (outPrefix + fileName) + "'");
	}
	final int outputLength = outputBytes.size();
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
	    if ((outputByteArray[i] & 0xff) != outputBytes.get(i)) {
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
