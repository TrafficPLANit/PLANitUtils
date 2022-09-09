package org.goplanit.utils.misc;

import org.goplanit.utils.geo.PlanitJtsCrsUtils;
import org.goplanit.utils.time.TimePeriod;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.function.Predicate;
import java.util.logging.Logger;

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
  public static String runIdPrefix(long runId) {
    return surroundwithBrackets(String.format("run id: %d", runId));
  }
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular project
   * are prefixed with the exact same string, i.e.  {@code [project id: <id> ]}
   * 
   * @param projectId the project id
   * @return project prefix
   */  
  public static String projectPrefix(long projectId) {
    return surroundwithBrackets(String.format("project id: %d", projectId));
  }
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular network
   * are prefixed with the exact same string, i.e.  {@code [network id: <id> ]}
   * 
   * @param networkId the network id
   * @return network prefix
   */    
  public static String networkPrefix(long networkId) {
    return surroundwithBrackets(String.format("network id: %d", networkId));
  }   
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular zoning
   * are prefixed with the exact same string, i.e.  {@code [zoning id: <id> ]}
   * 
   * @param zoningId the zoning id
   * @return zoning prefix
   */   
  public static String zoningPrefix(long zoningId) {
    return surroundwithBrackets(String.format("zoning id: %d", zoningId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular demands
   * are prefixed with the exact same string, i.e.  {@code [demands id: <id> ]}
   * 
   * @param demandsId the demands id
   * @return demands prefix
   */   
  public static String demandsPrefix(long demandsId) {
    return surroundwithBrackets(String.format("demands id: %d", demandsId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular service network
   * are prefixed with the exact same string, i.e.  {@code [services network id: <id> ]}
   * 
   * @param serviceNetworkId the id
   * @return service network prefix
   */    
  public static String serviceNetworkPrefix(long serviceNetworkId) {
    return surroundwithBrackets(String.format("services network id: %d", serviceNetworkId));
  }

  /**
   * Create a prefix for the logger so that all logging items specific to a particular service network layer
   * are prefixed with the exact same string, i.e.  {@code [s_layer id: <id> ]}
   *
   * @param serviceNetworkLayerId the id
   * @return service network prefix
   */
  public static String serviceNetworkLayerPrefix(long serviceNetworkLayerId) {
    return surroundwithBrackets(String.format("s_layer id: %d", serviceNetworkLayerId));
  }

  /**
   * Create a prefix for the logger so that all logging items specific to a particular routed services
   * are prefixed with the exact same string, i.e.  {@code [routed services id: <id> ]}
   * 
   * @param routedServicesId the routed services id
   * @return routed services prefix
   */    
  public static String routedServicesPrefix(long routedServicesId) {
    return surroundwithBrackets(String.format("routed services id: %d", routedServicesId));
  }

  /**
   * Create a prefix for the logger so that all logging items specific to a particular routed services layer
   * are prefixed with the exact same string, i.e.  {@code [rs_layer id: <id> ]}
   *
   * @param routedServiceLayerId the routed services id
   * @return routed services prefix
   */
  public static String routedServiceLayerPrefix(long routedServiceLayerId) {
    return surroundwithBrackets(String.format("rs_layer id: %d", routedServiceLayerId));
  }

  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular od path sets
   * are prefixed with the exact same string, i.e.  {@code [od path sets id: <id> ]}
   * 
   * @param odPathSetsId the odPathSets id
   * @return od path sets Prefix
   */     
  public static String odPathSetsPrefix(long odPathSetsId) {
    return surroundwithBrackets(String.format("od path sets id: %d", odPathSetsId));
  }  
  
  /**
   * Create a prefix for the logger so that all logging items specific to a particular output formatters
   * are prefixed with the exact same string, i.e.  {@code [output formatter id: <id> ]}
   * 
   * @param outputFormatterId the output formatter id
   * @return output formatter prefix
   */     
  public static String outputFormatterPrefix(long outputFormatterId) {
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
  public static String timePeriodPrefix(TimePeriod timePeriod) {
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
  public static String iterationPrefix(int iterationIndex) {
    return surroundwithBrackets(String.format("iteration: %d", iterationIndex));
  }  
  
  /** create a string that states if item is activated or deactivated based and provide the simple class name
   * @param item to (de)activate
   * @param activate indicate to activate or deactive
   * @return the string (de)activated :  {@code <simple class name>}
   */
  public static String logActiveStateByClassName(Object item, boolean activate) {
    return (activate ? "activated: " : "deactivated:") + item.getClass().getSimpleName();
  }
  
  /** create a string that gets the class simple name and surrounds them with brackets
   * @param item to to apply
   * @return the string  {@code [<class simple name>]}
   */
  public static String getClassNameWithBrackets(Object item) {
    return surroundwithBrackets(item.getClass().getSimpleName());
  }

  /** surround the string with repetitions of given character
   * 
   * @param theString to surround
   * @param c character to us
   * @param repeat num reptitions on either side
   * @return created string
   */
  public static String surround(String theString, char c, int repeat) {
    var sb = new StringBuilder();
    for(int i = 0 ; i < repeat ; i++) {
      sb.append(c);
    }
    sb.append(" ").append(theString).append(" ");
    for(int i = 0 ; i < repeat ; i++) {
      sb.append(c);
    }
    
    return sb.toString();
  }

  /**
   * Log severe message when null
   *
   * @param object to check
   * @param logger to use
   * @param message to log
   * @param arguments arguments of message
   */
  public static void LogSevereIfNull(Object object, Logger logger, String message, Object... arguments){
    if(object==null){
      logger.severe(String.format(message, arguments));
    }
  }

  /** log the given warning message if predicate holds
   *
   * @param message to log if not too close to bounding box
   * @param testObject to test on
   * @param predicate to use
   */
  public static <T> void logWarningIf(Logger logger, String message, T testObject, Predicate<T> predicate) {
    if(predicate.test(testObject)) {
      logger.warning(message);
    }
  }


}
