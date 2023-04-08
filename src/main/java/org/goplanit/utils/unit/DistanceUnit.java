package org.goplanit.utils.unit;

import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.exceptions.PlanItRunTimeException;

/** Unit class for all distance related units
 * 
 * @author markr
 *
 */
public class DistanceUnit extends SimpleUnit{
  
  /**
   * Logger to use
   */
  private static final Logger LOGGER = Logger.getLogger(DistanceUnit.class.getCanonicalName());  
  
  /** Constructor
   * 
   * @param unitType to use
   */
  protected DistanceUnit(UnitType unitType) {
    super(unitType);
    if(!unitType.group.equals(UnitGroup.DISTANCE)) {
      LOGGER.warning(String.format("Invalid unit type %s for distance based unit",unitType.name));
      unitType = UnitType.NONE;
    }
  }  
    
  /**
   * km to meter conversion multiplier
   */
  private static final double KM_2_METER = 1000;

  /**
   * meter to kilometre conversion multiplier
   */  
  private static final double METER_2_KM = 1.0/KM_2_METER;  
  
  /** convert meter to... 
   * @param to to unit
   * @param value to convert 
   * @return converted value
   */
  public static double convertMeterTo(UnitType to, double value) {
    if(to==null) {
      throw new PlanItRunTimeException("To unit null, conversion infeasible");
    }
    switch (to) {
    case METER:
      return value;
    case KM:
      return value * METER_2_KM;       
    default:
      throw new PlanItRunTimeException("Conversion illegal or not supported yet from %s --> %s",UnitType.METER, to);
    }  
  }

  /** convert km to... 
   * @param to to unit
   * @param value to convert 
   * @return converted value 
   * @throws PlanItException thrown if error
   */  
  public static double convertKmTo(UnitType to, double value){
    if(to==null) {
      throw new PlanItRunTimeException("to unit null, conversion infeasible");
    }
    switch (to) {
    case METER:
      return value * KM_2_METER;       
    default:
      throw new PlanItRunTimeException("Conversion illegal or not supported yet from %s --> %s",UnitType.KM, to);
    }  
  }   
 
  /**
   * {@inheritDoc}
   */
  @Override
  public double convertTo(Unit to, double value){
    DistanceUnit toUnit = (DistanceUnit)to;
    switch(unitType) {
      case METER:
        return convertMeterTo(toUnit.unitType, value);  
      case KM:
        return convertKmTo(toUnit.unitType, value);        
    default:
      throw new PlanItRunTimeException("Unsupported distance unit encountered on from Unit");
    }    
  }  

}
