package org.goplanit.utils;

import org.goplanit.utils.yaml.SimpleYamlReader;
import org.goplanit.utils.yaml.SimpleYamlWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link SimpleYamlWriter}: sections indented inside their parents, comments at the depth of what follows
 * them, commented-out keys at the depth they belong, and text read back by {@link SimpleYamlReader} as written
 *
 * @author markr
 */
public class SimpleYamlWriterTest {

  /**
   * A map keeping the order its keys are given in, so the text written is known
   *
   * @param keysAndValues each key followed by its value
   * @return the map
   */
  private static Map<String, Object> orderedMapOf(Object... keysAndValues) {
    var map = new LinkedHashMap<String, Object>();
    for (int index = 0; index < keysAndValues.length; index += 2) {
      map.put((String) keysAndValues[index], keysAndValues[index + 1]);
    }
    return map;
  }

  /** folder for the test files */
  @TempDir
  public Path tempDir;

  /** Text that YAML would read as something else, or that needs quotes, is read back as the same text */
  @Test
  public void testTextReadBackAsWritten() {
    var texts = List.of("", " lead", "duplicate: carriageway #2", "NO", "true", "~", "it's", "[not a list]", "two\nlines");
    var yaml = new SimpleYamlWriter();
    for (int index = 0; index < texts.size(); index++) {
      yaml.addEntry("text" + index, texts.get(index));
    }
    var topLevel = SimpleYamlReader.asMap(SimpleYamlReader.read(yaml.toString()));
    for (int index = 0; index < texts.size(); index++) {
      assertEquals(texts.get(index), topLevel.get("text" + index));
    }
  }

  /** Lists and maps, nested in each other and in sections, are read back as written */
  @Test
  public void testListsMapsAndSectionsReadBackAsWritten() {
    var lanes = orderedMapOf("12", orderedMapOf("value", List.of("2", "1"), "reason", "checked"));
    var yaml = new SimpleYamlWriter();
    yaml.addEntry("name", "Sydney");
    yaml.startSection("network");
    yaml.addEntry("types", List.of("primary", "secondary"));
    yaml.addEntry("lanes", lanes);
    yaml.endSection();
    assertEquals(orderedMapOf("name", "Sydney", "network",
        orderedMapOf("types", List.of("primary", "secondary"), "lanes", lanes)), SimpleYamlReader.read(yaml.toString()));
  }

  /**
   * Each section is indented two spaces inside its parent, and a key's list or map is written one entry per line only
   * when it holds a list or map
   */
  @Test
  public void testSectionsIndentedAndValuesLaidOut() {
    var yaml = new SimpleYamlWriter();
    yaml.startSection("network");
    yaml.startSection("highway");
    yaml.addEntry("types", List.of("primary", "secondary"));
    yaml.addEntry("access", orderedMapOf("Node", orderedMapOf("46", List.of("bus"))));
    yaml.addEntry("excluded", List.of("12", orderedMapOf("value", "34", "reason", "duplicate")));
    yaml.addEntry("none", List.of());
    yaml.startSection("unused");
    yaml.endSection();
    yaml.endSection();
    yaml.addEntry("other", "1");
    yaml.endSection();
    assertEquals("network:\n"
        + "  highway:\n"
        + "    types: [primary, secondary]\n"
        + "    access:\n"
        + "      Node: {46: [bus]}\n"
        + "    excluded:\n"
        + "      - 12\n"
        + "      - {value: 34, reason: duplicate}\n"
        + "    none: []\n"
        + "    unused: {}\n"
        + "  other: 1\n", yaml.toString());
  }

  /** A comment goes above the entry or section that follows it, at that entry's depth */
  @Test
  public void testCommentsAboveWhatFollows() {
    var yaml = new SimpleYamlWriter();
    yaml.addComment("About the network");
    yaml.startSection("network");
    yaml.addComment("The settings of parsing the network");
    yaml.addComment("--- standard ---");
    yaml.addEntry("active", "true");
    yaml.endSection();
    assertEquals("# About the network\n"
        + "network:\n"
        + "  # The settings of parsing the network\n"
        + "  # --- standard ---\n"
        + "  active: true\n", yaml.toString());
  }

