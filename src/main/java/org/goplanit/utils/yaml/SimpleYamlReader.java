package org.goplanit.utils.yaml;

import org.goplanit.utils.exceptions.YamlProblemsException;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.SequenceNode;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.schema.FailsafeSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Reads YAML into plain Java values: text as {@code String}, lists as {@code List<Object>} and maps as
 * {@code Map<String, Object>} in the order of the YAML, nested as the YAML nests them
 * <p>
 * For example, the YAML
 * </p>
 * <pre>
 * name: Sydney
 * modes: [car, bus]
 * lanes:
 *   forward: 2
 *   enabled: NO
 * </pre>
 * <p>
 * gives a map in which {@code name} is the text {@code Sydney}, {@code modes} the list {@code [car, bus]} and
 * {@code lanes} a map in which {@code forward} is the text {@code 2} and {@code enabled} the text {@code NO}:
 * </p>
 * <pre>
 * var root = SimpleYamlReader.asMap(SimpleYamlReader.read(Path.of("scenario.yaml")));
 * var lanes = SimpleYamlReader.asMap(root.get("lanes"));
 * int forward = Integer.parseInt(SimpleYamlReader.asText(lanes.get("forward")));
 * </pre>
 * <p>
 * Every value stays text, so the code using it decides what it means: {@code NO} is not read as false as YAML 1.1
 * readers do, and {@code 2} becomes a number only where a number is expected. The reader is strict, so a mistake is
 * reported rather than read as something else: a tag such as {@code !!java.io.File} is refused, so the YAML never makes
 * a Java object, and so are keys that are not text, a key given twice in one map, a value that holds itself through an
 * alias, and more than {@link #MAX_ALIASES} aliases of lists or maps. Every problem in the text is found before the
 * reader throws, each with the path of its key, such as {@code lanes.forward} or {@code modes[1]}, so all can be fixed
 * in one go. The YAML library calls each part of the YAML tree a node; its {@code Node} type is written in full here,
 * as {@code org.snakeyaml.engine.v2.nodes.Node}, so it is not taken for a node of a PLANit network.
 * </p>
 *
 * @author markr
 */
public final class SimpleYamlReader {

  /** the tags a YAML node has under the failsafe schema when the text gives it none of its own */
  private static final Set<Tag> PLAIN_TAGS = Set.of(Tag.STR, Tag.SEQ, Tag.MAP);

  /**
   * Where a YAML node is, for problems
   *
   * @param yamlNode to locate
   * @return " on line n", or empty when not known
   */
  private static String lineOf(org.snakeyaml.engine.v2.nodes.Node yamlNode) {
    return yamlNode.getStartMark().map(mark -> " on line " + (mark.getLine() + 1)).orElse("");
  }

  /** problems found so far */
  private final List<YamlProblem> problems = new ArrayList<>();

  /** Constructor, private, as each text is read by a reader of its own through {@link #read(String)} */
  private SimpleYamlReader() {
  }

  /**
   * Record a problem
   *
   * @param kind of problem
   * @param keyPath of the key it is about, empty for the text as a whole
   * @param detail what is wrong, for people
   */
  private void addProblem(YamlProblemKind kind, String keyPath, String detail) {
    problems.add(new YamlProblem(kind, keyPath, detail));
  }

  /**
   * The plain value of a YAML node and every node in it, recording each problem found
   * <p>
   * One walk of the YAML tree both checks and converts, so every problem in the text is found in one go: a node with a
   * problem is recorded and skipped, and the walk goes on with its neighbours. For example, in
   * {@code {a: {b: 1, b: 2}, c: [!!java.io.File x]}} it records the key {@code a.b} given twice and the tag at
   * {@code c[0]}, then the caller throws both together. A skipped node gives null, which no caller sees, since a read
   * with any problem throws. A value holding itself is refused before its entries are walked, so the walk always ends.
   * </p>
   *
   * @param yamlNode to read
   * @param keyPath of the node
   * @return the value: a String, a List or a Map, or null when the node itself has a problem
   */
  private Object toValue(org.snakeyaml.engine.v2.nodes.Node yamlNode, String keyPath) {
    if (!PLAIN_TAGS.contains(yamlNode.getTag())) {
      addProblem(YamlProblemKind.UNSUPPORTED_TAG, keyPath,
          String.format("Tag %s is not supported%s", yamlNode.getTag(), lineOf(yamlNode)));
      return null;
    }
    if (yamlNode.isRecursive()) {
      addProblem(YamlProblemKind.HOLDS_ITSELF, keyPath, "Holds itself through an alias" + lineOf(yamlNode));
      return null;
    }
    if (yamlNode instanceof ScalarNode) {
      return ((ScalarNode) yamlNode).getValue();
    }
    if (yamlNode instanceof SequenceNode) {
      var entries = ((SequenceNode) yamlNode).getValue();
      var list = new ArrayList<Object>(entries.size());
      for (int index = 0; index < entries.size(); index++) {
        list.add(toValue(entries.get(index), entryPathOf(keyPath, index)));
      }
      return list;
    }
    var map = new LinkedHashMap<String, Object>();
    for (var tuple : ((MappingNode) yamlNode).getValue()) {
      var keyNode = tuple.getKeyNode();
      if (!(keyNode instanceof ScalarNode)) {
        addProblem(YamlProblemKind.NON_TEXT_KEY, keyPath, "A key is not text" + lineOf(keyNode));
        continue;
      }
      if (!Tag.STR.equals(keyNode.getTag())) {
        addProblem(YamlProblemKind.UNSUPPORTED_TAG, keyPath,
            String.format("Tag %s of a key is not supported%s", keyNode.getTag(), lineOf(keyNode)));
        continue;
      }
      var key = ((ScalarNode) keyNode).getValue();
      if (map.containsKey(key)) {
        addProblem(YamlProblemKind.DUPLICATE_KEY, keyPathOf(keyPath, key), "Given twice" + lineOf(keyNode));
        continue;
      }
      map.put(key, toValue(tuple.getValueNode(), keyPathOf(keyPath, key)));
    }
    return map;
  }

  /**
   * The plain value of the root of YAML text, recording each problem found
   *
   * @param yamlText to read
   * @return the value, null for an empty document or text that is not YAML
   */
  private Object toRootValue(String yamlText) {
    /* keys that are not text are let through here, so the walk refuses each one with its key path and carries on */
    var settings = LoadSettings.builder().setSchema(new FailsafeSchema()).setMaxAliasesForCollections(MAX_ALIASES)
        .setAllowNonScalarKeys(true).build();
    Optional<org.snakeyaml.engine.v2.nodes.Node> root;
    try {
      root = new Compose(settings).composeString(yamlText);
    } catch (YamlEngineException e) {
      addProblem(YamlProblemKind.NOT_YAML, "", e.getMessage());
      return null;
    }
    return root.map(yamlNode -> toValue(yamlNode, "")).orElse(null);
  }

  /** the most aliases of lists and maps the text may hold, so it cannot grow without bound through nested aliases */
  public static final int MAX_ALIASES = 50;

  /**
   * The path of a key in a map, as problems give it: {@code lanes} and {@code forward} give {@code lanes.forward}
   *
   * @param path of the map, empty at the root
   * @param key in the map
   * @return the path of the key, with a dot between nested maps
   */
  public static String keyPathOf(String path, String key) {
    return path.isEmpty() ? key : path + "." + key;
  }

  /**
   * The path of an entry of a list, as problems give it: {@code modes} and 1 give {@code modes[1]}
   *
   * @param path of the list
   * @param index of the entry, from 0
   * @return the path of the entry
   */
  public static String entryPathOf(String path, int index) {
    return path + "[" + index + "]";
  }

  /**
   * A value as text, such as {@code asText(root.get("name"))}
   *
   * @param value as {@link #read(String)} gives it
   * @return its text, null when it is a list or a map
   */
  public static String asText(Object value) {
    return value instanceof String ? (String) value : null;
  }

  /**
   * A value as keys with values, such as {@code asMap(root.get("lanes"))}, the keys in the order of the YAML
   *
   * @param value as {@link #read(String)} gives it
   * @return the map, null when it is text or a list
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Object> asMap(Object value) {
    return value instanceof Map ? (Map<String, Object>) value : null;
  }

  /**
   * The plain values of YAML text
   *
   * @param yamlText to read
   * @return the value of the root, a Map, a List or a String with any values in it nested as in the YAML; null for an
   *     empty document
   * @throws YamlProblemsException holding every problem found, when there is any
   */
  public static Object read(String yamlText) {
    var reader = new SimpleYamlReader();
    var root = reader.toRootValue(yamlText);
    if (!reader.problems.isEmpty()) {
      throw new YamlProblemsException(reader.problems);
    }
    return root;
  }

  /**
   * The plain values of a YAML file, read as UTF-8
   *
   * @param file to read
   * @return the value of the root, as {@link #read(String)} gives it
   * @throws IOException when the file cannot be read
   * @throws YamlProblemsException holding every problem found, when there is any
   */
  public static Object read(Path file) throws IOException {
    return read(Files.readString(file));
  }
}
