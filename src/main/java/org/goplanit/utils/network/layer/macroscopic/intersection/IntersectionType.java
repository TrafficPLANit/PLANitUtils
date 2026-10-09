package org.goplanit.utils.network.layer.macroscopic.intersection;

/**
 * Kind of intersection: road traffic meeting at a junction, or a pedestrian crossing. An intersection can be both.
 *
 * @author markr
 */
public enum IntersectionType {

  /** the control applies to road traffic meeting at a junction */
  JUNCTION("junction"),

  /** the control applies to a pedestrian crossing */
  CROSSING("crossing");

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
