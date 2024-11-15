package org.goplanit.utils.network.layer;

import org.goplanit.utils.network.layer.physical.*;

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
      

}
