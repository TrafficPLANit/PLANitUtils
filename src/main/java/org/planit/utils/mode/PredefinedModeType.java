package org.planit.utils.mode;

/**
 * While PLANit allows the user to come up with its own modes. It does suggest a number of predefined modes to use. This is especially useful to get started quickly or when one
 * wants to use the PLANit memory model to convert networks of one type to another with PLANit as the intermediary memory model, in which case some kind of mapping between known
 * modes is required.
 * 
 * When a custom mode is used, i.e., the mode factory is not used to create one of the defaults, the type of the mode is automatically set to CUSTOM
 * 
 * @author markr
 *
 */
public enum PredefinedModeType {

  BICYCLE("bicycle"),
  CUSTOM("custom"), 
  CAR("car"),
  CAR_SHARE("car_share"),
  CAR_HIGH_OCCUPANCY("car_hov"),
  BUS("bus"), 
  PEDESTRIAN("pedestrian"),
  MOTOR_BIKE("motor_bike"),
  SUBWAY("subway"),
  TRAIN("train"), 
  TRAM("tram"),
  LIGHTRAIL("light_rail"),
  GOODS_VEHICLE("goods"),            /* non-articulated goods vehicle, up to 3.5 tonnes */
  HEAVY_GOODS_VEHICLE("hgv"),        /* non-articulated goods vehicle, over to 3.5 tonnes */
  LARGE_HEAVY_GOODS_VEHICLE("lhgv"); /* articulated large truck */  

  /** string representation of predefined mode type */
  private final String value;
  
  /** constructor
   * @param v string representation of predefined mode
   */
  PredefinedModeType(final String v) {
    value = v;
  }

  public final String value() {
    return value;
  }
  
  /** convert string to enum. When no match can be found, the custom type is returned
   * @param predefinedModeTypeValue to convert to enum
   * @return the enum value, CUSTOM when no match could be found
   */
  public static PredefinedModeType create(final String predefinedModeTypeValue) {
    if(BICYCLE.value().equals(predefinedModeTypeValue)) {
      return BICYCLE;
    }else if(CAR.value().equals(predefinedModeTypeValue)) {
      return CAR;
    }else if(CAR_SHARE.value().equals(predefinedModeTypeValue)) {
      return CAR_SHARE;
    }else if(CAR_HIGH_OCCUPANCY.value().equals(predefinedModeTypeValue)) {
      return CAR_HIGH_OCCUPANCY;
    }else if(BUS.value().equals(predefinedModeTypeValue)) {
      return BUS;
    }else if(PEDESTRIAN.value().equals(predefinedModeTypeValue)) {
      return PEDESTRIAN;
    }else if(MOTOR_BIKE.value().equals(predefinedModeTypeValue)) {
      return MOTOR_BIKE;
    }else if(TRAIN.value().equals(predefinedModeTypeValue)) {
      return TRAIN;
    }else if(TRAM.value().equals(predefinedModeTypeValue)) {
      return TRAM;
    }else if(LIGHTRAIL.value().equals(predefinedModeTypeValue)) {
      return LIGHTRAIL;
    }else if(HEAVY_GOODS_VEHICLE.value().equals(predefinedModeTypeValue)) {
      return HEAVY_GOODS_VEHICLE;
    }else if(LARGE_HEAVY_GOODS_VEHICLE.value().equals(predefinedModeTypeValue)) {
      return LARGE_HEAVY_GOODS_VEHICLE;
    }else {
      return CUSTOM;
    }
  }
}