  /**
   * A commented-out key is written at the depth of its section wherever it falls: before an entry, last in a nested
   * section, and last in the text, so taking the # away puts the key where it belongs
   */
  @Test
  public void testCommentedOutKeysAtTheirDepth() {
    var yaml = new SimpleYamlWriter();
    yaml.startSection("network");
    yaml.startSection("highway");
    yaml.addCommentedOutKey("first");
    yaml.addEntry("active", "true");
    yaml.addComment("Where to read from");
    yaml.addCommentedOutKey("sourceFile");
    yaml.endSection();
    yaml.addCommentedOutKey("other");
    yaml.endSection();
    yaml.addCommentedOutKey("last");
    assertEquals("network:\n"
        + "  highway:\n"
        + "    # first:\n"
        + "    active: true\n"
        + "    # Where to read from\n"
        + "    # sourceFile:\n"
        + "  # other:\n"
        + "# last:\n", yaml.toString());
    assertEquals(Map.of("network", Map.of("highway", Map.of("active", "true"))), SimpleYamlReader.read(yaml.toString()));
  }

  /** A long comment is wrapped over lines no longer than the comment line width, each at the depth of what follows */
  @Test
  public void testLongCommentWrapped() {
    var words = String.join(" ", Collections.nCopies(60, "word"));
    var yaml = new SimpleYamlWriter();
    yaml.startSection("network");
    yaml.startSection("highway");
    yaml.addComment(words);
    yaml.addEntry("active", "true");
    yaml.endSection();
    yaml.endSection();
    var commentLines = yaml.toString().lines().filter(line -> line.trim().startsWith("#")).collect(Collectors.toList());
    assertTrue(commentLines.size() > 1);
    for (var line : commentLines) {
      assertTrue(line.startsWith("    # "));
      assertTrue(line.length() <= SimpleYamlWriter.COMMENT_LINE_WIDTH);
    }
    assertEquals(words,
        commentLines.stream().map(line -> line.substring("    # ".length())).collect(Collectors.joining(" ")));
  }

  /**
   * What cannot be written is refused: a key given twice, a value that is not text, a list or a map keyed by text,
   * closing a section that was not started, text with a section still open, and a section holding only comments
   */
  @Test
  public void testWhatCannotBeWrittenRefused() {
    var twice = new SimpleYamlWriter();
    twice.addEntry("a", "1");
    assertThrows(IllegalArgumentException.class, () -> twice.addEntry("a", "2"));
    assertThrows(IllegalArgumentException.class, () -> twice.startSection("a"));

    var values = new SimpleYamlWriter();
    assertThrows(IllegalArgumentException.class, () -> values.addEntry("number", 2));
    assertThrows(IllegalArgumentException.class, () -> values.addEntry("nothing", null));
    assertThrows(IllegalArgumentException.class, () -> values.addEntry("numbers", List.of(2)));
    assertThrows(IllegalArgumentException.class, () -> values.addEntry("numberKeys", Map.of(1, "a")));

    var sections = new SimpleYamlWriter();
    assertThrows(IllegalStateException.class, sections::endSection);
    sections.startSection("open");
    sections.addEntry("a", "1");
    assertThrows(IllegalStateException.class, sections::toString);

    var onlyComments = new SimpleYamlWriter();
    onlyComments.startSection("highway");
    onlyComments.addCommentedOutKey("sourceFile");
    assertThrows(IllegalStateException.class, onlyComments::endSection);
  }

  /** A file holds the text, read back as written */
  @Test
  public void testFileWritten() throws IOException {
    var yaml = new SimpleYamlWriter();
    yaml.addEntry("name", "Sydney");
    var file = tempDir.resolve("a.yaml");
    yaml.write(file);
    assertEquals(Map.of("name", "Sydney"), SimpleYamlReader.read(file));
  }

  /** A file in a folder that is not there yet is written, the folder made */
  @Test
  public void testFileWrittenIntoAFolderNotThereYet() throws IOException {
    var yaml = new SimpleYamlWriter();
    yaml.addEntry("name", "Sydney");
    var file = tempDir.resolve("run").resolve("output").resolve("a.yaml");
    yaml.write(file);
    assertEquals(Map.of("name", "Sydney"), SimpleYamlReader.read(file));
  }
}
