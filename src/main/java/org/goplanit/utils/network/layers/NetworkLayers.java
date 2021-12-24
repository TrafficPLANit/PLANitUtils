package org.goplanit.utils.network.layers;

import java.util.ArrayList;
import java.util.Collection;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.NetworkLayer;

/**
 * interface to manage transport layers.
 * 
 * @author markr
 *
 */
public interface NetworkLayers<T extends NetworkLayer> extends ManagedIdEntities<T> {
  
  /**
   * Find the layer that supports the passed in mode. Since a mode is only allowed to be supported by a single layer, this should yield the correct result. If multiple layers
   * support the same mode for some reason, this method returns the first layer that supports the mode
   *
   * @param mode to find the layer for
   * @return first matching layer
   */
  public abstract T get(final Mode mode);
  
  /**
   * Find the layer based on non-indexed XML id rather than the managed internal id.
   *
   * @param xmlId to find the layer for
   * @return first matching layer
   */
  public abstract T getByXmlId(final String xmlId);  

  /**
   * When there are no layers the instance is considered empty
   * 
   * @return true when no layers exist yet, false otherwise
   */
  public default boolean isNoLayers() {
    return !(size() > 0);
  }

  /**
   * Check if each layer itself is empty
   * 
   * @return true when all empty false otherwise
   */
  public default boolean isEachLayerEmpty() {
    boolean eachLayerEmpty = true;
    for (NetworkLayer layer : this) {
      if (!layer.isEmpty()) {
        eachLayerEmpty = false;
        break;
      }
    }
    return eachLayerEmpty;
  }

  /**
   * Allows you to collect all registered layers of a specific derived transport layer type
   * 
   * @param <U> derived type of type T
   * @return list of layers of desired type, empty list when none exist
   */
  @SuppressWarnings("unchecked")
  public default <U extends NetworkLayer> Collection<U> getLayersOfType() {
    ArrayList<U> layerList = new ArrayList<U>();
    for (T layer : this) {
      try {
        U castLayer = (U) layer;
        layerList.add(castLayer);
      } catch (ClassCastException e) {
        /* wrong type ignore */
      }
    }
    return layerList;
  }
  
  /**
   * clone container
   */
  @Override
  public abstract NetworkLayers<T> clone();

}
