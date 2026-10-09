package org.goplanit.utils.network.layers;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.ConjugateMacroscopicNetworkLayer;
import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;

/** Factory interface for creating macroscopic conjugate network layers
 * 
 * @author markr
 *
 */
public interface ConjugateMacroscopicNetworkLayerFactory
        extends ManagedIdEntityFactory<ConjugateMacroscopicNetworkLayer> {
 
  /** Create a new conjugate macroscopic conjugate network layer instance based on a given reference macroscopic layer
   *
   * @param referenceLayer to set as original
   * @return created instance
   */
  public abstract ConjugateMacroscopicNetworkLayer registerNew(MacroscopicNetworkLayer referenceLayer);

}
