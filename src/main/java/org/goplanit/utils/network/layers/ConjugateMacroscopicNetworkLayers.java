package org.goplanit.utils.network.layers;

import org.goplanit.utils.network.layer.ConjugateMacroscopicNetworkLayer;
import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;

/**
 * interface to manage macroscopic conjugate physical network layers, i.e., layers that contain a topologically
 * meaningful representation in the form of conjugate nodes and conjugate links
 * 
 * @author markr
 *
 */
public interface ConjugateMacroscopicNetworkLayers
        extends UntypedPhysicalNetworkLayers<ConjugateMacroscopicNetworkLayer> {
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateMacroscopicNetworkLayerFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateMacroscopicNetworkLayers shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateMacroscopicNetworkLayers deepClone();

  /**
   * Access to reference layers these conjugate layers are based on
   *
   * @return layers container
   */
  public abstract MacroscopicNetworkLayers getReferenceMacroscopicLayers();
}
