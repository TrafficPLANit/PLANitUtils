package org.goplanit.utils.locale;

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
      countries2ISO.put(l.getDisplayCountry(Locale.ENGLISH), iso);
  }
    /* country names spelt otherwise than Java's English names, such as Saint Lucia for St. Lucia */
    countries2ISO.putAll(Map.of(CountryNames.ANTIGUA_BARBUDA, "AG", CountryNames.HONG_KONG, "HK",
        CountryNames.MACAU, "MO", CountryNames.EAST_TIMOR, "TL", CountryNames.ENGLAND, "GB",
        CountryNames.SAINT_KITS_NEVIS, "KN", CountryNames.SAINT_LUCIA, "LC",
        CountryNames.SAINT_VINCENT_GRENADINES, "VC", CountryNames.TRINIDAD_TOBAGO, "TT",
        CountryNames.TURKS_CAICOS_ISLANDS, "TC"));
  }
  
  /**Collect ISO2 code based on full name
   * 
   * @param countryName (full name as per English locale)
   * @return ISO2 code, null if incorrect name
   */
  public static String getIso2CountryCodeByName(String countryName){
    return countries2ISO.get(countryName);
  }
  
  /** Collect full name of country based on ISO2 code
   * 
   * @param iso2Code country code
   * @return full name under English Locale, empty string if not available
   */
  public static String getCountryNameCodeByIso2Code(String iso2Code){
    return new Locale("en", iso2Code).getDisplayCountry(Locale.ENGLISH);
  }  

}
