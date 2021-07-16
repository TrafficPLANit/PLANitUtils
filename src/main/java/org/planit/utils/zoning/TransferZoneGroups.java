package org.planit.utils.zoning;

import org.planit.utils.id.ManagedIdEntities;
import org.planit.utils.zoning.modifier.event.ZoningModifierListener;

/**
 * A Container for transfer zone groups which also acts as a factory for creating new transfer zone groups
 * 
 * @author markr
 *
 */
public interface TransferZoneGroups extends ManagedIdEntities<TransferZoneGroup>, ZoningModifierListener{
      
  /** Collect first transfer zone that would be returned by the iterator
   * @return transfer zone
   */
  public default TransferZoneGroup getFirst() {
    return iterator().next();
  }
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract TransferZoneGroupFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferZoneGroups clone();   
  
}
