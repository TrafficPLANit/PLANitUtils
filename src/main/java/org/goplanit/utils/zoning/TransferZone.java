package org.goplanit.utils.zoning;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A zone where transfers between different network layers may occur. Trips do not
 * terminate at transfer zones
 * 
 * @author markr
 *
 */
public interface TransferZone extends Zone {
  
  /** the class to use for the id generation */
  public static final Class<TransferZone> TRANSFER_ZONE_ID_CLASS = TransferZone.class;  
  
  /**
   * default transfer zone type
   */
  public static TransferZoneType DEFAULT_TYPE = TransferZoneType.NONE;  

  /** In addition to a zone id across all zones of any derived type, each transfer zone also has a unique id
   * across the transfer zones specifically
   * 
   * @return transfer zone specific id
   */
  public abstract long getTransferZoneId();

  /** Unmodifiable list of registered platform names for this transfer zone (if any).
   *
   * @return transfer zone platform names if any were registered
   */
  public abstract List<String> getTransferZonePlatformNames();

  /** Add platform name for this transfer zone
   *
   * @param platformName to add
   * @return true if newly added, false otherwise
   */
  public abstract boolean addTransferZonePlatformName(String platformName);

  /** Remove a platform name if present
   *
   * @param platformName to remove
   * @return true when removed, false otherwise
   */
  public abstract boolean removeTransferZonePlatformName(String platformName);

  /**
   * Verify if any platform names are present
   *
   * @return true when present, false otherwise
   */
  public default boolean hasPlatformNames(){
    return getTransferZonePlatformNames() != null && !getTransferZonePlatformNames().isEmpty();
  }

  /** Add all platform names provided
   *
   * @param platformNames to add
   * @return true if all are newly added, false otherwise
   */
  public default void addTransferZonePlatformNames(String[] platformNames){
    addTransferZonePlatformNames(Arrays.asList(platformNames));
  }

  /** Add all platform names provided
   *
   * @param platformNames to add
   * @return true if all are newly added, false otherwise
   */
  public default void addTransferZonePlatformNames(Collection<String> platformNames){
    platformNames.forEach( s -> addTransferZonePlatformName(s));
  }

  /** set the type of this transfer zone
   * 
   * @param transferZoneType to set
   */  
  public abstract void setType(TransferZoneType transferZoneType);
  
  /** collect the type of this transfer zone
   * 
   * @return transfer zone type
   */
  public abstract TransferZoneType getTransferZoneType();
  
  /** Verify if a transfer zone is part of one or more transfer zone groups
   * 
   * @return true when in transfer zone group, false otherwise
   */
  public abstract boolean hasTransferZoneGroup();
  
  /** Verify if transfer zone is part of passed in transfer zone group
   * 
   * @param transferZoneGroup to check
   * @return true when part of group, false otherwise
   */
  public abstract boolean isInTransferZoneGroup(TransferZoneGroup transferZoneGroup);
    
  /** register this transfer zone group as a group this transfer zone is part of
   * 
   * @param transferZoneGroup to register as being part of
   */
  public abstract void addToTransferZoneGroup(TransferZoneGroup transferZoneGroup);

  /** remove from transfer zone group provided and also remove ths transfer zone from group provided
   * @param transferZoneGroup to remove ourselves as member from
   * @return true when removal was successful, false otherwise
   */
  public abstract boolean removeFromTransferZoneGroup(TransferZoneGroup transferZoneGroup);

  /** All transfer zone groups the transfer zone resides in (unmodifiable)
   * @return transfer zone groups of the transfer zone
   */
  public abstract Set<TransferZoneGroup> getTransferZoneGroups();

  /**
   * remove this transfer zone from all groups it is registered on (and also update the group references
   */
  public abstract void removeFromAllTransferZoneGroups();
  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferZone clone();
  
  /**
   * {@inheritDoc}
   */
  public default Class<TransferZone> getTransferZoneIdClass() {
    return TRANSFER_ZONE_ID_CLASS;
  }

}
