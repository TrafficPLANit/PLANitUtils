package org.planit.utils.misc;

import org.planit.utils.time.TimePeriod;

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
   * @return runId prefix
   */
  public static String createRunIdPrefix(long runId) {
    return surroundwithBrackets(String.format("run id: %d", runId));
  }
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular project
   * are prefixed with the exact same string, i.e.  {@code [project id: <id> ]}
   * 
   * @param projectId the project id
   * @return project prefix
   */  
  public static String createProjectPrefix(long projectId) {
    return surroundwithBrackets(String.format("project id: %d", projectId));
  }
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular network
   * are prefixed with the exact same string, i.e.  {@code [network id: <id> ]}
   * 
   * @param networkId the network id
   * @return network prefix
   */    
  public static String createNetworkPrefix(long networkId) {
    return surroundwithBrackets(String.format("network id: %d", networkId));
  }   
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular zoning
   * are prefixed with the exact same string, i.e.  {@code [zoning id: <id> ]}
   * 
   * @param zoningId the zoning id
   * @return zoning prefix
   */   
  public static String createZoningPrefix(long zoningId) {
    return surroundwithBrackets(String.format("zoning id: %d", zoningId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular demands
   * are prefixed with the exact same string, i.e.  {@code [demands id: <id> ]}
   * 
   * @param demandsId the demands id
   * @return demands prefix
   */   
  public static String createDemandsPrefix(long demandsId) {
    return surroundwithBrackets(String.format("demands id: %d", demandsId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular service network
   * are prefixed with the exact same string, i.e.  {@code [services network id: <id> ]}
   * 
   * @param serviceNetworkId the id
   * @return service network prefix
   */    
  public static String createServiceNetworkPrefix(long serviceNetworkId) {
    return surroundwithBrackets(String.format("services network id: %d", serviceNetworkId));
  }    
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular routed services
   * are prefixed with the exact same string, i.e.  {@code [routed services id: <id> ]}
   * 
   * @param routedServicesId the routed services id
   * @return routed services prefix
   */    
  public static String createRoutedServicesPrefix(long routedServicesId) {
    return surroundwithBrackets(String.format("routed services id: %d", routedServicesId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular od path sets
   * are prefixed with the exact same string, i.e.  {@code [od path sets id: <id> ]}
   * 
   * @param odPathSetsId the odPathSets id
   * @return od path sets Prefix
   */     
  public static String createOdPathSetsPrefix(long odPathSetsId) {
    return surroundwithBrackets(String.format("od path sets id: %d", odPathSetsId));
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
   * @param timePeriod the time period to create it for
   * @return time period prefix
   */
  public static String createTimePeriodPrefix(TimePeriod timePeriod) {
    String timePeriodReference = timePeriod.hasExternalId() ? "external id: " + timePeriod.getExternalId() : (timePeriod.hasXmlId() ? "xml id: "+timePeriod.getXmlId() : "");
    return surroundwithBrackets(String.format("time period: %s (id %d)", timePeriodReference, timePeriod.getId()));
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
  public static String logActiveStateByClassName(Object item, boolean activate) {
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
