package org.goplanit.utils.geo;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.cs.CoordinateSystem;
import org.geotools.api.referencing.cs.CoordinateSystemAxis;
import org.geotools.referencing.CRS;
import org.goplanit.utils.epsg.ProjectedEpsgCodesByCountry;
import tech.units.indriya.unit.Units;

import javax.measure.Unit;

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
    Logger.getLogger("org.hsqldb").setLevel(Level.SEVERE);
    System.setProperty("hsqldb.reconfig_logging", "false");
    /* also ignore intermediate warnings while searching for EPSG matches */
    Logger.getLogger("org.geotools.referencing.factory").setLevel(Level.SEVERE);    
  }

  /** shorthand for EPSG:4326 code (WGS84) */
  public static String EPSG_4326 = "EPSG:4326";

  /** shorthand for WGS 84 EPSG code */
  public static String EPSG_CODE_FOR_WGS84 = EPSG_4326;

  /**
   * PLANit provides some defaults for EPSG codes to consider for its most used countries. This list is not exhaustive
   * so it may fail.
   *
   * @param countryName to lookup
   * @param useFallbackIfAbsent when true use fallback web mercator instead
   * @return epsg code if present, null otherwise
   */
  public static String findProjectedCrsEpsgCodeByCountryName(String countryName, boolean useFallbackIfAbsent){
    return ProjectedEpsgCodesByCountry.hasEpsgDefined(countryName) ?
            ProjectedEpsgCodesByCountry.getEpsg(countryName):
            ProjectedEpsgCodesByCountry.WORLD_PROJECTED_WGS84;
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

        if(crs==null) {
          LOGGER.warning(String.format("Unable to decode CRS %s to coordinate reference system",code));
        }
      } catch (Exception e1) {
        try {
          crs = CRS.decode(code, true);
        } catch (Exception e2) {
          e2.printStackTrace();
          LOGGER.warning(String.format("unable to find coordinate reference system for %s", code));
        }
      }
    }
    return crs;
  }

  /**
   * Verify if CRS is linear and compatible with a length unit, e.g., km, m, etc. IF so distances can be computed
   * cheaply and do not need a geodetic calclulator
   *
   * @param crs to check
   * @return true when linear, false otherwise
   */
  public static boolean isLinearCRSWithLengthCompatibleUnit(CoordinateReferenceSystem crs) {
    CoordinateSystem cs = crs.getCoordinateSystem();
    for (int i = 0; i < cs.getDimension(); i++) {
      CoordinateSystemAxis axis = cs.getAxis(i);
      var unit = axis.getUnit();

      if (unit == null) continue;

      // isCompatible(Units.METRE) means:
      // - the unit is a length unit compatible with metres
      //   (metres, kilometres, etc.), not degrees.
      if (unit.getSystemUnit().isCompatible(Units.METRE)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Verify if CRS is linear and compatible with exact length unit, e.g., km, m, etc. If so distances can be computed
   * cheaply and do not need a geodetic calclulator.
   *
   * @param crs to check
   * @param desiredUnit to confirm exact compatibility with
   * @return true when linear, false otherwise
   */
  public static boolean isLinearCRSWithExactUnit(CoordinateReferenceSystem crs, Unit<?> desiredUnit) {
    CoordinateSystem cs = crs.getCoordinateSystem();
    for (int i = 0; i < cs.getDimension(); i++) {
      CoordinateSystemAxis axis = cs.getAxis(i);
      var unit = axis.getUnit();

      if (unit == null) continue;

      // strictly check on unit
      if (unit.equals(desiredUnit)) {
        return true;
      }
    }
    return false;
  }

}
