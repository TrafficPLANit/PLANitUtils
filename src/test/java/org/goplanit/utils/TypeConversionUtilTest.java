package org.goplanit.utils;

import org.goplanit.utils.math.TypeConversionUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link TypeConversionUtil}
 *
 * @author markr
 */
public class TypeConversionUtilTest {

  /** Kinds to convert text to */
  public enum TestKind {
    SIGNAL,
    RAMP_METER
  }

  /** folder to read relative paths against */
  @TempDir
  public Path tempDir;

  /** Text becomes a value of each type read straight from text, a primitive type giving its wrapper */
  @Test
  public void testFromTextPlainTypes() {
    assertEquals("x", TypeConversionUtil.fromText("x", String.class, tempDir));
    assertEquals(5, TypeConversionUtil.fromText("5", int.class, tempDir));
    assertEquals(12345678901L, TypeConversionUtil.fromText("12345678901", Long.class, tempDir));
    assertEquals(2.5, TypeConversionUtil.fromText("2.5", double.class, tempDir));
    assertEquals(true, TypeConversionUtil.fromText("true", boolean.class, tempDir));
    assertEquals(TestKind.RAMP_METER, TypeConversionUtil.fromText("RAMP_METER", TestKind.class, tempDir));
    assertEquals(7L, TypeConversionUtil.fromText("7", Number.class, tempDir));
    assertEquals(7.5, TypeConversionUtil.fromText("7.5", Number.class, tempDir));
  }

  /** Text that does not fit is refused: NO is no boolean, 1.5 no int, a name in lower case no constant */
  @Test
  public void testFromTextRefusesTextThatDoesNotFit() {
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromText("NO", boolean.class, tempDir));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromText("1.5", int.class, tempDir));
    assertThrows(IllegalArgumentException.class,
        () -> TypeConversionUtil.fromText("ramp_meter", TestKind.class, tempDir));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromText("x", Object.class, tempDir));
  }

  /** A path, a file and a URL without a scheme are read against the folder; a URL with a scheme as written */
  @Test
  public void testFromTextPathsAgainstFolder() throws MalformedURLException {
    assertEquals(tempDir.resolve("data/x.pbf"), TypeConversionUtil.fromText("data/x.pbf", Path.class, tempDir));
    assertEquals(tempDir.resolve("out").toFile(), TypeConversionUtil.fromText("out", File.class, tempDir));
    assertEquals(tempDir.resolve("data/x.pbf").toUri().toURL(),
        TypeConversionUtil.fromText("data/x.pbf", URL.class, tempDir));
    assertEquals("https://example.org/x",
        TypeConversionUtil.fromText("https://example.org/x", URL.class, tempDir).toString());
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromText("nosuch://x", URL.class, tempDir));
  }

  /** Only true and false are booleans */
  @Test
  public void testToBooleanStrictly() {
    assertTrue(TypeConversionUtil.toBooleanStrictly("true"));
    assertFalse(TypeConversionUtil.toBooleanStrictly("false"));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.toBooleanStrictly("True"));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.toBooleanStrictly("yes"));
  }

  /** A whole number is a Long and any other number a Double */
  @Test
  public void testToNumber() {
    assertEquals(12345678901L, TypeConversionUtil.toNumber("12345678901"));
    assertEquals(0.5, TypeConversionUtil.toNumber("0.5"));
    assertThrows(NumberFormatException.class, () -> TypeConversionUtil.toNumber("many"));
  }
}
