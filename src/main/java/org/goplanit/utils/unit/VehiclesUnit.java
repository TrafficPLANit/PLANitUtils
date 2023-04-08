package org.goplanit.utils.unit;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.util.logging.Logger;

/** Unit class for all vehicles related units. Since pcu to vehicle (and vice versa) is not a fixed conversion it is possible
 * to set the conversion factor on this units class.
 * 
 * @author markr
 *
 */
public class VehiclesUnit extends SimpleUnit{
  
  /**
   * Logger to use
   */
  private static final Logger LOGGER = Logger.getLogger(VehiclesUnit.class.getCanonicalName());
    
  /**
   * Default multiplier for conversion from pcus to vehicles
   */
  public static final double DEFAULT_PCU_VEHICLE_MULTIPLIER = 1;
  
  /**
   * conversion factor from pcu to vehicles
   */
  private static double PCU_2_VEHICLE_FACTOR = DEFAULT_PCU_VEHICLE_MULTIPLIER;
  
  /** Constructor with default 1:1 conversion between vehicles and pcus
   * 
   * @param unitType to use
   */
  protected VehiclesUnit(final UnitType unitType) {
    super(unitType);
    if(!unitType.group.equals(UnitGroup.VEHICLES)) {
      LOGGER.warning(String.format("Invalid unit type %s for vehicles based unit",unitType.name));
      this.unitType = UnitType.NONE;
    }    
  }         
          
  /** Convert pcu to... 
   * @param unitType to use
   * @param value to convert
   * @return converted value
   */
  public static double convertPcuTo(final UnitType unitType, final double value){
    if(!unitType.equals(UnitType.VEH)){
      throw new PlanItRunTimeException("Conversion illegal or not supported yet from %s --> %s",UnitType.PCU.name, unitType.name);
    }
    return value * PCU_2_VEHICLE_FACTOR;
  }

  /** Convert vehicles to... 
   * @param unitType to use
   * @param value to convert
   * @return converted value
   */
  public static double convertVehiclesTo(final UnitType unitType, final double value){
    if(!unitType.equals(UnitType.PCU)){
      throw new PlanItRunTimeException("Conversion illegal or not supported yet from %s --> %s",UnitType.VEH.name, unitType.name);
    }    
    return value/PCU_2_VEHICLE_FACTOR;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double convertTo(final Unit to, final double value){
    VehiclesUnit toUnit = (VehiclesUnit)to;
    switch(unitType) {
      case VEH:
        return convertVehiclesTo(toUnit.unitType, value);  
      case PCU:
        return convertPcuTo(toUnit.unitType, value);        
    default:
      throw new PlanItRunTimeException("Unsupported vehicles unit encountered on from Unit");
    }    
  }  
  
  /** Update the pcu to vehicle conversion factor to use as it is not a fixed conversion
   * 
   * @param pcuToVehicleFactor to use
   */
  public static synchronized void updatePcuToVehicleFactor(final double pcuToVehicleFactor) {
    VehiclesUnit.PCU_2_VEHICLE_FACTOR = pcuToVehicleFactor;
  }

}
