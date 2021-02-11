package org.planit.utils.zoning;

import org.planit.utils.id.ExternalIdable;

/**
 * A group of transfer zones, can be used to represent for example train stations.
 * 
 * @author markr
 *
 */
public interface TransferZoneGroup extends ExternalIdable, Iterable<TransferZone> {
  
  /** default type*/
  public static TransferZoneGroupType DEFAULT_TYPE = TransferZoneGroupType.NONE;

  /** Name of the group
   * @return name
   */
  public abstract String getName();
  
  /** set Name of the group
   * @param name to set
   */
  public abstract void setName(String name);
  
  /** type of the group
   * @return type
   */
  public abstract TransferZoneGroupType getType();
  
  /** set type of the group
   * @param type to set
   */
  public abstract void setType(TransferZoneGroupType type);  
  
  /**
   * Add a transferZone
   * 
   * @param transferZone to add
   * @return previousTransferZone under same id (if any)
   */
  public abstract TransferZone addTransferZone(TransferZone transferZone);
  
  /** remove a transfer zone
   * 
   * @param transferZone to remove
   * @return removed transfer zone
   */
  public abstract TransferZone removeTransferZone(TransferZone transferZone);
  
  /** Verify if transfer zone is present
   * 
   * @param transferZone to check
   * @return true when present, false otherwise
   */
  public abstract boolean hasTransferZone(TransferZone transferZone);
  
  /** Number of registered transfer zones
   * @return number of registered transfer zones
   */
  public abstract int size();
  
  /** Collect first transfer zone that would be returned by the iterator
   * @return transfer zone
   */
  public default TransferZone getFirst() {
    return iterator().next();
  }
  
  /** check if no transfer zones are registered
   * 
   * @return true when empty false otherwise
   */
  public default boolean isEmpty() {
    return size()<=0;
  }

}
