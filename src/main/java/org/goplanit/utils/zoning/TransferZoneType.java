package org.goplanit.utils.zoning;

/**
 * Types of transfer zones to more easily identify their purpose
 * 
 * @author markr
 *
 */
public enum TransferZoneType {

  NONE("none"),
  PLATFORM("platform"),
  POLE("pole"),
  SMALL_STATION("small station"),
  STATION("station"),
  UNKNOWN("unknown");
  
  
  private final String value;
  
  /** Colect the value
   * @return value
   */
  public String value() {
    return value;
  }
  
  /** Constructor
   * @param value representation of enum
   */
  TransferZoneType(String value){
    this.value = value;
  }
}
