package org.goplanit.utils.network.layer.macroscopic.intersection;

/**
 * Kind of intersection: road traffic meeting at a junction, a pedestrian crossing, or a ramp meter. An intersection can
 * be more than one: a junction can also be a crossing, or hold a crossing or a ramp meter consolidation joined into it.
 *
 * @author markr
 */
public enum IntersectionType {

  /** the control applies to road traffic meeting at a junction */
  JUNCTION("junction"),

  /** the control applies to a pedestrian crossing */
  CROSSING("crossing"),

  /** the control holds road traffic back on its own, letting one or two vehicles go at a time, as a ramp meter does on
   * a motorway entry ramp */
  RAMP_METER("ramp_meter");

  /** value as used in persistence, e.g. PLANit XML */
  private final String value;

  /**
   * Collect the value as used in persistence, e.g. PLANit XML
   *
   * @return value
   */
  public String value() {
    return value;
  }

  /**
   * Constructor
   *
   * @param value as used in persistence
   */
  IntersectionType(String value){
    this.value = value;
  }
}
