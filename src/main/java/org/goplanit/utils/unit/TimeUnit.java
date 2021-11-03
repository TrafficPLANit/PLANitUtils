package org.goplanit.utils.unit;

import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;

/** Unit class for all time related units.
 * 
 * @author markr
 *
 */
public class TimeUnit extends SimpleUnit{
  
  /**
   * Logger to use
   */
  private static final Logger LOGGER = Logger.getLogger(TimeUnit.class.getCanonicalName());  
  
  /** Constructor
   * 
   * @param unitType to use
   */
  protected TimeUnit(UnitType unitType) {
    super(unitType);
    if(!unitType.group.equals(UnitGroup.TIME)) {
      LOGGER.warning(String.format("Invalid unit type %s for time based unit",unitType.name));
      unitType = UnitType.NONE;
    }
  }  
    
  /**
   * hour to minute conversion multiplier
   */
  public static final double HOUR_2_MINUTE = 60.0;

  /**
   * minute to hour conversion multiplier
   */
  public static final double MINUTE_2_HOUR = 1.0/HOUR_2_MINUTE; 
  
  /**
   * minute to second conversion multiplier
   */
  public static final double MINUTE_2_SECOND = HOUR_2_MINUTE;
  
  /**
   * second to minute conversion multiplier
   */
  public static final double SECOND_2_MINUTE = MINUTE_2_HOUR;   
  
  /**
   * hour to second conversion multiplier
   */
  public static final double HOUR_2_SECOND = HOUR_2_MINUTE*MINUTE_2_SECOND;
  
  /**
   * second to hour conversion multiplier
   */
  public static final double SECOND_2_HOUR = 1.0/HOUR_2_SECOND;  
  
  /** convert second to... 
   * @param to to unit
   * @param value to convert 
   * @return converted value 
   * @throws PlanItException thrown if error
   */
  public static double convertSecondTo(UnitType to, double value) throws PlanItException {
    if(to==null) {
      throw new PlanItException(String.format("to unit null, conversion infeasible"));
    }
    switch (to) {    
    case HOUR:
      return value * SECOND_2_HOUR;
    case MINUTE:
      return value * SECOND_2_MINUTE;
    case SECOND:
      return value;
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",UnitType.SECOND, to));
    }   
  }  

  /** convert minute to... 
   * @param to to unit
   * @param value to convert 
   * @return converted value 
   * @throws PlanItException thrown if error
   */
  public static double convertMinuteTo(UnitType to, double value) throws PlanItException {
    if(to==null) {
      throw new PlanItException(String.format("to unit null, conversion infeasible"));
    }
    switch (to) {
    case MINUTE:
      return value;
    case HOUR:
      return value * MINUTE_2_HOUR;
    case SECOND:
      return value * MINUTE_2_SECOND;        
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",UnitType.MINUTE, to));
    }   
  }
  
  /** convert hour to... 
   * @param to to unit
   * @param value to convert 
   * @return converted value 
   * @throws PlanItException thrown if error
   */
  public static double convertHourTo(UnitType to, double value) throws PlanItException {
    if(to==null) {
      throw new PlanItException(String.format("to unit null, conversion infeasible"));
    }
    switch (to) {
    case HOUR:
      return value;    
    case MINUTE:
      return value * HOUR_2_MINUTE;
    case SECOND:
      return value * HOUR_2_SECOND;        
    default:
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",UnitType.HOUR, to));
    }  
  }      
  
  /**
   * {@inheritDoc}
   */
  @Override
  public double convertTo(Unit to, double value) throws PlanItException {
    TimeUnit toUnit = (TimeUnit)to;
    switch(unitType) {
      case MINUTE:
        return convertMinuteTo(toUnit.unitType, value);  
      case SECOND:
        return convertSecondTo(toUnit.unitType, value);        
      case HOUR:
        return convertHourTo(toUnit.unitType, value);
    default:
      throw new PlanItException("Unsupported time unit encountered on from Unit");
    }    
  }  

}
