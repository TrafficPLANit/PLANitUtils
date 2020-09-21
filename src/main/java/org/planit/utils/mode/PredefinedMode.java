package org.planit.utils.mode;

public interface PredefinedMode extends Mode {

  /** check if the mode is one of the PLANit predefined mode types or not
   * 
   * @return true when predefined, false, when custom
   */
  default boolean isPredefinedModeType()
  {
    return true;
  }
  
}
