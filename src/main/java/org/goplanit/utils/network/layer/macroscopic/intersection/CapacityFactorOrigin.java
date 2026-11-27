package org.goplanit.utils.network.layer.macroscopic.intersection;

/**
 * How a green ratio or saturation flow factor got its value. An algorithm that computes these values only changes those
 * it computed itself, never those set directly
 *
 * @author markr
 */
public enum CapacityFactorOrigin {

  /** set directly, by the user or read from a file */
  SET_DIRECTLY("set_directly"),

  /** computed by an algorithm */
  COMPUTED("computed");

  /** value as used in persistence, e.g. a GIS export */
  private final String value;

  /**
   * Constructor
   *
   * @param value as used in persistence
   */
  private CapacityFactorOrigin(String value){
    this.value = value;
  }

  /**
   * Collect the value as used in persistence, e.g. a GIS export
   *
   * @return value
   */
  public String value() {
    return value;
  }
}
