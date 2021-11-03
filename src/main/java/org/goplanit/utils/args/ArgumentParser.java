package org.goplanit.utils.args;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;

/**
 * This class parses program arguments and returns them as a map of file types
 * and file locations.
 * 
 * @author gman6028, markr
 *
 */
public class ArgumentParser {

  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(ArgumentParser.class.getCanonicalName());

  /**
   * The logger
   * 
   * @param splitter separator to use for string
   * @param arg to split
   * @param argsMap to place split results in
   * @return true when successful otherwise false
   */
  private static boolean updateMap(String splitter, String arg, Map<String, String> argsMap) {
    if (arg.contains(splitter)) {
      String[] parts = arg.split(splitter);
      argsMap.put(parts[0].toUpperCase().strip(), parts[1]);
      return true;
    }
    return false;
  }

  /**
   * Convert program arguments into a Map, with signature: {@code <key>=/-/:<value>},
   * e.g. the default style
   * 
   * @param args command-line arguments
   * @return Map of key value pairs
   * @throws PlanItException thrown if an argument cannot be parsed
   */
  protected static Map<String, String> convertArgsToMapDefaultStyle(final String[] args) throws PlanItException {
    Map<String, String> argsMap = new HashMap<String, String>();
    for (String arg : args) {
      boolean validKey = false;
      validKey = updateMap("=", arg, argsMap);
      validKey = validKey || updateMap("-", arg, argsMap);
      validKey = validKey || updateMap(":", arg, argsMap);
      if (!validKey) {
        throw new PlanItException("Argument %s cannot be parsed into a program input", arg);
      }
    }
    return argsMap;
  }

  /**
   * Convert program arguments into a Map, with signature: {@code --<key> <value>}, e.g.
   * the double hyphen style
   * 
   * @param args command-line arguments
   * @return Map of key value pairs
   * @throws PlanItException thrown if an argument cannot be parsed
   */
  protected static Map<String, String> convertArgsToMapDoubleHyphenStyle(String[] args) throws PlanItException {
    Map<String, String> argsMap = new HashMap<String, String>();
    boolean parseKey = true;
    String key = null;
    for (String arg : args) {
      String trimmedArg = arg.trim();
      
      /* finalise value-less key */
      if (!parseKey && trimmedArg.startsWith("--")) {
        /* preceding key has no value, can happen, so simply provide no value for that key and parse this as next key */
        argsMap.put(key, "");
        parseKey = true;  
      }
      
      /* parse key */
      if(parseKey) {
        if (!trimmedArg.startsWith("--")) {
          throw new PlanItException("keys should start with \\\"--\" but found %s", arg);
        }
        key = trimmedArg.substring(2);
        parseKey = false;
      }
      /* parse value */
      else {
        argsMap.put(key, trimmedArg);
        parseKey = true;
      }
    }
    return argsMap;
  }

  /**
   * Convert program arguments into a Map, with signature: {@code <key>=/-/:<value>},
   * e.g. the default style
   * 
   * @param args command-line arguments
   * @return Map of file locations
   * @throws PlanItException thrown if an argument cannot be parsed into a file type and file
   *           location
   */
  public static Map<String, String> convertArgsToMap(final String[] args) throws PlanItException {
    return convertArgsToMap(args, ArgumentStyle.DEFAULT);
  }

  /**
   * Convert program arguments into a Map, based on given style
   * 
   * @param args command-line arguments
   * @param style to apply
   * @return Map of key value pairs
   * @throws PlanItException thrown if an argument cannot be parsed
   */
  public static Map<String, String> convertArgsToMap(final String[] args, final ArgumentStyle style)
      throws PlanItException {
    switch (style) {
      case DEFAULT:
        return convertArgsToMapDefaultStyle(args);
      case DOUBLEHYPHEN:
        return convertArgsToMapDoubleHyphenStyle(args);
      default:
        LOGGER.warning("Unknown argument style provided, assuming default style");
        return convertArgsToMapDefaultStyle(args);
    }
  }
}
