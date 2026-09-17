package org.goplanit.utils;

import org.goplanit.utils.csv.SimpleCsvWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test writing CSV content
 *
 * @author markr
 */
public class SimpleCsvWriterTest {

  @TempDir
  Path tempDir;

  @Test
  public void headerAndRowsTest() throws IOException {
    var file = tempDir.resolve("basic.csv");

    try (var csvWriter = SimpleCsvWriter.create(file, "id", "reason", "count")) {
      assertEquals(3, csvWriter.getNumColumns());
      csvWriter.writeRow("2000111", "no physical path", 12);
      csvWriter.writeRow("2000112", "outside bounding area", 3);
    }

    assertEquals(
        List.of("id,reason,count", "2000111,no physical path,12", "2000112,outside bounding area,3"),
        Files.readAllLines(file, StandardCharsets.UTF_8));
  }

  @Test
  public void quotingTest() throws IOException {
    var file = tempDir.resolve("quoted.csv");

    try (var csvWriter = SimpleCsvWriter.create(file, "id", "detail")) {
      csvWriter.writeRow("a", "contains, a separator");
      csvWriter.writeRow("b", "contains a \"quote\"");
      csvWriter.writeRow("c", "contains\na line break");
      csvWriter.writeRow("d", "plain");
    }

    var content = Files.readString(file, StandardCharsets.UTF_8);

    /* only values that would otherwise corrupt the row are quoted, with embedded quotes doubled */
    assertTrue(content.contains("a,\"contains, a separator\""));
    assertTrue(content.contains("b,\"contains a \"\"quote\"\"\""));
    assertTrue(content.contains("\"contains\na line break\""));
    assertTrue(content.contains("d,plain"));
  }

  @Test
  public void nullValueTest() throws IOException {
    var file = tempDir.resolve("nulls.csv");

    try (var csvWriter = SimpleCsvWriter.create(file, "id", "detail")) {
      csvWriter.writeRow("a", null);
    }

    assertEquals(List.of("id,detail", "a,"), Files.readAllLines(file, StandardCharsets.UTF_8));
  }

  @Test
  public void alternativeSeparatorTest() throws IOException {
    var file = tempDir.resolve("tabbed.csv");

    try (var csvWriter = SimpleCsvWriter.createWithSeparator(file, "\t", "id", "detail")) {
      csvWriter.writeRow("a", "value, with comma");
    }

    /* a comma is no longer special once the separator is not a comma */
    assertEquals(List.of("id\tdetail", "a\tvalue, with comma"), Files.readAllLines(file, StandardCharsets.UTF_8));
  }

  @Test
  public void createsMissingDirectoriesTest() throws IOException {
    var file = tempDir.resolve("nested").resolve("deeper").resolve("out.csv");

    try (var csvWriter = SimpleCsvWriter.create(file, "id")) {
      csvWriter.writeRow("a");
    }

    assertTrue(Files.exists(file));
  }

  @Test
  public void columnCountMismatchTest() throws IOException {
    var file = tempDir.resolve("mismatch.csv");

    try (var csvWriter = SimpleCsvWriter.create(file, "id", "detail")) {
      assertThrows(IllegalArgumentException.class, () -> csvWriter.writeRow("only one"));
      assertThrows(IllegalArgumentException.class, () -> csvWriter.writeRow("one", "two", "three"));
    }
  }

  @Test
  public void writeWholeFileInOneCallTest() throws IOException {
    var file = tempDir.resolve("oneshot.csv");

    SimpleCsvWriter.write(
        file,
        List.of("id", "reason", "count"),
        List.of(
            List.of("2000111", "no physical path", 12),
            List.of("2000112", "outside, bounding area", 3)));

    assertEquals(
        List.of(
            "id,reason,count",
            "2000111,no physical path,12",
            "2000112,\"outside, bounding area\",3"),
        Files.readAllLines(file, StandardCharsets.UTF_8));
  }

  @Test
  public void writeWholeFileWithoutEscapingTest() throws IOException {
    var file = tempDir.resolve("oneshot_raw.csv");

    SimpleCsvWriter.write(
        file, ";", false, List.of("id", "reason"), List.of(List.of("a", "no \"check\" applied")));

    /* with the inspection switched off the value is written exactly as its toString produced it */
    assertEquals(
        List.of("id;reason", "a;no \"check\" applied"), Files.readAllLines(file, StandardCharsets.UTF_8));
  }

  @Test
  public void writeEmptyContentTest() throws IOException {
    var file = tempDir.resolve("headers_only.csv");

    SimpleCsvWriter.write(file, List.of("id", "reason"), List.of());

    /* a file reporting nothing still states what it would have reported */
    assertEquals(List.of("id,reason"), Files.readAllLines(file, StandardCharsets.UTF_8));
  }

  @Test
  public void missingHeadersTest() {
    var file = tempDir.resolve("noheaders.csv");

    assertThrows(IllegalArgumentException.class, () -> SimpleCsvWriter.create(file));
  }
}
