package org.planit.utils.unit;

import org.planit.utils.exceptions.PlanItException;

/**
 * Utility class for unit related functionality
 * 
 * @author markr
 *
 */
public class UnitUtils {
  
  private static final double HOUR_2_MINUTE = 60.0;
  
  private static final double MINUTE_2_HOUR = 1.0/HOUR_2_MINUTE; 
  
  private static final double MINUTE_2_SECOND = HOUR_2_MINUTE;
  
  private static final double SECOND_2_MINUTE = MINUTE_2_HOUR;   
  
  private static final double HOUR_2_SECOND = HOUR_2_MINUTE*MINUTE_2_SECOND;
  
  private static final double SECOND_2_HOUR = 1.0/HOUR_2_SECOND;  

  private static final double KM_2_METER = 1000;
  
  private static final double METER_2_KM = 1.0/KM_2_METER;  

   
  public static double convertVehiclesPerSecondTo(Units to, double value) throws PlanItException {
    switch (to) {
    case VEH_HOUR:
      return convert(Units.HOUR, Units.SECOND, value);  //inverse 
    case VEH_MINUTE:
      return convert(Units.MINUTE, Units.SECOND, value);//inverse
    case VEH_SECOND:
      return value;
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.VEH_SECOND, to));
    } 
  }

  public static double convertVehiclesPerMinuteTo(Units to, double value) throws PlanItException {
    switch (to) {
      case VEH_HOUR:
        return convert(Units.HOUR, Units.MINUTE, value);    //inverse
      case VEH_MINUTE:
        return value;
      case VEH_SECOND:
        return convert(Units.SECOND, Units.MINUTE, value);  //inverse       
      default:
        throw new PlanItException(
            String.format("conversion illegal or not supported yet from %s --> %s",Units.VEH_MINUTE, to));
      }        
  }
  
  public static double convertVehiclesPerHourTo(Units to, double value) throws PlanItException {
    switch (to) {
    case VEH_HOUR:
      return value;
    case VEH_MINUTE:
      return convert(Units.MINUTE, Units.HOUR, value);  //inverse
    case VEH_SECOND:
      return convert(Units.SECOND, Units.HOUR, value);  //inverse        
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.VEH_HOUR, to));
    }    
  }  
  
  public static double convertSecondTo(Units to, double value) throws PlanItException {
    switch (to) {    
    case HOUR:
      return value * SECOND_2_HOUR;
    case MINUTE:
      return value * SECOND_2_MINUTE;
    case SECOND:
      return value;
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.SECOND, to));
    }   
  }  

  public static double convertMinuteTo(Units to, double value) throws PlanItException {
    switch (to) {
    case MINUTE:
      return value;
    case HOUR:
      return value * MINUTE_2_HOUR;
    case SECOND:
      return value * MINUTE_2_SECOND;        
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.MINUTE, to));
    }   
  }
  
  public static double convertHourTo(Units to, double value) throws PlanItException {
    switch (to) {
    case HOUR:
      return value;    
    case MINUTE:
      return value * HOUR_2_MINUTE;
    case SECOND:
      return value * HOUR_2_SECOND;        
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.HOUR, to));
    }  
  }    
  
  public static double convertMeterTo(Units to, double value) throws PlanItException {
    switch (to) {
    case METER:
      return value;
    case KM:
      return value * METER_2_KM;       
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.METER, to));
    }  
  }

  public static double convertKmTo(Units to, double value) throws PlanItException {
    switch (to) {
    case METER:
      return value * KM_2_METER;       
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.KM, to));
    }  
  }  
  
  public static double convertKmSecTo(Units to, double value) throws PlanItException {
    switch (to) {
    case KM_HOUR:
      return convert(Units.HOUR, Units.SECOND, value);    // inverse
    case KM_MINUTE:
      return convert(Units.MINUTE, Units.SECOND, value);  //inverse
    case METER_HOUR:
      return convertMeterSecondTo(Units.METER_HOUR,convert(Units.KM, Units.METER, value));     
    case METER_MINUTE:
      return convertMeterSecondTo(Units.METER_MINUTE,convert(Units.KM, Units.METER, value));  
    case METER_SECOND:
      return convert(Units.KM, Units.METER, value);        
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.KM_SECOND, to));
    } 
  }   

  public static double convertKmMinTo(Units to, double value) throws PlanItException {
    switch (to) {
    case KM_HOUR:
      return convert(Units.HOUR, Units.MINUTE, value); // inverse
    case KM_SECOND:
      return convert(Units.SECOND, Units.MINUTE, value);// inverse
    case METER_HOUR:
      return convertMeterMinuteTo(Units.METER_HOUR,convert(Units.KM, Units.METER, value));      
    case METER_MINUTE:
      return convert(Units.KM, Units.METER, value);
    case METER_SECOND:
      return convertMeterMinuteTo(Units.METER_SECOND,convert(Units.KM, Units.METER, value));          
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.KM_MINUTE, to));
    } 
  }
  
  public static double convertKmHourTo(Units to, double value) throws PlanItException {
    switch (to) {
    case KM_MINUTE:
      return convert(Units.MINUTE, Units.HOUR, value); // inverse
    case KM_SECOND:
      return convert(Units.SECOND, Units.HOUR, value); // inverse
    case METER_HOUR:
      return convert(Units.KM, Units.METER, value);   
    case METER_MINUTE:
      return convertMeterHourTo(Units.METER_MINUTE,convert(Units.KM, Units.METER, value));
    case METER_SECOND:
      return convertMeterHourTo(Units.METER_SECOND,convert(Units.KM, Units.METER, value));         
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.KM_HOUR, to));
    } 
  } 
  
  public static double convertMeterHourTo(Units to, double value) throws PlanItException {
    switch (to) {
    case KM_HOUR:
      return convert(Units.METER, Units.KM, value);   
    case KM_MINUTE:
      return convertKmHourTo(Units.KM_MINUTE,convert(Units.METER, Units.KM, value));
    case KM_SECOND:
      return convertKmHourTo(Units.KM_SECOND,convert(Units.METER, Units.KM, value));      
    case METER_MINUTE:
      return convert(Units.MINUTE, Units.HOUR, value); // inverse 
    case METER_SECOND:
      return convert(Units.SECOND, Units.HOUR, value); // inverse        
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.METER_HOUR, to));
    } 
  }

  public static double convertMeterMinuteTo(Units to, double value) throws PlanItException {
    switch (to) {
    case KM_HOUR:
      return convertKmMinTo(Units.KM_HOUR,convert(Units.METER, Units.KM, value));    
    case KM_MINUTE:
      return convert(Units.METER, Units.KM, value); 
    case KM_SECOND:
      return convertKmMinTo(Units.KM_SECOND,convert(Units.METER, Units.KM, value));      
    case METER_HOUR:
      return convert(Units.HOUR, Units.MINUTE, value);  // inverse 
    case METER_SECOND:
      return convert(Units.SECOND, Units.MINUTE, value);// inverse          
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.METER_MINUTE, to));
    } 
  }

  public static double convertMeterSecondTo(Units to, double value) throws PlanItException {
    switch (to) {
    case KM_HOUR:
      return convertKmSecTo(Units.KM_HOUR,convert(Units.METER, Units.KM, value));     
    case KM_MINUTE:
      return convertKmSecTo(Units.KM_MINUTE,convert(Units.METER, Units.KM, value));
    case KM_SECOND:
      return convert(Units.METER, Units.KM, value);    
    case METER_HOUR:
      return convert(Units.HOUR, Units.SECOND, value);  // inverse
    case METER_MINUTE:
      return convert(Units.MINUTE, Units.SECOND, value);// inverse        
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.METER_SECOND, to));
    } 
  }  
  
  public static double convertVehiclesPerMeterTo(Units to, double value) throws PlanItException {
    switch (to) {
    case VEH_KM:
      return convert(Units.KM, Units.METER, value); // inverse      
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.METER, to));
    } 
  }

  public static double convertVehiclesPerKmTo(Units to, double value) throws PlanItException {
    switch (to) {
    case VEH_METER:
      return convert(Units.METER, Units.KM, value); // inverse       
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",Units.VEH_KM, to));
    } 
  }  

  /**
   * convert between indicated units
   * 
   * @param from unit
   * @param to unit
   * @param value to convert
   * @return converted value
   * @throws PlanItException thrown if error
   */
  public static double convert(Units from, Units to, double value) throws PlanItException {
    switch (from) {
    case KM_HOUR:
      return convertKmHourTo(to, value);
    case KM_MINUTE:
      return convertKmMinTo(to, value);      
    case KM_SECOND:
      return convertKmSecTo(to, value);
    case METER_SECOND:
      return convertMeterSecondTo(to, value);
    case METER_MINUTE:
      return convertMeterMinuteTo(to, value);
    case METER_HOUR:
      return convertMeterHourTo(to, value);      
    case KM:
      return convertKmTo(to, value);
    case METER:
      return convertMeterTo(to, value);      
    case MINUTE:
      return convertMinuteTo(to, value);  
    case SECOND:
      return convertSecondTo(to, value);        
    case HOUR:
      return convertHourTo(to, value);
    case VEH_HOUR:
      return convertVehiclesPerHourTo(to, value);
    case VEH_MINUTE:
      return convertVehiclesPerMinuteTo(to, value);
    case VEH_SECOND:
      return convertVehiclesPerSecondTo(to, value);      
    case VEH_KM:
      return convertVehiclesPerKmTo(to, value);      
    case VEH_METER:
      return convertVehiclesPerMeterTo(to, value);        
    default:
      throw new PlanItException(String.format("conversion illegal or not supported yet from %s --> %s",from, to));
    }
  }
  

}
