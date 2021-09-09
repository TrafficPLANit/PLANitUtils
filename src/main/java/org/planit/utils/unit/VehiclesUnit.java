package org.planit.utils.unit;

import java.util.logging.Logger;

import org.planit.utils.exceptions.PlanItException;

/** Unit class for all time related units.
 * 
 * @author markr
 *
 */
public class VehiclesUnit extends SimpleUnit{
  
  /**
   * Logger to use
   */
  private static final Logger LOGGER = Logger.getLogger(VehiclesUnit.class.getCanonicalName());  
  
  /** Constructor
   * 
   * @param unitType to use
   */
  protected VehiclesUnit(UnitType unitType) {
    super(unitType);
    if(!unitType.group.equals(UnitGroup.VEHICLES)) {
      LOGGER.warning(String.format("Invalid unit type %s for vehicles based unit",unitType.name));
      unitType = UnitType.NONE;
    }
  }  
        
  /**
   * {@inheritDoc}
   */
  @Override
  public double convertTo(Unit to, double value) throws PlanItException {
    VehiclesUnit toUnit = (VehiclesUnit)to;
    switch(unitType) {
      case VEH:
        return convertVehiclesTo(toUnit.unitType, value);  
      case PCU:
        return convertPcuTo(toUnit.unitType, value);        
    default:
      throw new PlanItException("Unsupported vehicles unit encountered on from Unit");
    }    
  }  

}
