package org.goplanit.utils.geo;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/** Utilities for coordinate Reference systems
 * 
 * @author markr
 *
 */
public class PlanitCrsUtils {
  
  /** the logger to use */
  private static final Logger LOGGER = Logger.getLogger(PlanitCrsUtils.class.getCanonicalName());
  
  /**
   * make sure we silence the Hsql logging that is used by CRS to collect crs for different countries. Make sure this is called
   * BEFORE it is loaded, otherwise it is too late
   */
  protected static void silenceHsqlLogging() {
    Logger.getLogger("org.hsqldb").setLevel(Level.WARNING);
    System.setProperty("hsqldb.reconfig_logging", "false");
  }
  
  /**
   * create a coordinate reference system instance based on String representation, e.g. "EPSG:4326" for WGS84", using the underlying geotools hsql authority factory. see also
   * {@code https://docs.geotools.org/latest/userguide/library/referencing/crs.html} on some context on why we include the hsql dependency in the planit build to ensure that the
   * provided crs codes here can actually be transformed into a viable CRS and why it makes sense to provide this simple wrapper method in this utility class
   * <p>
   * always make sure you lookup the CRS via this method as it ensures the logging of PLANit is not messed up by the geotools-HSQL dependency since we programmatically disallow it
   * to overwrite our logging configuration in the static initialiser of this class.
   * </p>
   * 
   * @param code for the CRS
   * @return the created coordinate reference system
   */
  public static CoordinateReferenceSystem createCoordinateReferenceSystem(String code) {
    silenceHsqlLogging();
    
    CoordinateReferenceSystem crs = null;
    if (code != null) {
      try {
        /* decode lookup is performed using the gt hsql database which is loaded as dependency in pom */
        crs = CRS.decode(code);
      } catch (Exception e1) {
        try {
          crs = CRS.decode(code, true);
        } catch (Exception e2) {
          LOGGER.warning(String.format("unable to find coordinate reference system for %s", code));
        }
      }
    }
    return crs;
  }
}
