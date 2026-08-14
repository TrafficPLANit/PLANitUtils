package org.goplanit.utils.network.layer.modifier;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.modifier.TopologicalModifier;

/**
 * Interface to define all layer wide modifications functionality to be exposed.
 * 
 * @author markr
 *
 */
public interface TopologicalLayerModifier extends TopologicalModifier{
  
  /**
   * Remove any dangling subnetworks below a given size from the network if they exist and subsequently reorder
   * the internal ids if needed
   * 
   * @param belowSize         remove subnetworks below the given size
   * @param aboveSize         remove subnetworks above the given size (typically set to maximum value)
   * @param alwaysKeepLargest when true the largest of the subnetworks is always kept, otherwise not
   * @param recreateManagedIds when true recreate managed id entities so they are contiguous again
   */
  public abstract void removeDanglingSubnetworks(
          final Integer belowSize, Integer aboveSize, boolean alwaysKeepLargest, boolean recreateManagedIds);


  /**
   * remove any dangling subnetworks from the layer if they exist and subsequently reorder the internal ids if needed
   * 
   * @throws PlanItException thrown if error
   */
  public default void removeDanglingSubnetworks() throws PlanItException {
    removeDanglingSubnetworks(Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
  }

}
