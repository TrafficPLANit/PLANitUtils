package org.planit.utils.misc;

/**
 * some utilities for consistent logging message creation in PLANit
 * 
 * @author markr
 */
public class LoggingUtils {
  
  /** Surround string with brackets "[%s]"
   * 
   * @param message to surround with brackets
   * @return bracketed string
   */
  public static String surroundwithBrackets(String message) {
    return String.format("[%s] ", message);
  }
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular traffic assignment run
   * are prefixed with the exact same string, i.e.  {@code [run id: <id> ]}
   * 
   * @param runId the run id
   * @return runId Prefix
   */
  public static String createRunIdPrefix(long runId) {
    return surroundwithBrackets(String.format("run id: %d", runId));
  }
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular project
   * are prefixed with the exact same string, i.e.  {@code [project id: <id> ]}
   * 
   * @param projectId the project id
   * @return project Prefix
   */  
  public static String createProjectPrefix(long projectId) {
    return surroundwithBrackets(String.format("project id: %d", projectId));
  }
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular network
   * are prefixed with the exact same string, i.e.  {@code [network id: <id> ]}
   * 
   * @param networkId the network id
   * @return network Prefix
   */    
  public static String createNetworkPrefix(long networkId) {
    return surroundwithBrackets(String.format("network id: %d", networkId));
  }   
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular zoning
   * are prefixed with the exact same string, i.e.  {@code [zoning id: <id> ]}
   * 
   * @param zoningId the zoning id
   * @return zoning Prefix
   */   
  public static String createZoningPrefix(long zoningId) {
    return surroundwithBrackets(String.format("zoning id: %d", zoningId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular demands
   * are prefixed with the exact same string, i.e.  {@code [demands id: <id> ]}
   * 
   * @param demandsId the demands id
   * @return demands Prefix
   */   
  public static String createDemandsPrefix(long demandsId) {
    return surroundwithBrackets(String.format("demands id: %d", demandsId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular od route sets
   * are prefixed with the exact same string, i.e.  {@code [od route sets id: <id> ]}
   * 
   * @param odRouteSetsId the odRouteSets id
   * @return od route sets Prefix
   */     
  public static String createOdRouteSetsPrefix(long odRouteSetsId) {
    return surroundwithBrackets(String.format("od route sets id: %d", odRouteSetsId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular output formatters
   * are prefixed with the exact same string, i.e.  {@code [output formatter id: <id> ]}
   * 
   * @param outputFormatterId the output formatter id
   * @return output formatter prefix
   */     
  public static String createOutputFormatterPrefix(long outputFormatterId) {
    return surroundwithBrackets(String.format("output formatter id: %d", outputFormatterId));
  }  
  
  /**
   * 
   * Create a prefix for the logger so that all logging items specific to a particular time period
   * are prefixed with the exact same string, i.e.  {@code [time period : <external id> (id <id>) ]}
   * 
   * @param timePeriodExternalId the time period's external id
   * @param timePeriodId the time period internal id
   * @return time period prefix
   */
  public static String createTimePeriodPrefix(Object timePeriodExternalId, long timePeriodId) {
    return surroundwithBrackets(String.format("time period: %s (id %d)", timePeriodExternalId, timePeriodId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular traffic assignment run
   * are prefixed with the exact same iteration string, i.e.  {@code [i=<id> ]}
   * 
   * @param iterationIndex the iteration index
   * @return iteration prefix
   */  
  public static String createIterationPrefix(int iterationIndex) {
    return surroundwithBrackets(String.format("iteration: %d", iterationIndex));
  }  
  
  /** create a string that states if item is activated or deactivated based and provide the simple class name
   * @param item to (de)activate
   * @param activate indicate to activate or deactive
   * @return the string (de)activated :  {@code <simple class name>}
   */
  public static String activateItemByClassName(Object item, boolean activate) {
    return (activate ? "activated: " : "deactivated :") + item.getClass().getSimpleName();
  }
  
  /** create a string that gets the class simple name and surrounds them with brackets
   * @param item to to apply
   * @return the string  {@code [<class simple name>]}
   */
  public static String getClassNameWithBrackets(Object item) {
    return surroundwithBrackets(item.getClass().getSimpleName());
  }



}
