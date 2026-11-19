package org.goplanit.utils.yaml;

import org.goplanit.utils.exceptions.ExceptionUtils;
import org.goplanit.utils.misc.StringUtils;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.StreamDataWriter;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.SequenceNode;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.schema.FailsafeSchema;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Writes YAML text from plain Java values, the reverse of {@link SimpleYamlReader}: text as {@code String}, lists as
 * {@code List} and maps as {@code Map} keyed by text, in sections that may hold comments
 * <p>
 * For example,
 * </p>
 * <pre>
 * var yaml = new SimpleYamlWriter();
 * yaml.startSection("lanes");
 * yaml.addComment("Lanes from the first node to the last");
 * yaml.addEntry("forward", "2");
 * yaml.addCommentedOutKey("backward");
 * yaml.endSection();
 * yaml.addEntry("modes", List.of("car", "bus"));
 * yaml.write(Path.of("scenario.yaml"));
 * </pre>
 * <p>
 * writes
 * </p>
 * <pre>
 * lanes:
 *   # Lanes from the first node to the last
 *   forward: 2
 *   # backward:
 * modes: [car, bus]
 * </pre>
 * <p>
 * It exists so that files people read and change, such as settings files, can be written with notes for the reader
 * and with keys left to fill in, and are read back by {@link SimpleYamlReader} as written. A section is indented two
 * spaces inside its parent. A comment goes above the entry or section that follows it, at its depth, wrapped so that
 * no comment line is longer than {@link #COMMENT_LINE_WIDTH}. A commented-out key stays at the depth of its section
 * wherever it falls, so taking the {@code #} away puts the key where it belongs. Text is quoted only where YAML needs
 * it, such as {@code ''} or {@code 'duplicate: carriageway #2'}. A key's list or map is written one entry per line
 * when it holds a list or map, and on one line otherwise, and each entry stays on one line however long. What cannot
 * be read back as written is refused: a key given twice in one section, a value that is not text, a list or a map
 * keyed by text, and a section holding only comments, as YAML would put them one level too far out. The YAML library
 * calls each part of the YAML tree a node; its {@code Node} type is written in full here, as
 * {@code org.snakeyaml.engine.v2.nodes.Node}, so it is not taken for a node of a PLANit network.
 * </p>
 *
 * @author markr
 */
public final class SimpleYamlWriter {

  /** An entry of a YAML section: the comment lines above it, and its value, a plain value or a YAML section */
  private static final class YamlEntry {

    /** comment lines above the entry */
    private final List<String> commentsAbove;

    /** text, a list or a map as {@link #addEntry(String, Object)} takes it, or a {@link YamlSection} */
    private final Object value;

    /**
     * Constructor
     *
     * @param commentsAbove comment lines above the entry
     * @param value of the entry
     */
    private YamlEntry(List<String> commentsAbove, Object value) {
      this.commentsAbove = commentsAbove;
      this.value = value;
    }
  }

  /** A YAML section: its entries in order, and the comment lines waiting for what follows them */
  private static final class YamlSection {

    /** path of the section, as messages give it, empty at the root */
    private final String keyPath;

    /** how deep the section is nested, 0 at the root */
    private final int depth;

    /** each key with its entry, in the order added */
    private final Map<String, YamlEntry> entries = new LinkedHashMap<>();

    /** comment lines waiting for the entry that follows them, or for the end of the section */
    private final List<String> waitingComments = new ArrayList<>();

    /**
     * Constructor
     *
     * @param keyPath of the section, empty at the root
     * @param depth of the section, 0 at the root
     */
    private YamlSection(String keyPath, int depth) {
      this.keyPath = keyPath;
      this.depth = depth;
    }

    /**
     * Add an entry, with the comment lines waiting above it
     *
     * @param key of the entry
     * @param value of the entry
     * @throws IllegalArgumentException when the section has the key already
     */
    private void addEntry(String key, Object value) {
      ExceptionUtils.throwIllegalArgumentIf(entries.containsKey(key), "Key %s is given twice",
          SimpleYamlReader.keyPathOf(keyPath, key));
      entries.put(key, new YamlEntry(List.copyOf(waitingComments), value));
      waitingComments.clear();
    }
  }

  /** The text made by the YAML library, in memory */
  private static final class TextWriter extends StringWriter implements StreamDataWriter {
  }

  /** spaces a section is indented inside its parent */
  private static final int INDENT = 2;

  /** what each comment line starts with before its text */
  private static final String COMMENT_START = "# ";

  /**
   * how the YAML library writes: comments kept, no line broken, list entries indented like keys, and every value
   * text, so text such as {@code true} is written as it is, the way {@link SimpleYamlReader} reads it
   */
  private static final DumpSettings PERSIST_SETTINGS = DumpSettings.builder().setDumpComments(true).setSplitLines(false)
      .setIndent(INDENT).setIndicatorIndent(INDENT).setIndentWithIndicator(true).setSchema(new FailsafeSchema()).build();

  /**
   * The YAML node of a text
   *
   * @param text of the YAML node
   * @return the YAML node, quoted by the YAML library only where YAML needs it
   */
  private static ScalarNode toScalarYamlNode(String text) {
    return new ScalarNode(Tag.STR, text, ScalarStyle.PLAIN);
  }

  /**
   * Comment lines as the YAML library takes them, each the text after its #, which the library writes itself
   *
   * @param lines of the comment, each without its #
   * @return the comment lines
   */
  private static List<CommentLine> toCommentLines(List<String> lines) {
    return lines.stream().map(line -> new CommentLine(Optional.empty(), Optional.empty(),
        line.isEmpty() ? line : " " + line, CommentType.BLOCK)).collect(Collectors.toList());
  }

  /**
   * How a list or map is laid out: one entry per line when it is a key's value holding a list or map, on one line
   * otherwise
   *
   * @param isKeyValue true when it is the value of a key itself, not inside another list or map
   * @param values of the list or map
   * @return the style
   */
  private static FlowStyle layoutOf(boolean isKeyValue, Collection<?> values) {
    boolean holdsListOrMap = values.stream().anyMatch(value -> value instanceof List || value instanceof Map);
    return isKeyValue && holdsListOrMap ? FlowStyle.BLOCK : FlowStyle.FLOW;
  }

  /**
   * The YAML node of a value and every value in it
   *
   * @param value text, a list or a map keyed by text, nested as the YAML is to nest them
   * @param keyPath of the value, for messages
   * @param isKeyValue true when it is the value of a key itself, not inside another list or map
   * @return the YAML node
   * @throws IllegalArgumentException when the value, or a value in it, is not text, a list or a map keyed by text
   */
  private static org.snakeyaml.engine.v2.nodes.Node toValueYamlNode(Object value, String keyPath, boolean isKeyValue) {
    if (value instanceof String) {
      return toScalarYamlNode((String) value);
    }
    if (value instanceof List) {
      var list = (List<?>) value;
      var yamlNodes = new ArrayList<org.snakeyaml.engine.v2.nodes.Node>(list.size());
      for (int index = 0; index < list.size(); index++) {
        yamlNodes.add(toValueYamlNode(list.get(index), SimpleYamlReader.entryPathOf(keyPath, index), false));
      }
      return new SequenceNode(Tag.SEQ, yamlNodes, layoutOf(isKeyValue, list));
    }
    ExceptionUtils.throwIllegalArgumentIf(!(value instanceof Map), "Value of %s is not text, a list or a map: %s",
        keyPath, value);
    var map = (Map<?, ?>) value;
    var tuples = new ArrayList<NodeTuple>(map.size());
    for (var entry : map.entrySet()) {
      ExceptionUtils.throwIllegalArgumentIf(!(entry.getKey() instanceof String), "A key of %s is not text: %s",
          keyPath, entry.getKey());
      var key = (String) entry.getKey();
      tuples.add(new NodeTuple(toScalarYamlNode(key),
          toValueYamlNode(entry.getValue(), SimpleYamlReader.keyPathOf(keyPath, key), false)));
    }
    return new MappingNode(Tag.MAP, tuples, layoutOf(isKeyValue, map.values()));
  }

  /**
   * The YAML node of a section and everything in it
   * <p>
   * Comment lines after the last entry of a section go with that entry's value, since the YAML library writes those
   * at the depth of the entry, while it writes the closing comments of the section itself one level further out. For
   * example, a commented-out {@code # sourceFile:} last in section {@code highway} would otherwise line up with
   * {@code highway} and be read as a key of its parent.
   * </p>
   *
   * @param section to make the YAML node of
   * @return the YAML node
   */
  private static MappingNode toMappingYamlNode(YamlSection section) {
    var tuples = new ArrayList<NodeTuple>(section.entries.size());
    org.snakeyaml.engine.v2.nodes.Node valueYamlNode = null;
    for (var keyAndYamlEntry : section.entries.entrySet()) {
      var key = keyAndYamlEntry.getKey();
      var yamlEntry = keyAndYamlEntry.getValue();
      var keyYamlNode = toScalarYamlNode(key);
      keyYamlNode.setBlockComments(toCommentLines(yamlEntry.commentsAbove));
      valueYamlNode = yamlEntry.value instanceof YamlSection ? toMappingYamlNode((YamlSection) yamlEntry.value) :
          toValueYamlNode(yamlEntry.value, SimpleYamlReader.keyPathOf(section.keyPath, key), true);
      tuples.add(new NodeTuple(keyYamlNode, valueYamlNode));
    }
    if (!section.waitingComments.isEmpty()) {
      valueYamlNode.setEndComments(toCommentLines(section.waitingComments));
    }
    return new MappingNode(Tag.MAP, tuples, FlowStyle.BLOCK);
  }

  /**
   * Refuse a section holding only comments, as YAML would put them one level too far out
   *
   * @param section to check
   * @throws IllegalStateException when the section has comments and no entry
   */
  private static void throwIfOnlyComments(YamlSection section) {
    ExceptionUtils.throwIllegalStateIf(section.entries.isEmpty() && !section.waitingComments.isEmpty(),
        "Section '%s' holds only comments, which YAML cannot keep at its depth", section.keyPath);
  }

  /** the sections not yet ended, the one entries go to on top, the root at the bottom */
  private final Deque<YamlSection> openSections = new ArrayDeque<>();

  /** longest a comment line may be, its indent and # included */
  public static final int COMMENT_LINE_WIDTH = 120;

  /** Constructor, of a writer holding nothing yet */
  public SimpleYamlWriter() {
    openSections.push(new YamlSection("", 0));
  }

  /**
   * Add a comment above the entry or section that follows it, wrapped over several lines when it is long
   *
   * @param text of the comment, without its #
   */
  public void addComment(String text) {
    var section = openSections.peek();
    section.waitingComments.addAll(
        StringUtils.wrapLineAtSpaces(
            text, COMMENT_LINE_WIDTH - INDENT * section.depth - COMMENT_START.length()));
  }

  /**
   * Add a key with no value, written commented out, such as {@code # sourceFile:}, at the depth of the section
   *
   * @param key to write commented out
   */
  public void addCommentedOutKey(String key) {
    openSections.peek().waitingComments.add(key + ":");
  }

  /**
   * Add a key with its value to the section
   *
   * @param key of the entry
   * @param value text, a list or a map keyed by text, with lists and maps nested in it as the YAML is to nest them
   * @throws IllegalArgumentException when the section has the key already, or the value, or a value in it, is not
   *     text, a list or a map keyed by text
   */
  public void addEntry(String key, Object value) {
    var section = openSections.peek();
    /* made here only to refuse a value that cannot be written, now rather than when the text is made */
    toValueYamlNode(value, SimpleYamlReader.keyPathOf(section.keyPath, key), true);
    section.addEntry(key, value);
  }

  /**
   * Start a section nested in the current one; entries go to it until it is ended
   *
   * @param key of the section
   * @throws IllegalArgumentException when the current section has the key already
   */
  public void startSection(String key) {
    var parent = openSections.peek();
    var section = new YamlSection(SimpleYamlReader.keyPathOf(parent.keyPath, key), parent.depth + 1);
    parent.addEntry(key, section);
    openSections.push(section);
  }

  /**
   * End the current section; entries go to its parent again
   *
   * @throws IllegalStateException when no section is started, or the section holds only comments
   */
  public void endSection() {
    ExceptionUtils.throwIllegalStateIf(openSections.size() == 1, "No section is started");
    throwIfOnlyComments(openSections.pop());
  }

  /**
   * The YAML text of everything added
   *
   * @return the text
   * @throws IllegalStateException when a section is not ended, or the text holds only comments
   */
  @Override
  public String toString() {
    ExceptionUtils.throwIllegalStateIf(openSections.size() > 1, "Section '%s' is not ended",
        openSections.peek().keyPath);
    var root = openSections.peek();
    throwIfOnlyComments(root);
    var text = new TextWriter();
    new Dump(PERSIST_SETTINGS).dumpNode(toMappingYamlNode(root), text);
    return text.toString();
  }

  /**
   * Write the YAML text of everything added to a file, as UTF-8
   *
   * @param file to write
   * @throws IOException when the file cannot be written
   * @throws IllegalStateException when a section is not ended, or the text holds only comments
   */
  public void write(Path file) throws IOException {
    Files.writeString(file, toString());
  }
}
