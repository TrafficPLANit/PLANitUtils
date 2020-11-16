package org.planit.utils.epsg;

import java.util.HashMap;
import java.util.logging.Logger;

import org.planit.utils.locale.CountryNames;

/**
 * convenience method that provides a mapping from country name to a possible EPSG coordinate 
 * reference system code, e.g., "EPSG:1234". This list is currently hard coded and limited but
 * we hope to extend it and make it more flexible over time
 * 
 * @author markr
 *
 */
public class EpsgCodesByCountry {
  
  /** logger to use */
  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(EpsgCodesByCountry.class.getCanonicalName());
  
  /** Area of use: Australia, EPSG:3112 */
  public static final String AUSTRALIA_LAMBERT = "EPSG:3112";
  
  /** Area of use: Germany, 3-degree Gauss-Kruger zone 4, EPSG:31468 */
  public static final String GERMANY_DHDN = "EPSG:31468";
  
  /** Area of use: Netherlands, Rijks Driehoek Stelsel, EPSG:28992 */
  public static final String NETHERLANDS_RDS = "EPSG:31468";  
  
  /** Area of use: world, EPSG:4326, accessible via countryname: "global" as pwe {@link CountryNames.GLOBAL} */
  public static final String WORLD_WG84 = "EPSG:4326";
  
  /** mapping of country names to expected most commonly used EPSG code */
  protected static final HashMap<String, String> countryToEpsgCodes = new HashMap<String, String>();
  
  /** add a mapping to the register
   * 
   * @param countryName to use
   * @param epsgCode to use
   */
  protected static void add(String countryName, String epsgCode) {
    countryToEpsgCodes.put(countryName, epsgCode);
  }
    
  /* populate */
  static {
    /* generic */
    add(CountryNames.WORLD, WORLD_WG84);
    /* actual countries */
    add(CountryNames.GERMANY, GERMANY_DHDN);
    add(CountryNames.NETHERLANDS, NETHERLANDS_RDS);
    add(CountryNames.AUSTRALIA,AUSTRALIA_LAMBERT);
  }
  
  /** collect the epsg most likely applicable code based on the country name. 
   * If no mapping exists yet null is returned
   * 
   * @param countryName to use
   * @return epsg code string
   */
  public static final String getEpsg(String countryName) {
    return countryToEpsgCodes.get(countryName);
  }

}
