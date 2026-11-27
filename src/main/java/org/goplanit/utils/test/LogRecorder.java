package org.goplanit.utils.test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Counts the records a logger publishes at a level or above, warnings by default, while this is one of its handlers
 * <p>
 * Tests check that a step warns, or stays quiet, by how many records it logs, never by their text, which is free to
 * change. For example, a factory refusing a link segment already on another toll road gives one warning:
 * </p>
 * <pre>
 * assertEquals(1, LogRecorder.countWarnings(TollRoadFactoryImpl.class, () -&gt; factory.registerNew(...)));
 * </pre>
 * <p>
 * A test following a logger over several steps, such as every read of a test class, adds a recorder to that logger
 * itself, reads {@link #getCount()} and resets it in between. Shipped in main so the tests of every repository share
 * it.
 * </p>
 *
 * @author markr
 */
public class LogRecorder extends Handler {

  /** lowest level of the records counted */
  private final Level lowestLevel;

  /** number of records counted */
  private int count = 0;

  /**
   * Count the records the logger of a class publishes at a level or above while an action runs
   *
   * @param loggingClass whose logger to follow, the logger named after its canonical name
   * @param lowestLevel lowest level of the records counted
   * @param action to run
   * @return number of records counted
   */
  public static int countRecords(Class<?> loggingClass, Level lowestLevel, Runnable action) {
    var logger = Logger.getLogger(loggingClass.getCanonicalName());
    var recorder = new LogRecorder(lowestLevel);
    logger.addHandler(recorder);
    try {
      action.run();
    } finally {
      logger.removeHandler(recorder);
    }
    return recorder.getCount();
  }

  /**
   * Count the warnings, and the more severe records, the logger of a class publishes while an action runs
   *
   * @param loggingClass whose logger to follow, the logger named after its canonical name
   * @param action to run
   * @return number of records counted
   */
  public static int countWarnings(Class<?> loggingClass, Runnable action) {
    return countRecords(loggingClass, Level.WARNING, action);
  }

  /**
   * Constructor, counting warnings and the more severe records
   */
  public LogRecorder() {
    this(Level.WARNING);
  }

  /**
   * Constructor
   *
   * @param lowestLevel lowest level of the records counted
   */
  public LogRecorder(Level lowestLevel) {
    this.lowestLevel = lowestLevel;
  }

  /**
   * Number of records counted
   *
   * @return count
   */
  public int getCount() {
    return count;
  }

  /**
   * Start counting from zero again
   */
  public void reset() {
    count = 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void publish(LogRecord record) {
    if (record.getLevel().intValue() >= lowestLevel.intValue()) {
      ++count;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void flush() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
  }
}
