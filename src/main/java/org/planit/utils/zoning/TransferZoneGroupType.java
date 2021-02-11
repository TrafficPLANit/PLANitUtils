package org.planit.utils.zoning;

/**
 * Types of transfer zone groups to more easily identify their purpose
 * 
 * @author markr
 *
 */
public enum TransferZoneGroupType {

  NONE("none"),
  TRAIN_STATION("train station"),
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
  TransferZoneGroupType(String value){
    this.value = value;
  }
}
