package org.goplanit.utils;

import org.goplanit.utils.misc.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests of {@link FileUtils}
 *
 * @author markr
 */
public class FileUtilsTest {

  /** folder to read paths against */
  @TempDir
  public Path tempDir;

  /** A relative path is read against the folder and normalised; an absolute path stays as it is */
  @Test
  public void testResolveAgainstFolder() {
    assertEquals(tempDir.resolve("data/x.pbf"), FileUtils.resolveAgainstFolder("data/../data/x.pbf", tempDir));
    var absolute = tempDir.resolve("other/y.pbf").toAbsolutePath();
    assertEquals(absolute, FileUtils.resolveAgainstFolder(absolute.toString(), Path.of("unused")));
  }

  /** Text that cannot be a path is refused */
  @Test
  public void testResolveAgainstFolderRefusesNonPath() {
    assertThrows(InvalidPathException.class, () -> FileUtils.resolveAgainstFolder("a\0b", tempDir));
  }
}
