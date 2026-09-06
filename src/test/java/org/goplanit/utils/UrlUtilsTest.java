package org.goplanit.utils;

import org.goplanit.utils.misc.UrlUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlUtilsTest {

  @TempDir
  Path tempDir;

  @Test
  public void localFileUrlTest() throws Exception {
    Path filePath = Files.createFile(tempDir.resolve("sample.txt"));
    URL fileUrl = filePath.toUri().toURL();

    assertTrue(UrlUtils.isLocalFile(fileUrl));
    assertFalse(UrlUtils.isLocalDirectory(fileUrl));
    assertFalse(UrlUtils.isLocalZipFile(fileUrl));
  }

  @Test
  public void localDirectoryUrlTest() throws Exception {
    URL directoryUrl = tempDir.toUri().toURL();

    assertTrue(UrlUtils.isLocalDirectory(directoryUrl));
    assertFalse(UrlUtils.isLocalFile(directoryUrl));
    assertFalse(UrlUtils.isLocalZipFile(directoryUrl));
  }

  @Test
  public void localZipFileUrlTest() throws Exception {
    Path filePath = Files.createFile(tempDir.resolve("sample.zip"));
    URL fileUrl = filePath.toUri().toURL();

    assertTrue(UrlUtils.isLocalFile(fileUrl));
    assertTrue(UrlUtils.isLocalZipFile(fileUrl));
  }

  @Test
  public void nonLocalUrlTest() throws Exception {
    URL fileUrl = new URL("https://www.goplanit.org/sample.zip");

    assertFalse(UrlUtils.isLocalFile(fileUrl));
    assertFalse(UrlUtils.isLocalDirectory(fileUrl));
    assertFalse(UrlUtils.isLocalZipFile(fileUrl));
  }
}
