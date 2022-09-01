package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierListener;

import java.util.Map;
import java.util.Set;

/**
 * Container to register and manager connectoids that define the points of access for
 * zones regarding infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface Connectoids<T extends Connectoid> extends ManagedIdEntities<T>, ZoningModifierListener{

  /**
   * Lay an index across all connectoids based on their access zones, e.g., a reversed index essentially
   *
   * @return newly created map of each access zone's its connectoids
   */
  public abstract Map<Zone, Set<T>> createIndexByAccessZone();
}
