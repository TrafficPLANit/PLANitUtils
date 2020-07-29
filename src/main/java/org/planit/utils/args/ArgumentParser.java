package org.planit.utils.args;

import java.util.HashMap;
import java.util.Map;

import org.planit.utils.exceptions.PlanItException;

/**
 * This class parses program arguments and returns them as a map of file types and file locations.
 * 
 * @author gman6028
 *
 */
public class ArgumentParser {

  /**
   * the logger
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
   * Convert MetroScan program arguments into a Map of file locations , with file type as key and
   * file location as values
   * 
   * @param args command-line arguments
   * @return Map of file locations
   * @throws PlanItException thrown if an argument cannot be parsed into a file type and file
   *           location
   */
  public static Map<String, String> convertArgsToMap(String[] args) throws PlanItException {
    Map<String, String> argsMap = new HashMap<String, String>();
    for (String arg : args) {
      boolean validKey = false;
      validKey = updateMap("=", arg, argsMap);
      validKey = validKey || updateMap("-", arg, argsMap);
      validKey = validKey || updateMap(":", arg, argsMap);
      if (!validKey) {
        String errorMessage = "Argument " + arg + " cannot be parsed into a program input.";
        throw new PlanItException(errorMessage);
      }
    }
    return argsMap;
  }
}
