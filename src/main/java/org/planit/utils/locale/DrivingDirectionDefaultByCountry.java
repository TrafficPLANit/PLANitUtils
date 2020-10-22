package org.planit.utils.locale;

import java.util.HashSet;
import java.util.Set;

/**
 * convenience class that provides the driving direction per country (left hand drive or not)
 * based on https://en.wikipedia.org/wiki/Left-_and_right-hand_traffic
 * 
 * @author markr
 *
 */
public class DrivingDirectionDefaultByCountry {
  /**
   * all Left hand drive countries which determine the default direction on one way roundabouts when not tagged
   */
  protected static Set<String> leftHandDriveCountries = new HashSet<String>();
  
  /**
   * initialise all left hand drive countries 
   */
  protected static void initialiseLeftHandDriveCountries() {
        
    leftHandDriveCountries.add(CountryNames.ANTIGUA_BARBUDA);
    leftHandDriveCountries.add(CountryNames.AUSTRALIA);
    leftHandDriveCountries.add(CountryNames.BAHAMAS);
    leftHandDriveCountries.add(CountryNames.BANGLADESH);
    leftHandDriveCountries.add(CountryNames.BARBADOS);
    leftHandDriveCountries.add(CountryNames.BHUTAN);
    leftHandDriveCountries.add(CountryNames.BOTSWANA);
    leftHandDriveCountries.add(CountryNames.BRITISH_VIRGIN_ISLANDS);
    leftHandDriveCountries.add(CountryNames.BRUNEI);
    leftHandDriveCountries.add(CountryNames.HONG_KONG);
    leftHandDriveCountries.add(CountryNames.MACAU);
    leftHandDriveCountries.add(CountryNames.CAYMAN_ISLANDS);
    leftHandDriveCountries.add(CountryNames.CYPRUS);
    leftHandDriveCountries.add(CountryNames.DOMINICA);
    leftHandDriveCountries.add(CountryNames.EAST_TIMOR);
    leftHandDriveCountries.add(CountryNames.ENGLAND);
    leftHandDriveCountries.add(CountryNames.ESWATINI);
    leftHandDriveCountries.add(CountryNames.FALKLAND_ISLANDS);
    leftHandDriveCountries.add(CountryNames.FIJI);
    leftHandDriveCountries.add(CountryNames.GRENADA);
    leftHandDriveCountries.add(CountryNames.GUERNSEY);
    leftHandDriveCountries.add(CountryNames.GUYANA);
    leftHandDriveCountries.add(CountryNames.INDIA);
    leftHandDriveCountries.add(CountryNames.INDONESIA);
    leftHandDriveCountries.add(CountryNames.IRELAND);
    leftHandDriveCountries.add(CountryNames.JAMAICA);
    leftHandDriveCountries.add(CountryNames.JAPAN);
    leftHandDriveCountries.add(CountryNames.JERSEY);
    leftHandDriveCountries.add(CountryNames.KENYA);
    leftHandDriveCountries.add(CountryNames.KIRIBATI);
    leftHandDriveCountries.add(CountryNames.LESOTHO);
    leftHandDriveCountries.add(CountryNames.MALAWI);
    leftHandDriveCountries.add(CountryNames.MALAYSIA);
    leftHandDriveCountries.add(CountryNames.MALDIVES);
    leftHandDriveCountries.add(CountryNames.MALTA);    
    leftHandDriveCountries.add(CountryNames.MAURITIUS);
    leftHandDriveCountries.add(CountryNames.MOZAMBIQUE);
    leftHandDriveCountries.add(CountryNames.NAMIBIA);
    leftHandDriveCountries.add(CountryNames.NAURU);
    leftHandDriveCountries.add(CountryNames.NEPAL);
    leftHandDriveCountries.add(CountryNames.NEW_ZEALAND);
    leftHandDriveCountries.add(CountryNames.PAPUA_NEW_GUINEA);
    leftHandDriveCountries.add(CountryNames.PITCAIRN_ISLANDS);
    leftHandDriveCountries.add(CountryNames.SAINT_KITS_NEVIS);
    leftHandDriveCountries.add(CountryNames.SAINT_LUCIA);
    leftHandDriveCountries.add(CountryNames.SAINT_VINCENT_GRENADINES);
    leftHandDriveCountries.add(CountryNames.SAMOA);
    leftHandDriveCountries.add(CountryNames.SEYCHELLES);
    leftHandDriveCountries.add(CountryNames.SINGAPORE);
    leftHandDriveCountries.add(CountryNames.SOlOMON_ISLANDS);
    leftHandDriveCountries.add(CountryNames.SOUNTH_AFRICA);
    leftHandDriveCountries.add(CountryNames.SRI_LANKA);
    leftHandDriveCountries.add(CountryNames.SURINAME);
    leftHandDriveCountries.add(CountryNames.TANZANIA);
    leftHandDriveCountries.add(CountryNames.THAILAND);
    leftHandDriveCountries.add(CountryNames.TONGA);
    leftHandDriveCountries.add(CountryNames.TRINIDAD_TOBAGO);
    leftHandDriveCountries.add(CountryNames.TURKS_CAICOS_ISLANDS);
    leftHandDriveCountries.add(CountryNames.TUVALU);
    leftHandDriveCountries.add(CountryNames.UGANDA);
    leftHandDriveCountries.add(CountryNames.US_VIRGIN_ISLANDS);
    leftHandDriveCountries.add(CountryNames.ZAMBIA);
    leftHandDriveCountries.add(CountryNames.ZIMBABWE);
  }  
  
  static {
    initialiseLeftHandDriveCountries();
  }
  
  /** check if country is left hand drive or not
   * @param countryName to check
   * @return true when left hand drive, false otherwise
   */
  public static boolean isLeftHandDrive(String countryName) {
    return leftHandDriveCountries.contains(countryName);
  }
  
  /** check if country is left hand drive or not
   * @param countryName to check
   * @return true when left hand drive, false otherwise
   */
  public static boolean isRightHandDrive(String countryName) {
    return !isLeftHandDrive(countryName);
  }  
}
