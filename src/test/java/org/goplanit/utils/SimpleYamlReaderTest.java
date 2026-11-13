package org.goplanit.utils;

import org.goplanit.utils.exceptions.YamlProblemsException;
import org.goplanit.utils.yaml.SimpleYamlReader;
import org.goplanit.utils.yaml.YamlProblem;
import org.goplanit.utils.yaml.YamlProblemKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests of {@link SimpleYamlReader}: plain values nested as the YAML nests them, every value text, and every problem
 * found at once with the path of its key
 *
 * @author markr
 */
public class SimpleYamlReaderTest {

  /**
   * The problems reading the text throws with
   *
   * @param yamlText to read
   * @return the problems, in the order found
   */
  private static List<YamlProblem> problemsOf(String yamlText) {
    return assertThrows(YamlProblemsException.class, () -> SimpleYamlReader.read(yamlText)).getProblems();
  }

  /**
   * The kinds of the problems
   *
   * @param problems to get the kinds of
   * @return the kinds, in order
   */
  private static List<YamlProblemKind> kindsOf(List<YamlProblem> problems) {
    return problems.stream().map(YamlProblem::getKind).collect(Collectors.toList());
  }

  /** folder for the test files */
  @TempDir
  public Path tempDir;

  /** Maps keep the order of the YAML, nesting is kept, and every value stays text, NO and numbers included */
  @Test
  public void testPlainValuesNestedAndText() {
    var root = (Map<?, ?>) SimpleYamlReader.read(
        "name: Sydney\nmodes: [car, NO, 2.5]\nlanes:\n  forward: 2\n  sides:\n    - {left: 1}\n");
    assertEquals(List.of("name", "modes", "lanes"), List.copyOf(root.keySet()));
    assertEquals("Sydney", root.get("name"));
    assertEquals(List.of("car", "NO", "2.5"), root.get("modes"));
    var lanes = (Map<?, ?>) root.get("lanes");
    assertEquals("2", lanes.get("forward"));
    assertEquals(List.of(Map.of("left", "1")), lanes.get("sides"));
  }

  /** A value is read as text or as keys with values only when it is one, and is null otherwise */
  @Test
  public void testValueAsTextOrMap() {
    var root = SimpleYamlReader.asMap(SimpleYamlReader.read("name: Sydney\nlanes:\n  forward: 2\n"));
    assertEquals("Sydney", SimpleYamlReader.asText(root.get("name")));
    assertEquals("2", SimpleYamlReader.asText(SimpleYamlReader.asMap(root.get("lanes")).get("forward")));
    assertNull(SimpleYamlReader.asMap(root.get("name")));
    assertNull(SimpleYamlReader.asText(root.get("lanes")));
  }

  /** An empty document, or one holding only a comment, gives null */
  @Test
  public void testEmptyDocumentIsNull() {
    assertNull(SimpleYamlReader.read(""));
    assertNull(SimpleYamlReader.read("# nothing yet\n"));
  }

  /** Every problem is found before it throws, each with the path of its key, nested ones and list entries included */
  @Test
  public void testEveryProblemFoundWithItsKeyPath() {
    var problems = problemsOf("a:\n  b: 1\n  b: 2\nlist:\n  - {k: 1, k: 2}\n  - !!java.io.File x\n");
    assertEquals(List.of("a.b", "list[0].k", "list[1]"),
        problems.stream().map(YamlProblem::getKeyPath).collect(Collectors.toList()));
    assertEquals(List.of(YamlProblemKind.DUPLICATE_KEY, YamlProblemKind.DUPLICATE_KEY, YamlProblemKind.UNSUPPORTED_TAG),
        kindsOf(problems));
  }

  /** A key that is not text is refused */
  @Test
  public void testNonTextKeyRefused() {
    assertEquals(List.of(YamlProblemKind.NON_TEXT_KEY), kindsOf(problemsOf("? [a, b]\n: c\n")));
  }

  /** A value that holds itself through an alias is refused, rather than read without end */
  @Test
  public void testValueHoldingItselfRefused() {
    var problems = problemsOf("a: &x [*x]\n");
    assertEquals(List.of(YamlProblemKind.HOLDS_ITSELF), kindsOf(problems));
    assertEquals("a", problems.get(0).getKeyPath());
  }

  /** Text that is not YAML is refused, and so are more aliases than allowed */
  @Test
  public void testNotYamlRefused() {
    assertEquals(List.of(YamlProblemKind.NOT_YAML), kindsOf(problemsOf("a: [open\n")));
    var manyAliases = new StringBuilder("x: &x [1]\nys:\n");
    for (int index = 0; index <= SimpleYamlReader.MAX_ALIASES; index++) {
      manyAliases.append("  - *x\n");
    }
    assertEquals(List.of(YamlProblemKind.NOT_YAML), kindsOf(problemsOf(manyAliases.toString())));
  }

  /** A file reads as its text does */
  @Test
  public void testFileRead() throws IOException {
    var file = tempDir.resolve("a.yaml");
    Files.writeString(file, "name: Sydney\n");
    assertEquals(Map.of("name", "Sydney"), SimpleYamlReader.read(file));
  }
}
