package org.goplanit.utils.network.layer;

import org.goplanit.utils.network.layer.physical.*;
import org.goplanit.utils.network.virtual.ConjugateVirtualNetworkLayer;

/**
 * Conjugate Macroscopic physical network layer consisting of conjugate nodes, conjugate links and
 * conjugate macroscopic link segments. Note that conjugate links are undirected turns whereas conjugate
 * edge segments are directed turns
 *
 * @author markr
 */
public interface ConjugateMacroscopicNetworkLayer extends
        UntypedPhysicalLayer<ConjugateNode, ConjugateLink, ConjugateLinkSegment> {

  /**
   * Reset and re-populate the entire conjugate network layer from the current state of the original layer it is the
   * conjugate of.
   * <p>
   *   The conjugate virtual network layer is assumed to be populated already (if present), so the managed ids are not
   *   reset, to enforce contiguous ids across edges and vertices.
   * </p>
   * <p>
   *   XML ids of conjugate entities are derived from their original counterparts and marked with '*' to show this.
   * </p>
   *
   * @param conjugateVirtualNetworkLayer optional to connect to original connectoid edges/segments when present
   */
  public abstract void recreateFromReferenceLayer(ConjugateVirtualNetworkLayer conjugateVirtualNetworkLayer);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateMacroscopicNetworkLayer shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateMacroscopicNetworkLayer deepClone();

  /**
   * Collect the undirected turns, i.e., conjugate links
   * 
   * @return the undirected turns
   */
  @Override
  public abstract ConjugateLinks getLinks();

  /**
   * Collect the turns, i.e. conjugate link segments
   * 
   * @return the turns
   */
  @Override
  public abstract ConjugateLinkSegments getLinkSegments();

  /**
   * Collect the conjugate nodes, i.e., links/link segments in original network
   * 
   * @return the conjugate nodes
   */
  @Override
  public abstract ConjugateNodes getNodes();
  
  /** Reference to original layer this conjugate layer represents
   * 
   * @return original layer
   */
  public abstract MacroscopicNetworkLayer getOriginalLayer();

  /**
   * For each conjugate entity, log the mapping to its original underlying entity where possible
   */
  public abstract void logConjugateToOriginalMapping();
}
