package org.goplanit.utils;

import org.goplanit.utils.locale.CountryNames;
import org.goplanit.utils.locale.LocaleUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests of {@link LocaleUtils}: every country name of {@link CountryNames} converts to its ISO2 code, also the names
 * Java writes otherwise, whatever language the machine shows names in
 *
 * @author markr
 */
public class LocaleUtilsTest {

  /** Every country name of CountryNames, the global one aside, converts to an ISO2 code */
  @Test
  public void everyCountryNameConverts() throws IllegalAccessException {
    var notConverted = new ArrayList<String>();
    for (var field : CountryNames.class.getFields()) {
      if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
        var name = (String) field.get(null);
        if (!CountryNames.GLOBAL.equals(name) && LocaleUtils.getIso2CountryCodeByName(name) == null) {
          notConverted.add(name);
        }
      }
    }
    assertEquals(List.of(), notConverted);
  }

  /** A name Java writes otherwise, or differently by the language of the machine, converts to its country's code */
  @Test
  public void namesJavaWritesOtherwise() {
    assertEquals("LC", LocaleUtils.getIso2CountryCodeByName(CountryNames.SAINT_LUCIA));
    assertEquals("VI", LocaleUtils.getIso2CountryCodeByName(CountryNames.US_VIRGIN_ISLANDS));
    assertEquals("AU", LocaleUtils.getIso2CountryCodeByName(CountryNames.AUSTRALIA));
    assertEquals("Australia", LocaleUtils.getCountryNameCodeByIso2Code("AU"));
  }
}
