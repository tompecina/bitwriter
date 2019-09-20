package cz.pecina.bin.bitwriter;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.StreamTokenizer;
import java.io.Reader;
import java.io.FileReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;
import junit.framework.TestCase;

public class TestProcessor extends TestCase {

  private static final String PREFIX = "src/test/resources/cz/pecina/bin/bitwriter";

  protected PresetCrcModels presetCrcModels;
  protected Parameters parameters;

  @Override
  protected void setUp() throws PresetCrcModelsException, ParametersException, IOException {
    presetCrcModels = new PresetCrcModels(BitWriter.class.getResourceAsStream("crc.xml"));
    parameters = new Parameters();
  }

  private boolean testFile(final Path inputPath) {
    final String name = inputPath.getFileName().toString();
    final Path outputPath = Paths.get(PREFIX + "/output/" +
                                      name.replace(".xml", ".hex"));
    final List<Integer> outputBytes = new ArrayList<>();
    try (Reader streamReader = new FileReader(outputPath.toFile())) {
      final StreamTokenizer outputTokenizer = new StreamTokenizer(streamReader);
      outputTokenizer.resetSyntax();
      outputTokenizer.wordChars('a', 'f');
      outputTokenizer.wordChars('A', 'F');
      outputTokenizer.wordChars('0', '9');
      outputTokenizer.whitespaceChars(0, ' ');
      while (outputTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
        outputBytes.add(Integer.valueOf(outputTokenizer.sval, 16));
      }
    } catch (IOException exception) {
      fail("Failed to tokenize hex file for '" + name + "'");
    }
    final int outputLength = outputBytes.size();
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (InputStream inputStream =
         new FileInputStream(inputPath.toFile())) {
      (new InputTreeProcessor(outputStream))
          .process(parameters, inputStream, presetCrcModels, System.err);
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

  private int testFileFail(final Path path) {
    final String prefix = "input/";
    try (InputStream inputStream =
         new FileInputStream(path.toFile())) {
      (new InputTreeProcessor(new ByteArrayOutputStream()))
          .process(parameters, inputStream, presetCrcModels, System.err);
      return 1;
    } catch (Exception exception) {
      return ((exception instanceof ProcessorException) ? 0 : 2);
    }
  }

  public void testInputTreeProcessor() {
    final Path inputDirectory = Paths.get(PREFIX + "/input");
    try (DirectoryStream<Path> paths =
         Files.newDirectoryStream(inputDirectory, "*[0-9].xml")) {
      for (Path path: paths) {
        assertTrue("Test file '" + path.getFileName() + "' failed", testFile(path));
      }
    } catch (IOException exception) {
      fail("Failed to process directory (1)");
    }
    try (DirectoryStream<Path> paths =
         Files.newDirectoryStream(inputDirectory, "*-fail.xml")) {
      for (Path path: paths) {
        final int r = testFileFail(path);
        if (r == 1) {
          fail("Test file '" + path.getFileName() + "' did not fail");
        } else if (r == 2) {
          fail("Test file '" + path.getFileName() + "' failed with uncaught exception");
        }
      }
    } catch (IOException exception) {
      fail("Failed to process directory (2)");
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
      assertTrue("AllModels failed on model: " + model.getId(), c.getRegister().equals(model.getCheck()));
    }
  }
}
