package org.planit.utils.unit;

import org.planit.utils.exceptions.PlanItException;

/** Simple unit that comprises a single unit
 * 
 * @author markr
 *
 */
public class NoneUnit extends SimpleUnit {
      
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCombinedUnit() {
    return false;
  }

  /** Constructor 
   * @param unitType to use
   */
  protected NoneUnit () {
    super(UnitType.NONE);
  }
        
  /**
   * {@inheritDoc}
   */  
  @Override
  public double convertTo(Unit to, double value) throws PlanItException {
    if(!to.isCombinedUnit() && ((NoneUnit)to).unitType.equals(UnitType.NONE)) {
      return value;
    }else {
      throw new PlanItException(
          String.format("conversion illegal or not supported yet from %s --> %s",UnitType.NONE, to));
    }
  }
  
}

