package org.goplanit.utils;

import org.goplanit.utils.csv.SimpleCsvParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SimpleCsvTest {

  private static final Path RESOURCE_PATH = Path.of("src", "test", "resources");

  private static final String SPACE_SEP = " ";

  private static final String TAB_SEP = "\\t";

  @Test
  public void noHeaderTest() {
    var filePath = Path.of(RESOURCE_PATH.toAbsolutePath().toString(), "csv_test_no_headers.csv");

    var csvParser = SimpleCsvParser.createWithoutHeaders(filePath.toAbsolutePath().toString(), TAB_SEP);
    var result = csvParser.parse();

    assertNull(result.first()); // no header column
    assertNull(result.second()); // no header row
    assertEquals(25 * 25, result.third().stream().flatMap(Arrays::stream).count()); // content should be 25*25
  }

  @Test
  public void rowHeaderTest() {
    var filePath = Path.of(RESOURCE_PATH.toAbsolutePath().toString(), "csv_test_with_row_header.csv");

    var csvParser = SimpleCsvParser.createWithHeaderRow(filePath.toAbsolutePath().toString(), TAB_SEP);
    var result = csvParser.parse();

    assertNull(result.first()); // no header column
    assertEquals(25, result.second().length); // header row of 25 entries
    assertEquals(25 * 25, result.third().stream().flatMap(Arrays::stream).count()); // content should be 25*25
  }

  @Test
  public void rowAndColHeaderTest() {
    var filePath = Path.of(RESOURCE_PATH.toAbsolutePath().toString(), "csv_test_with_headers.csv");

    var csvParser = SimpleCsvParser.create(filePath.toAbsolutePath().toString(), TAB_SEP, true, true);
    var result = csvParser.parse();

    assertEquals(25, result.second().length); // header col of 25 entries
    assertEquals(25, result.second().length); // header row of 25 entries
    assertEquals(25 * 25, result.third().stream().flatMap(Arrays::stream).count()); // content should be 25*25
  }
}
