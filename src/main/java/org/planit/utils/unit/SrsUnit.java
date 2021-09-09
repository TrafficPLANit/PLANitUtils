package org.planit.utils.unit;

import java.util.logging.Logger;

import org.planit.utils.exceptions.PlanItException;

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
   * @param unitType to use
   */
  protected SrsUnit(UnitType unitType) {
    super(unitType);
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
    throw new PlanItException("SRS units cannot yet be converted");   
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canConvertTo(Unit other) {
    return false;
  }  

}
