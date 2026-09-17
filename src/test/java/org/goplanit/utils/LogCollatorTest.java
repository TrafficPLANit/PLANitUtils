package org.goplanit.utils;

import org.goplanit.utils.misc.LogCollator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test the collation of repeated log worthy events
 *
 * @author markr
 */
public class LogCollatorTest {

  private static final String NO_PATH = "no physical path";

  private static final String WRONG_SIDE = "stop on wrong side of road";

  @Test
  public void emptyCollatorTest() {
    var collator = LogCollator.create();

    assertTrue(collator.isEmpty());
    assertEquals(0, collator.getTotalOccurrences());
    assertEquals(0, collator.getOccurrences(NO_PATH));
    assertNull(collator.getTemplate(NO_PATH));
    assertTrue(collator.getTemplateIds().isEmpty());
  }

  @Test
  public void collationByTemplateTest() {
    var collator = LogCollator.create();

    collator.increment(NO_PATH, "leg_1");
    collator.increment(NO_PATH, "leg_2", "between stop a and b");
    collator.increment(WRONG_SIDE, "stop_1");

    assertFalse(collator.isEmpty());
    assertEquals(3, collator.getTotalOccurrences());
    assertEquals(2, collator.getOccurrences(NO_PATH));
    assertEquals(1, collator.getOccurrences(WRONG_SIDE));
    assertEquals(2, collator.getTemplateIds().size());

    /* detail is retained for later listing even though it does not surface in the log summary */
    var occurrences = collator.getTemplate(NO_PATH).getRetainedOccurrences();
    assertEquals(2, occurrences.size());
    assertEquals("leg_1", occurrences.get(0).getEntityId());
    assertFalse(occurrences.get(0).hasDetail());
    assertEquals("between stop a and b", occurrences.get(1).getDetail());
    assertTrue(occurrences.get(1).hasDetail());
  }

  @Test
  public void entityLessOccurrenceTest() {
    var collator = LogCollator.create();

    collator.increment(NO_PATH);

    assertEquals(1, collator.getOccurrences(NO_PATH));
    var occurrence = collator.getTemplate(NO_PATH).getRetainedOccurrences().get(0);
    assertFalse(occurrence.hasEntityId());
    assertFalse(occurrence.hasDetail());
  }

  @Test
  public void retentionLimitTest() {
    var collator = LogCollator.createWithRetentionLimit(3);

    IntStream.range(0, 10).forEach(i -> collator.increment(NO_PATH, "leg_" + i));

    var template = collator.getTemplate(NO_PATH);

    /* the total remains exact while only the first entries are available for listing */
    assertEquals(10, template.getOccurrences());
    assertEquals(3, template.getRetainedOccurrences().size());
    assertTrue(template.hasUnretainedOccurrences());

    var retainedIds = template.getRetainedOccurrences().stream().map(
        LogCollator.Occurrence::getEntityId).collect(Collectors.toList());
    assertEquals(java.util.List.of("leg_0", "leg_1", "leg_2"), retainedIds);
  }

  @Test
  public void unlimitedRetentionTest() {
    var collator = LogCollator.create();

    IntStream.range(0, 1000).forEach(i -> collator.increment(NO_PATH, "leg_" + i));

    var template = collator.getTemplate(NO_PATH);
    assertEquals(1000, template.getOccurrences());
    assertEquals(1000, template.getRetainedOccurrences().size());
    assertFalse(template.hasUnretainedOccurrences());
  }

  @Test
  public void orderingTest() {
    var collator = LogCollator.create();

    collator.increment("b_template", "1");
    IntStream.range(0, 5).forEach(i -> collator.increment("prolific", "" + i));
    collator.increment("a_template", "1");

    var ordered = collator.getTemplatesByOccurrencesDescending().stream().map(
        LogCollator.CollatedTemplate::getTemplateId).collect(Collectors.toList());

    /* most prolific first, equally sized templates ordered by id so the summary is stable between runs */
    assertEquals(java.util.List.of("prolific", "a_template", "b_template"), ordered);
  }

  @Test
  public void resetTest() {
    var collator = LogCollator.create();

    collator.increment(NO_PATH, "leg_1");
    collator.reset();

    assertTrue(collator.isEmpty());
    assertEquals(0, collator.getTotalOccurrences());
    assertNull(collator.getTemplate(NO_PATH));
  }

  @Test
  public void missingTemplateIdTest() {
    var collator = LogCollator.create();

    assertThrows(IllegalArgumentException.class, () -> collator.increment(null, "leg_1"));
    assertThrows(IllegalArgumentException.class, () -> collator.increment(" ", "leg_1"));
    assertTrue(collator.isEmpty());
  }

  @Test
  public void concurrentIncrementTest() throws InterruptedException {
    final int numThreads = 8;
    final int numIncrementsPerThread = 5000;

    var collator = LogCollator.createWithRetentionLimit(10);
    var executorService = Executors.newFixedThreadPool(numThreads);
    try {
      IntStream.range(0, numThreads).forEach(thread -> executorService.submit(
          () -> IntStream.range(0, numIncrementsPerThread).forEach(
              i -> collator.increment(NO_PATH, "leg_" + thread + "_" + i))));
      executorService.shutdown();
      assertTrue(executorService.awaitTermination(30, TimeUnit.SECONDS));
    } finally {
      executorService.shutdownNow();
    }

    var template = collator.getTemplate(NO_PATH);

    /* every occurrence is counted regardless of the thread recording it, while retention stays within its bound */
    assertEquals(numThreads * numIncrementsPerThread, template.getOccurrences());
    assertEquals(10, template.getRetainedOccurrences().size());
  }
}
