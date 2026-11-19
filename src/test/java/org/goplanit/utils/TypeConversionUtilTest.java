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
  public void testFromStringPlainTypes() {
    assertEquals("x", TypeConversionUtil.fromString("x", String.class, tempDir));
    assertEquals(5, TypeConversionUtil.fromString("5", int.class, tempDir));
    assertEquals(12345678901L, TypeConversionUtil.fromString("12345678901", Long.class, tempDir));
    assertEquals(2.5, TypeConversionUtil.fromString("2.5", double.class, tempDir));
    assertEquals(true, TypeConversionUtil.fromString("true", boolean.class, tempDir));
    assertEquals(TestKind.RAMP_METER, TypeConversionUtil.fromString("RAMP_METER", TestKind.class, tempDir));
    assertEquals(7L, TypeConversionUtil.fromString("7", Number.class, tempDir));
    assertEquals(7.5, TypeConversionUtil.fromString("7.5", Number.class, tempDir));
  }

  /** Text that does not fit is refused: NO is no boolean, 1.5 no int, a name in lower case no constant */
  @Test
  public void testFromStringRefusesTextThatDoesNotFit() {
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromString("NO", boolean.class, tempDir));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromString("1.5", int.class, tempDir));
    assertThrows(IllegalArgumentException.class,
        () -> TypeConversionUtil.fromString("ramp_meter", TestKind.class, tempDir));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromString("x", Object.class, tempDir));
  }

  /** A path, a file and a URL without a scheme are read against the folder; a URL with a scheme as written */
  @Test
  public void testFromStringPathsAgainstFolder() throws MalformedURLException {
    assertEquals(tempDir.resolve("data/x.pbf"), TypeConversionUtil.fromString("data/x.pbf", Path.class, tempDir));
    assertEquals(tempDir.resolve("out").toFile(), TypeConversionUtil.fromString("out", File.class, tempDir));
    assertEquals(tempDir.resolve("data/x.pbf").toUri().toURL(),
        TypeConversionUtil.fromString("data/x.pbf", URL.class, tempDir));
    assertEquals("https://example.org/x",
        TypeConversionUtil.fromString("https://example.org/x", URL.class, tempDir).toString());
    assertThrows(IllegalArgumentException.class,
        () -> TypeConversionUtil.fromString("nosuch://x", URL.class, tempDir));
  }

  /**
   * A value becomes text that reads back as the same value: a number and a boolean as Java writes them, an enum by
   * the name of its constant, a path and a folder in full from the working folder, and a URL in full
   */
  @Test
  public void testToString() throws MalformedURLException {
    assertEquals("x", TypeConversionUtil.toString("x"));
    assertEquals("12345678901", TypeConversionUtil.toString(12345678901L));
    assertEquals("2.5", TypeConversionUtil.toString(2.5));
    assertEquals("true", TypeConversionUtil.toString(true));
    assertEquals("RAMP_METER", TypeConversionUtil.toString(TestKind.RAMP_METER));
    assertEquals(Path.of("data/x.pbf").toAbsolutePath().toString(), TypeConversionUtil.toString(Path.of("data/x.pbf")));
    assertEquals(new File("out").getAbsolutePath(), TypeConversionUtil.toString(new File("out")));
    assertEquals("https://example.org/x", TypeConversionUtil.toString(new URL("https://example.org/x")));

    var path = tempDir.resolve("data/x.pbf");
    assertEquals(path, TypeConversionUtil.fromString(TypeConversionUtil.toString(path), Path.class, Path.of("")));
    var url = path.toUri().toURL();
    assertEquals(url, TypeConversionUtil.fromString(TypeConversionUtil.toString(url), URL.class, Path.of("")));
  }

  /**
   * max is the largest value of each whole number type, read and written; it is no value of another type, and a whole
   * number below the largest of its type is written as a number
   */
  @Test
  public void testMaxForTheLargestWholeNumber() {
    assertEquals(Integer.MAX_VALUE, TypeConversionUtil.fromString("max", int.class, tempDir));
    assertEquals(Long.MAX_VALUE, TypeConversionUtil.fromString("max", Long.class, tempDir));
    assertEquals(Short.MAX_VALUE, TypeConversionUtil.fromString("max", short.class, tempDir));
    assertEquals(Byte.MAX_VALUE, TypeConversionUtil.fromString("max", byte.class, tempDir));
    assertEquals("max", TypeConversionUtil.toString(Integer.MAX_VALUE));
    assertEquals("max", TypeConversionUtil.toString(Long.MAX_VALUE));
    assertEquals("2147483647", TypeConversionUtil.toString((long) Integer.MAX_VALUE));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromString("max", double.class, tempDir));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.fromString("max", Number.class, tempDir));
  }

  /** A value of a type text is not read as, and no value at all, are refused */
  @Test
  public void testToStringRefusesOtherTypes() {
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.toString(new Object()));
    assertThrows(IllegalArgumentException.class, () -> TypeConversionUtil.toString(null));
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
