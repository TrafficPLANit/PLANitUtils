package org.planit.utils.unit;

import org.planit.utils.exceptions.PlanItException;

/** Lightweight Unit interface to define units and allow for conversions between them
 * 
 * @author markr
 *
 */
public interface Unit {
  
  // single unit
  
  public static final Unit NONE = new NoneUnit();  
  
  /**
   * predefined HOUR TimeUnit
   */
  public static final TimeUnit HOUR = new TimeUnit(UnitType.HOUR);

  /**
   * predefined MINUTE TimeUnit
   */
  public static final TimeUnit MINUTE = new TimeUnit(UnitType.MINUTE);
  
  /**
   * predefined SECOND TimeUnit
   */
  public static final TimeUnit SECOND = new TimeUnit(UnitType.SECOND);
  
  /**
   * predefined KM DistanceUnit
   */
  public static final DistanceUnit KM = new DistanceUnit(UnitType.KM);
  
  /**
   * predefined METER DistanceUnit
   */
  public static final DistanceUnit METER = new DistanceUnit(UnitType.METER);

  /**
   * predefined PCU VehiclesUnit
   */
  public static final VehiclesUnit PCU = new VehiclesUnit(UnitType.PCU);
  
  /**
   * predefined VEHICLE VehiclesUnit
   */
  public static final VehiclesUnit VEH = new VehiclesUnit(UnitType.VEH);

  /**
   * predefined SRS SRSUnit
   */
  public static final Unit SRS = new SrsUnit();
  
  // Combined units
  
  /**
   * predefined KM/HOUR Unit
   */
  public static final Unit KM_HOUR = new GroupUnit(KM).per(HOUR);
  
  /**
   * predefined METER/SECOND Unit
   */
  public static final Unit METER_SECOND = new GroupUnit(METER).per(SECOND);  

  /**
   * predefined PCU/HOUR Unit
   */
  public static final Unit PCU_HOUR = new GroupUnit(PCU).per(HOUR);
  
  /**
   * predefined PCU/KM Unit
   */
  public static final Unit PCU_KM = new GroupUnit(PCU).per(KM);;  
  
  /**
   * predefined VEH/HOUR Unit
   */
  public static final Unit VEH_HOUR = new GroupUnit(VEH).per(HOUR);

  /**
   * predefined VEH/KM Unit
   */
  public static final Object VEH_KM = new GroupUnit(VEH).per(KM);
   
  
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
