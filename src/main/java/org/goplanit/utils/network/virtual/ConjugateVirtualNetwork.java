package org.goplanit.utils.network.virtual;

import java.util.Map;

import org.goplanit.utils.zoning.Centroid;

/**
 * Model-free conjugate virtual network interface which is part of the zoning and holds all the
 * virtual infrastructure connecting the zones to the physical road network.
 * 
 * @author markr
 */
public interface ConjugateVirtualNetwork extends UntypedVirtualNetwork<ConjugateVirtualNetworkLayer> {

  /** Access to underlying original virtual network this conjugate represents
   *
   * @return original virtual network
   */
  public abstract VirtualNetwork getOriginalVirtualNetwork();

  /**
   * Access to the single virtual layer
   * @return layer
   */
  @Override
  public abstract ConjugateVirtualNetworkLayer getLayer();

  /**
   * For each conjugate entity, log the mapping to its original underlying entity where possible
   */
  public default void logConjugateToOriginalMapping(){
    getLayer().logConjugateToOriginalMapping();
  }
}