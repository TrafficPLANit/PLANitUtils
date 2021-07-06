package org.planit.utils.zoning;

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

  /** all transfer zone groups the transfer zone resides in (unmodifiable)
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
