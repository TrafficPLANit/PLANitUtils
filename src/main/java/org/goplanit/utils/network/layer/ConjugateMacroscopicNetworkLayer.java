package org.goplanit.utils.network.layer;

import org.goplanit.utils.network.layer.physical.ConjugateLink;
import org.goplanit.utils.network.layer.physical.ConjugateLinkSegment;
import org.goplanit.utils.network.layer.physical.ConjugateLinkSegments;
import org.goplanit.utils.network.layer.physical.ConjugateLinks;
import org.goplanit.utils.network.layer.physical.ConjugateNode;
import org.goplanit.utils.network.layer.physical.ConjugateNodes;
import org.goplanit.utils.network.layer.service.ServiceNode;

/**
 * Conjugate Macroscopic physical network layer consisting of conjugate nodes, conjugate links and conjugate macroscopic link segments. Note that conjugate links are undirected turns whereas conjugate edge segments are directed turns  
 *
 * @author markr
 */
public interface ConjugateMacroscopicNetworkLayer extends UntypedDirectedGraphLayer<ConjugateNode, ConjugateLink, ConjugateLinkSegment>  {

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
  public abstract ConjugateLinks getConjugateLinks();

  /**
   * Collect the turns, i.e. conjugate link segments
   * 
   * @return the turns
   */
  public abstract ConjugateLinkSegments getConjugateLinkSegments();

  /**
   * Collect the conjugate nodes, i.e., links/link segments in original network
   * 
   * @return the conjugate nodes
   */
  public abstract ConjugateNodes getConjugateNodes(); 
  
  /** Reference to original layer this conjugate layer represents
   * 
   * @return original layer
   */
  public abstract MacroscopicNetworkLayer getOriginalLayer();
      

}
