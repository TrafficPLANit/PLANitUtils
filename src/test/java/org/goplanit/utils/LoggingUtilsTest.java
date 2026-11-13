package org.goplanit.utils;

import org.goplanit.utils.misc.LoggingUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests of {@link LoggingUtils}
 *
 * @author markr
 */
public class LoggingUtilsTest {

  /** A message listing problems has a heading line, then each problem on a line of its own, in order */
  @Test
  public void testProblemsMessageListsEachProblemOnALine() {
    var lines = LoggingUtils.problemsMessage("Files have", List.of("first", "second")).lines()
        .collect(Collectors.toList());
    assertEquals(3, lines.size());
    assertEquals(List.of("first", "second"), lines.subList(1, 3));
  }
}
