package org.planit.utils.locale;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utilities for locale
 * 
 * @author markr
 *
 */
public class LocaleUtils {
  
  /** store mapping from full name to iso2 code */
  protected static  final Map<String, String> countries2ISO = new HashMap<String, String>();
  
  /* populate countries2ISO for English locale */
  static {
    for (String iso : Locale.getISOCountries()) {
      Locale l = new Locale("en", iso);
      countries2ISO.put(l.getDisplayCountry(), iso);
  }
  }
  
  /**
   * @param countryName (full name as per English locale)
   * @return ISO2 code, null if incorrect name
   */
  public static String getIso2CountryCodeByName(String countryName){
    return countries2ISO.get(countryName);
  }
  
  /** collect full name of country based on ISO2 code
   * @param iso2Code country code
   * @return full name under English Locale
   */
  public static String getCountryNameCodeByIso2Code(String iso2Code){
    return new Locale("en", iso2Code).getDisplayCountry();
  }  

}
