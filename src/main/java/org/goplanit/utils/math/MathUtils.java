package org.goplanit.utils.math;

import java.util.logging.Logger;

public class MathUtils {

  private static final Logger LOGGER = Logger.getLogger(MathUtils.class.getCanonicalName());

  /**
   * Perform Math.exp but truncate power to 700 as this causes overflow error otherwise (infinity which is not a number)
   *
   * @param power to apply exp to
   * @return result
   */
  public static double safeExp(double power){
    if(power > 700){
      power = 700;
    }
    return Math.exp(power);
  }
}
