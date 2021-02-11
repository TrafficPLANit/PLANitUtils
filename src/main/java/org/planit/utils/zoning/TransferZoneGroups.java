package org.planit.utils.zoning;

/**
 * A Container for transfer zone groups which also acts as a factory for creating new transfer zone groups
 * 
 * @author markr
 *
 */
public interface TransferZoneGroups extends Iterable<TransferZoneGroup>{
  
  /**
   * register a transferZoneGroup
   * 
   * @param transferZoneGroup to add
   * @return previousTransferZoneGroup under same id (if any)
   */
  public abstract TransferZoneGroup register(TransferZoneGroup transferZoneGroup);
  
  /**
   * create and register a new transferZoneGroup
   * 
   * @return newly created transfer zone group
   */
  public abstract TransferZoneGroup registerNew(); 
  
  /**
   * create a new transferZoneGroup without registering it yet
   * 
   * @return newly created transfer zone group
   */
  public abstract TransferZoneGroup createNew();   
  
  /** remove a transfer zone group
   * 
   * @param transferZoneGroup to remove
   * @return removed transfer zone group
   */
  public abstract TransferZoneGroup remove(TransferZoneGroup transferZone);
  
  /** Verify if transfer zone group is present
   * 
   * @param transferZoneGroup to check
   * @return true when present, false otherwise
   */
  public abstract boolean hasTransferZoneGroup(TransferZoneGroup transferZoneGroup);
  
  /** get a transfer zone group by id
   * 
   * @param transferZoneGroupId to collect group for
   * @return the group, null if not present
   */
  public abstract TransferZoneGroup get(long transferZoneGroupId);  
  
  /** Number of registered transfer zone groups
   * @return number of registered transfer zone groups
   */
  public abstract int size();
  
  /** Collect first transfer zone that would be returned by the iterator
   * @return transfer zone
   */
  public default TransferZoneGroup getFirst() {
    return iterator().next();
  }
  
  /** check if no transfer zone groups are registered
   * 
   * @return true when empty false otherwise
   */
  public default boolean isEmpty() {
    return size()<=0;
  }

}
