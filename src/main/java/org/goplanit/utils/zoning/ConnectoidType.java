package org.goplanit.utils.zoning;

/**
 * Types of connectoids to more easily identify their purpose
 * 
 * @author markr
 *
 */
public enum ConnectoidType {

  NONE("none"),
  TRAVELLER_ACCESS("traveller_access"),
  PT_VEHICLE_STOP("pt_veh_stop"),
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
  ConnectoidType(String value){
    this.value = value;
  }
}
