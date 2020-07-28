package org.planit.utils.misc;

public class LoggingUtils {

  /**
   * Create a prefix for the logger so that all logging itmes specific to a particular traffic assignment run
   * are prefixed with the exact same string, i.e. "[run id: <id> ]"
   * 
   * @return runIdPRefix
   */
  public static String createRunIdPrefix(long runId) {
    return "[run id: " + runId + "] ";
  }
  
  /** create a string that states if item is activated or deactivated based and provide the simple class name
   * @param item to (de)activate
   * @param activate indicate to activate or deactive
   * @return the string (de)activated : <simple class name>
   */
  public static String activateItemByClassName(Object item, boolean activate) {
    return (activate ? "activated: " : "deactivated :") + item.getClass().getSimpleName();
  }
}
