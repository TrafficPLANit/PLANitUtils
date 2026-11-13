package org.goplanit.utils.network.virtual;

import org.goplanit.utils.network.virtual.physical.conjugate.*;

/**
 * Conjugate virtual network layer consisting of conjugate nodes, conjugate connectoid edges and
 * conjugate connectoid segments
 *
 * @author markr
 */
public interface ConjugateVirtualNetworkLayer extends
        UntypedVirtualLayer<ConjugateConnectoidNode, ConjugateConnectoidLink, ConjugateConnectoidSegment> {

  /**
   * Update the layer by syncing it to the current non-conjugate reference layer
   *
   * @param resetManagedIds when true reset the id token such that generated managed ids will start from zero again
   */
  public abstract void recreateFromReferenceLayer(boolean resetManagedIds);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateVirtualNetworkLayer shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateVirtualNetworkLayer deepClone();

  /**
   * Access to connectoid edges
   *
   * @return connectoid edges
   */
  @Override
  public abstract ConjugateConnectoidLinks getConnectoidLinks();

  /**
   * Access to connectoid segments
   *
   * @return connectoid segments
   */
  @Override
  public abstract ConjugateConnectoidSegments getConnectoidSegments();

  /**
   * Access conjugate connectoid nodes
   *
   * @return conjugate connectoid nodes
   */
  @Override
  public abstract ConjugateConnectoidNodes getVertices();

  /**
   * Access to original reference layer this conjugate version is based on
   *
   * @return referrence layer
   */
  public abstract VirtualNetworkLayer getReferenceLayer();

  /**
   * For each conjugate entity, log the mapping to its original underlying entity where possible
   */
  public abstract void logConjugateToOriginalMapping();
}
