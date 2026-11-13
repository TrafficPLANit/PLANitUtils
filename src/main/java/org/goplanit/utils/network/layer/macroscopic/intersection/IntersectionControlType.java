package org.goplanit.utils.network.layer.macroscopic.intersection;

/**
 * How traffic at an intersection is controlled
 *
 * @author markr
 */
public enum IntersectionControlType {

  /** controlled by traffic signals */
  SIGNALISED("signalised"),

  /** not controlled by traffic signals */
  UNSIGNALISED("unsignalised");

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
  IntersectionControlType(String value){
    this.value = value;
  }
}
