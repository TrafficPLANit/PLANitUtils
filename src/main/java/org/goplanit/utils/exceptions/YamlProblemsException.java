package org.goplanit.utils.exceptions;

import org.goplanit.utils.misc.LoggingUtils;
import org.goplanit.utils.yaml.YamlProblem;

import java.util.List;

/**
 * Thrown when YAML text cannot be read, holding every problem found in it
 * <p>
 * For example, catching it to log each problem on a line of its own:
 * </p>
 * <pre>
 * try {
 *   var root = SimpleYamlReader.read(file);
 * } catch (YamlProblemsException e) {
 *   e.getProblems().forEach(problem -&gt; LOGGER.severe(problem.toString()));
 * }
 * </pre>
 * <p>
 * The reader checks the whole text before it stops, so all mistakes can be fixed in one go. The message of the
 * exception lists the problems, one per line.
 * </p>
 *
 * @author markr
 */
public class YamlProblemsException extends PlanItRunTimeException {

  /** generated UID */
  private static final long serialVersionUID = 1L;

  /** the problems found */
  private final List<YamlProblem> problems;

  /**
   * Constructor
   *
   * @param problems found, at least one
   */
  public YamlProblemsException(List<YamlProblem> problems) {
    super(LoggingUtils.problemsMessage("YAML has", problems));
    this.problems = List.copyOf(problems);
  }

  /**
   * The problems found, in the order found
   *
   * @return the problems
   */
  public List<YamlProblem> getProblems() {
    return problems;
  }
}
