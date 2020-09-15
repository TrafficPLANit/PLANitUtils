package org.planit.utils.unit;

/**
 * Enumeration of possible units
 * 
 * @author gman6028, markr
 *
 */
public enum Units {

  VEH_KM("VEH_KM"),
  VEH_METER("VEH_M"),
  NONE("NONE"),
  VEH_HOUR("VEH_H"),
  VEH_SECOND("VEH_S"),
  VEH_MINUTE("VEH_MIN"),
  KM_HOUR("KM_H"),
  KM_MINUTE("KM_MIN"),
  KM_SECOND("KM_S"),
  METER_SECOND("M_S"),
  METER_MINUTE("M_MIN"),  
  METER_HOUR("M_S"),
  HOUR("H"),
  MINUTE("MIN"),
  SECOND("S"),
  KM("KM"),
  METER("M"),
  SRS("SRS");
  
  private final String value;

  Units(String v) {
    value = v;
  }

  public String value() {
    return value;
  }
}
