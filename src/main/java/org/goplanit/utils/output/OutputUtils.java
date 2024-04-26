package org.goplanit.utils.output;

import org.locationtech.jts.geom.Geometry;

/**
 * Utility Functions used during output
 * 
 * @author gman6028
 *
 */
public class OutputUtils {

  /**
   * Formats an object (if a double, outputs value to 7 decimal places
   * 
   * @param value the value to be output
   * @return the formatted output
   */
  public static Object formatObject(Object value) {

    if (value == null) {
      return "";
    } else if (value instanceof Double) {
      double outDouble = (double) value;
      return String.format("%.7f", outDouble);
    } else if (value instanceof Geometry) {
      /* geometry needs conversion to string for it to be writeable */
      return value.toString();
    }else {
      return value;
    }
  }

}
