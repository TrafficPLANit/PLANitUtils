package org.planit.utils.zoning;

import java.util.Collection;
import java.util.Set;

import org.planit.utils.id.ExternalIdable;

/**
 * A group of transfer zones, can be used to represent for example train stations.
 * 
 * @author markr
 *
 */
public interface TransferZoneGroup extends ExternalIdable, Iterable<TransferZone> {
  
  /** Name of the group
   * @return name
   */
  public abstract String getName();
  
  /** set Name of the group
   * @param name to set
   */
  public abstract void setName(String name);
    
  /**
   * Add a transferZone and also register this group on this transfer zone at the same time
   * 
   * @param transferZone to add
   * @return previousTransferZone under same id (if any)
   */
  public abstract TransferZone addTransferZone(TransferZone transferZone);
  
  /** remove a transfer zone from the group, and also remove the reference on the transfer zone at the same time
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
  
  /** create a view of the registered transfer zones. Any changes are
   * reflected in the group as well
   * 
   * @return registered transfer zones
   */
  public abstract Collection<TransferZone> getTransferZones();  
  
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

  /** Verify if the group has a name already
   * @return true when non-null, non-blank name is present, false otherwise
   */
  public default boolean hasName() {
    return (getName()!=null && !getName().isBlank());
  }
  

}
