package org.planit.utils.unit;

import org.planit.utils.exceptions.PlanItException;

/** Lightweight Unit interface to define units and allow for conversions between them
 * 
 * @author markr
 *
 */
public interface Unit {
  
  public static final TimeUnit HOUR = new TimeUnit(UnitType.HOUR);
  
  public static final TimeUnit MINUTE = new TimeUnit(UnitType.MINUTE);
  
  public static final TimeUnit SECOND = new TimeUnit(UnitType.SECOND);
  
  public static final DistanceUnit KM = new DistanceUnit(UnitType.KM);
  
  public static final DistanceUnit METER = new DistanceUnit(UnitType.METER);
  
  public static final Unit KM_HOUR = new GroupUnit(KM).per(HOUR);
    
  
  /** Is the unit a single or combined unit
   * 
   * @return false when single, true when combined 
   */
  public abstract boolean isCombinedUnit();
    
  /** Convert one unit to the other for a given value
   * 
   * @param to to unit 
   * @param value value to convert
   * @return converted value
   * 
   * @throws PlanItException thrown if not possible to perform conversion
   */
  public abstract double convertTo(Unit to, double value) throws PlanItException;
  
  /** Verify if unit can be converted to the desired unit
   * 
   * @param to to unit
   * @return true when compatible, false otherwise
   */
  public abstract boolean canConvertTo(Unit to);
}
