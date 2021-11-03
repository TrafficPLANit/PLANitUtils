package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierListener;

/**
 * Container to register and manager connectoids that define the points of access for
 * zones regarding infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface Connectoids<T extends Connectoid> extends ManagedIdEntities<T>, ZoningModifierListener{
  
}
