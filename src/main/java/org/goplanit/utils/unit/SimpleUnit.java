package org.goplanit.utils.unit;

/** Simple unit that comprises a single unit
 * 
 * @author markr
 *
 */
public abstract class SimpleUnit implements Unit {
  
  /**
   * type of the unit 
   */
  protected UnitType unitType;

  /** Constructor
   * @param unitType to use
   */
  protected SimpleUnit (UnitType unitType) {
    super();
    this.unitType = unitType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCombinedUnit() {
    return false;
  }

  /**
   * The type of this unit, such as {@link UnitType#HOUR}
   *
   * @return the type
   */
  public UnitType getUnitType() {
    return unitType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canConvertTo(Unit other){
    if(other.isCombinedUnit()) {
      return false;
    }
    return unitType.isCompatible(((SimpleUnit)other).unitType);
  }
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public String toString() {
    return unitType.getName();
  }

  /**
   * {@inheritDoc}
   */  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (o == null) {
        return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }      
    SimpleUnit unit = (SimpleUnit) o;
    return unit.unitType.equals(this.unitType);     
  }  
}

