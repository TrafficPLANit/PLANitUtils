package org.planit.utils.zoning;

import org.planit.utils.id.ManagedIdEntities;

/**
 * Container to register and manager connectoids that define the points of access for
 * zones regarding infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface Connectoids<T extends Connectoid> extends ManagedIdEntities<T>{
  
}
