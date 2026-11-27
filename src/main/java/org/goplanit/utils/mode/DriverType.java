package org.goplanit.utils.mode;

/**
 * Who drives a mode; part of usability features
 *
 * @author markr
 *
 */
public enum DriverType {

  /** one of the people travelling in it drives, e.g. in a car, on a motorbike or on a bicycle */
  TRAVELLER,

  /** someone who is not one of the people travelling in it drives, e.g. in a taxi, a ride share or a bus */
  OPERATOR,

  /** nobody drives, e.g. when walking or in an automated vehicle */
  NONE
}
