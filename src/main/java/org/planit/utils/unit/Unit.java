package org.planit.utils.unit;

import java.util.Arrays;

import org.planit.utils.exceptions.PlanItException;

/** Lightweight Unit interface to define units and allow for conversions between them
 * 
 * @author markr
 *
 */
public interface Unit {
  
  // single unit
  
  public static final Unit NONE = Unit.of(UnitType.NONE);  
  
  /**
   * predefined HOUR TimeUnit
   */
  public static final TimeUnit HOUR = (TimeUnit) Unit.of(UnitType.HOUR);

  /**
   * predefined MINUTE TimeUnit
   */
  public static final TimeUnit MINUTE = (TimeUnit) Unit.of(UnitType.MINUTE);
  
  /**
   * predefined SECOND TimeUnit
   */
  public static final TimeUnit SECOND =(TimeUnit) Unit.of(UnitType.SECOND);
  
  /**
   * predefined KM DistanceUnit
   */
  public static final DistanceUnit KM = (DistanceUnit) Unit.of(UnitType.KM);
  
  /**
   * predefined METER DistanceUnit
   */
  public static final DistanceUnit METER = (DistanceUnit) Unit.of(UnitType.METER);

  /**
   * predefined PCU VehiclesUnit
   */
  public static final VehiclesUnit PCU = (VehiclesUnit) Unit.of(UnitType.PCU);
  
  /**
   * predefined VEHICLE VehiclesUnit
   */
  public static final VehiclesUnit VEH = (VehiclesUnit) Unit.of(UnitType.VEH);

  /**
   * predefined SRS SRSUnit
   */
  public static final Unit SRS = Unit.of(UnitType.SRS);
  
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
  public static final Unit VEH_KM = new GroupUnit(VEH).per(KM);
   
  
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
  
  /** Create unit based on given type
   * 
   * @param type to use
   * @return unit created
   */
  public static SimpleUnit of(UnitType type) {
    switch (type.getGroup()) {
    case DISTANCE:
      return new DistanceUnit(type);
    case TIME:
      return new TimeUnit(type);
    case VEHICLES:
      return new VehiclesUnit(type);
    case NONE:
      return new NoneUnit();      
    case SRS:
      return new SrsUnit();      
    default:
      return null;
    }
  }
  
  /** Create unit based on given types
   * 
   * @param numeratorUnits to use
   * @param denominatorUnits to use 
   * @return combined unit created
   */
  public static GroupUnit of(UnitType[] numeratorUnits, UnitType[] denominatorUnits) {
    SimpleUnit[] numeratorUnitTypes = Arrays.stream(numeratorUnits).map( unit -> Unit.of(unit)).toArray(SimpleUnit[]::new);
    SimpleUnit[] denominatorUnitTypes = Arrays.stream(denominatorUnits).map( unit -> Unit.of(unit)).toArray(SimpleUnit[]::new);
    return new GroupUnit(numeratorUnitTypes).per(denominatorUnitTypes);    
  }
    
}
