package org.goplanit.utils.problem;

import org.goplanit.utils.exceptions.ProblemsRunTimeException;
import org.goplanit.utils.misc.LoggingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * The problems found in one check, kept to be reported together
 * <p>
 * Whoever checks something, such as settings files being read or a pricing scheme being registered, records each
 * problem where it finds it and goes on, so every problem is found in one pass and a user fixes them all in one go. At
 * the end it logs them as one warning, throws them as one exception, or hands them to its caller to log, each problem
 * on a line of its own. For example, a check of settings:
 * </p>
 * <pre>
 * var problems = new Problems&lt;SettingsProblemKind&gt;();
 * problems.addIf(write == null, SettingsProblemKind.UNKNOWN_KEY, source.getDescription(), key, "No such key");
 * // ... the other keys
 * problems.throwIfAny("Settings files have");
 * </pre>
 *
 * @author markr
 * @param <K> type of the kinds of problem
 */
public final class Problems<K extends Enum<K>> {

  /** the problems found, in the order found */
  private final List<Problem<K>> problems = new ArrayList<>();

  /**
   * Record a problem
   *
   * @param kind the kind of problem
   * @param source the file or text the problem is in, empty when not given
   * @param subject what in the source the problem is about, empty for the source as a whole
   * @param detail what is wrong, for people
   */
  public void add(K kind, String source, String subject, String detail) {
    problems.add(new Problem<>(kind, source, subject, detail));
  }

  /**
   * Record a problem when it was found, so a check reads as one line
   *
   * @param found true when the problem was found, false to record nothing
   * @param kind the kind of problem
   * @param source the file or text the problem is in, empty when not given
   * @param subject what in the source the problem is about, empty for the source as a whole
   * @param detail what is wrong, for people
   */
  public void addIf(boolean found, K kind, String source, String subject, String detail) {
    if (found) {
      add(kind, source, subject, detail);
    }
  }

  /**
   * Record every problem another pass found, after those recorded already, such as a check run as a later step
   *
   * @param others the problems to add
   */
  public void addAll(Problems<K> others) {
    problems.addAll(others.problems);
  }

  /**
   * Whether a problem of a kind was found
   *
   * @param kind to look for
   * @return true when one was
   */
  public boolean has(K kind) {
    return hasAny(problemKind -> problemKind == kind);
  }

  /**
   * Whether a problem of a kind meeting a condition was found, such as a kind whose outcome refuses what was checked
   *
   * @param condition a kind must meet
   * @return true when one was
   */
  public boolean hasAny(Predicate<? super K> condition) {
    return problems.stream().anyMatch(problem -> condition.test(problem.getKind()));
  }

  /**
   * Whether no problem was found
   *
   * @return true when none was
   */
  public boolean isEmpty() {
    return problems.isEmpty();
  }

  /**
   * The number of problems found, so a check can tell whether a step found any more
   *
   * @return the number
   */
  public int size() {
    return problems.size();
  }

  /**
   * The problems found
   *
   * @return the problems, in the order found
   */
  public Stream<Problem<K>> stream() {
    return problems.stream();
  }

  /**
   * Log the problems found as one warning, each on a line of its own, when there is any
   *
   * @param logger to log with
   * @param introduction of the warning, such as {@code Scheme westconnex has}
   */
  public void logIfAny(Logger logger, String introduction) {
    if (!isEmpty()) {
      logger.warning(LoggingUtils.problemsMessage(introduction, problems));
    }
  }

  /**
   * Throw the problems found as one exception, when there is any
   *
   * @param introduction of the exception's message, such as {@code Settings files have}
   * @throws ProblemsRunTimeException holding every problem found, when there is any
   */
  public void throwIfAny(String introduction) {
    if (!isEmpty()) {
      throw new ProblemsRunTimeException(introduction, problems);
    }
  }
}
