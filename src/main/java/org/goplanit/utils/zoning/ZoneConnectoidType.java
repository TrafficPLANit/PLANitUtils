package org.goplanit.utils.zoning;

/**
 * Types of connectoids to more easily identify their purpose
 * 
 * @author markr
 *
 */
public enum ZoneConnectoidType {
  /** none */
  NONE("none"),
  /** traveller access */
  TRAVELLER_ACCESS("traveller_access"),
  /** pt vehicle stop */
  PT_VEHICLE_STOP("pt_veh_stop"),
  /** unknown */
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
  ZoneConnectoidType(String value){
    this.value = value;
  }
}
