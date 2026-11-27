package org.goplanit.utils;

import org.goplanit.utils.misc.UriUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests of {@link UriUtils}
 *
 * @author markr
 */
public class UriUtilsTest {

  /** folder to read paths against */
  @TempDir
  public Path tempDir;

  /** Text with a scheme is a URI, taken as written, also when the scheme is not a known protocol */
  @Test
  public void testUriWithScheme() {
    assertEquals(URI.create("https://example.org/x.pbf"),
        UriUtils.fromUriOrPath("https://example.org/x.pbf", tempDir));
    assertEquals(URI.create("nosuch://x"), UriUtils.fromUriOrPath("nosuch://x", tempDir));
  }

  /** Text without a scheme is a path, read against the folder when relative */
  @Test
  public void testRelativePath() {
    assertEquals(tempDir.resolve("data/x.pbf").toUri(), UriUtils.fromUriOrPath("data/x.pbf", tempDir));
  }

  /** An absolute path with a drive letter, as in C:/data, is a path, not a URI with scheme C */
  @Test
  public void testDriveLetterIsAPath() {
    var absolute = tempDir.resolve("y.pbf").toAbsolutePath();
    var withForwardSlashes = absolute.toString().replace('\\', '/');
    assertEquals(absolute.toUri(), UriUtils.fromUriOrPath(withForwardSlashes, Path.of("unused")));
  }

  /** Without a folder, a relative path is read against the working folder */
  @Test
  public void testRelativePathWithoutFolder() {
    assertEquals(Path.of("").toAbsolutePath().resolve("data/x.pbf").toUri(),
        UriUtils.fromUriOrPath("data/x.pbf"));
  }
}
