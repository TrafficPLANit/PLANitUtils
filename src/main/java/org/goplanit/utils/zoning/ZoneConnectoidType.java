package org.goplanit.utils.zoning;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

/**
 * Types of connectoids to more easily identify their purpose
 * 
 * @author markr
 *
 */
public enum ZoneConnectoidType {
  /** none */
  NONE("none"),
  ZONE_ACCESS_EGRESS("access_egress"),
  /** traveller access */
  ZONE_ACCESS("access"),
  /** traveller egress */
  ZONE_EGRESS("egress"),
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

  /**
   * Obtain supported travel direction (can be both ways)
   * @return true when away from zone
   */
  public boolean isTravelDirectionAwayFromZone(){
    switch (this){
      case ZONE_EGRESS:
      case ZONE_ACCESS_EGRESS:
        return true;
      case ZONE_ACCESS:
      case PT_VEHICLE_STOP:
        return false;
      default:
        throw new PlanItRunTimeException("Direction unknown for type %s", this);
    }
  }

  /**
   * Obtain supported travel direction (can be both ways)
   * @return true when towards zone
   */
  public boolean isTravelDirectionTowardsZone(){
    switch (this){
      case ZONE_ACCESS:
      case ZONE_ACCESS_EGRESS:
      case PT_VEHICLE_STOP:
        return true;
      case ZONE_EGRESS:
        return false;
      default:
        throw new PlanItRunTimeException("Direction unknown for type %s", this);
    }
  }
}
