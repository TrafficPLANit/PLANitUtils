package org.goplanit.utils.unit;

import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;

/** Unit class for all spatial reference system related units
 * 
 * @author markr
 *
 */
public class SrsUnit extends SimpleUnit{
  
  /**
   * Logger to use
   */
  private static final Logger LOGGER = Logger.getLogger(SrsUnit.class.getCanonicalName());  
  
  /** Constructor
   * 
   */
  protected SrsUnit() {
    super(UnitType.SRS);
    if(!unitType.group.equals(UnitGroup.SRS)) {
      LOGGER.warning(String.format("Invalid unit type %s for SRS based unit",unitType.name));
      unitType = UnitType.NONE;
    }
  }  
       
  /**
   * {@inheritDoc}
   */
  @Override
  public double convertTo(Unit to, double value) throws PlanItException {
    if(!to.isCombinedUnit() && ((SrsUnit)to).unitType.equals(UnitType.SRS)) {
      return value;
    }else {
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",UnitType.SRS, to));
    }   
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canConvertTo(Unit other) {
    return false;
  }  

}
