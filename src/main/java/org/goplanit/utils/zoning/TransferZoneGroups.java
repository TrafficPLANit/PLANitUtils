package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierListener;

/**
 * A Container for transfer zone groups which also acts as a factory for creating new transfer zone groups
 * 
 * @author markr
 *
 */
public interface TransferZoneGroups extends ManagedIdEntities<TransferZoneGroup>, ZoningModifierListener{
      
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract TransferZoneGroupFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferZoneGroups shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TransferZoneGroups deepClone();
  
}
