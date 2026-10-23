package org.goplanit.utils.network.layer.macroscopic.intersection;

/**
 * How a green ratio or saturation flow factor got its value. An algorithm that computes these values only changes those
 * it computed itself, never those set directly
 *
 * @author markr
 */
public enum CapacityFactorOrigin {

  /** set directly, by the user or read from a file */
  SET_DIRECTLY,

  /** computed by an algorithm */
  COMPUTED;
}
