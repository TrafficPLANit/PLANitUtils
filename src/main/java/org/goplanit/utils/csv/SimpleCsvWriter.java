package org.goplanit.utils.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Simple CSV writer counterpart to {@link SimpleCsvParser}, writing a header row followed by any number of content
 * rows.
 * <p>
 * Values are quoted only where they have to be, i.e. when they contain the separator, a quote, or a line break, with
 * embedded quotes doubled. Writing a value containing a separator without quoting it would silently shift every
 * subsequent column of that row, which is the kind of corruption that only surfaces when the file is read back long
 * after the run that produced it.
 * </p>
 * <p>
 * Where the content is already available in full, {@link #write(Path, List, Collection)} writes the entire file in a
 * single call. Otherwise an instance is created and rows are written as they are produced, in which case it belongs in
 * a try-with-resources block since it holds an open file handle.
 * </p>
 *
 * @author markr
 */
public class SimpleCsvWriter implements AutoCloseable {

  /** separator used when none is provided */
  public static final String DEFAULT_SEPARATOR = ",";

  /** by default values are inspected and quoted where required */
  public static final boolean DEFAULT_ESCAPE_VALUES = true;

  /** characters that force a value to be quoted, beyond the separator itself */
  private static final char[] ALWAYS_QUOTE_TRIGGERS = {'"', '\n', '\r'};

  /** the underlying writer */
  private final BufferedWriter writer;

  /** separator between values */
  private final String separator;

  /** when true each value is inspected and quoted where required, when false it is written as is */
  private final boolean escapeValues;

  /** number of values each row is expected to hold, taken from the header */
  private final int numColumns;

  /**
   * Constructor, creates the file and any missing parent directories and writes the header row
   *
   * @param filePath to write to
   * @param separator to use between values
   * @param escapeValues when true values are inspected and quoted where required, when false they are written as is
   * @param headers naming each column, also fixing how many values a row must hold
   */
  protected SimpleCsvWriter(
      final Path filePath, final String separator, final boolean escapeValues, final String... headers) {
    Objects.requireNonNull(filePath, "file path is required to write a CSV file");
    if (headers == null || headers.length == 0) {
      throw new IllegalArgumentException("at least one header is required to write a CSV file");
    }
    this.separator = separator;
    this.escapeValues = escapeValues;
    this.numColumns = headers.length;

    try {
      var parentDir = filePath.getParent();
      if (parentDir != null) {
        Files.createDirectories(parentDir);
      }
      this.writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);
      writeRow((Object[]) headers);
    } catch (IOException e) {
      throw new UncheckedIOException("unable to create CSV file " + filePath, e);
    }
  }

  /**
   * Create a writer using the default separator
   *
   * @param filePath to write to
   * @param headers naming each column
   * @return created writer
   */
  public static SimpleCsvWriter create(final Path filePath, final String... headers) {
    return new SimpleCsvWriter(filePath, DEFAULT_SEPARATOR, DEFAULT_ESCAPE_VALUES, headers);
  }

  /**
   * Create a writer using the given separator
   *
   * @param filePath to write to
   * @param separator to use between values
   * @param headers naming each column
   * @return created writer
   */
  public static SimpleCsvWriter createWithSeparator(
      final Path filePath, final String separator, final String... headers) {
    return new SimpleCsvWriter(filePath, separator, DEFAULT_ESCAPE_VALUES, headers);
  }

  /**
   * Create a writer using the given separator, choosing whether values are inspected for characters that require
   * quoting. Skipping the inspection is only safe where the content is known to contain none of them
   *
   * @param filePath to write to
   * @param separator to use between values
   * @param escapeValues when true values are inspected and quoted where required, when false they are written as is
   * @param headers naming each column
   * @return created writer
   */
  public static SimpleCsvWriter createWithSeparator(
      final Path filePath, final String separator, final boolean escapeValues, final String... headers) {
    return new SimpleCsvWriter(filePath, separator, escapeValues, headers);
  }

  /**
   * Write an entire file in one call, i.e. create it, write the header row, write every content row and close it
   *
   * @param filePath to write to
   * @param headers naming each column
   * @param rows content, each row holding as many values as there are headers
   */
  public static void write(
      final Path filePath,
      final List<String> headers,
      final Collection<? extends Collection<?>> rows) {
    write(filePath, DEFAULT_SEPARATOR, DEFAULT_ESCAPE_VALUES, headers, rows);
  }

  /**
   * Write an entire file in one call, i.e. create it, write the header row, write every content row and close it
   *
   * @param filePath to write to
   * @param separator to use between values
   * @param escapeValues when true values are inspected and quoted where required, when false they are written as is
   * @param headers naming each column
   * @param rows content, each row holding as many values as there are headers
   */
  public static void write(
      final Path filePath,
      final String separator,
      final boolean escapeValues,
      final List<String> headers,
      final Collection<? extends Collection<?>> rows) {
    Objects.requireNonNull(headers, "headers are required to write a CSV file");
    Objects.requireNonNull(rows, "rows are required to write a CSV file, provide an empty collection instead of null");

    try (var csvWriter = new SimpleCsvWriter(
        filePath, separator, escapeValues, headers.toArray(new String[0]))) {
      for (var row : rows) {
        csvWriter.writeRow(row.toArray());
      }
    }
  }

  /**
   * Write a single row, converting each value via its string representation and treating a null as an empty value
   *
   * @param values to write, must match the number of headers the writer was created with
   */
  public void writeRow(final Object... values) {
    if (values == null || values.length != numColumns) {
      throw new IllegalArgumentException(String.format(
          "CSV row holds %d values while %d columns are defined",
          values == null ? 0 : values.length, numColumns));
    }

    var row = new StringBuilder();
    for (int index = 0; index < values.length; ++index) {
      if (index > 0) {
        row.append(separator);
      }
      row.append(escape(values[index]));
    }

    try {
      writer.write(row.toString());
      writer.newLine();
    } catch (IOException e) {
      throw new UncheckedIOException("unable to write CSV row", e);
    }
  }

  /**
   * Collect the number of columns each row must hold
   *
   * @return number of columns
   */
  public int getNumColumns() {
    return numColumns;
  }

  /**
   * Close the underlying file
   */
  @Override
  public void close() {
    try {
      writer.close();
    } catch (IOException e) {
      throw new UncheckedIOException("unable to close CSV file", e);
    }
  }

  /**
   * Convert a value to its written form, quoting it when leaving it bare would corrupt the row
   *
   * @param value to convert, may be null
   * @return value as it is to appear in the file
   */
  private String escape(final Object value) {
    if (value == null) {
      return "";
    }

    var asString = value.toString();
    if (!escapeValues) {
      return asString;
    }

    boolean requiresQuoting = asString.contains(separator);
    for (int index = 0; !requiresQuoting && index < ALWAYS_QUOTE_TRIGGERS.length; ++index) {
      requiresQuoting = asString.indexOf(ALWAYS_QUOTE_TRIGGERS[index]) >= 0;
    }

    if (!requiresQuoting) {
      return asString;
    }
    return "\"" + asString.replace("\"", "\"\"") + "\"";
  }
}
