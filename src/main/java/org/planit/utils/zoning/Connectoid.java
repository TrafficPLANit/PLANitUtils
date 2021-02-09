package org.planit.utils.zoning;

import java.util.Collection;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.id.ExternalIdable;
import org.planit.utils.mode.Mode;

/**
 * the connecting component between zone(s) and the network.
 * Note that connectoids are not vertices, they merely refer to physical entities via derived interfaces 
 * and the physical network.
 * <p>
 * Each combintation of (zone,connectoid) can have additional properties such as length or allowed modes.
 * Not specifying thos will cause the use of defaults (DEFAULT_LENGTH_KM, all modes allowed) 
 * </p>
 * 
 * @author markr
 *
 */
public interface Connectoid extends ExternalIdable, Iterable<Zone> {

  /**
   * Default connectoid length in km
   */
  double DEFAULT_LENGTH_KM = 0.0;
  
  /** default type is set to none */
  public static ConnectoidType DEFAULT_CONNECTOID_TYPE = ConnectoidType.NONE;  
  
  /** Set the name of the connectoid
   * 
   * @param name its name
   */
  void setName(String name);  
  
  /** The name of the connectoid
   * 
   * @return its name
   */
  String getName();
  
  /** Set the type of the connectoid
   * 
   * @param type its type
   */
  void setType(ConnectoidType type);  
  
  /** The type of the connectoid
   * 
   * @return its type
   */
  ConnectoidType getType();  
      
  /**
   * the zones that can be accessed by this connectoid
   * 
   * @return accessible zones
   */
  Collection<Zone> getAccessZones();
  
  /** add an access zone and provide length to this connectoid
   * 
   * @param zone to set length for
   * @param length to traverse between connectoid and zone
   */
  void setLength(Zone zone, double length);
    
  /** add an allowed mode. We assume the zone is already registered as an access zone for this connectoid
   * 
   * @param zone to add allowed mode(s) to
   * @param allowedModes to add
   */
  void addAllowedMode(Zone zone, Mode... allowedModes);   
  
  /** add an access zone with default properties
   * 
   * @param zone to register as accessible
   * @return overwritten zone if any
   */
  Zone addAccessZone(Zone zone);
  
  /** check if zone is registered as access zone
   * @param accessZone to verify
   * @return true when registered, false otherwise
   */
  boolean hasAccessZone(Zone accessZone);
  
  /** first available zone that is accessible based on the first entry the iterator returns
   * 
   * @return first available zone
   */
  Zone getFirstAccessZone();
  
  /** the number of accessible zones registered
   * 
   * @return number of accessible zones
   */
  long getNumberOfAccessZones();
  
  /** length can be used to virtually assign a length to the connectoid/zone combination
   * 
   * @param accessZone to collect length for
   * @return length (null if zone is not registered)
   * @throws PlanItException thrown if error
   */
  Double getLength(Zone accessZone) throws PlanItException;
  
  /** Verify if a mode is allowed access to the zone via this connectoid
   * 
   * @param accessZone to verify
   * @param mode to verify if allowed
   * @return true when allowed, false otherwise
   * @throws PlanItException thrown if provided zone is not valid
   */
  boolean isModeAllowed(Zone accessZone, Mode mode) throws PlanItException;  

}
