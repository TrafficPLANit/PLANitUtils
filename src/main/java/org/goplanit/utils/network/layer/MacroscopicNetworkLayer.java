package org.goplanit.utils.network.layer;

import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentTypes;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegments;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.Links;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.layer.physical.Nodes;
import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Macroscopic physical network layer consisting of nodes, links and macroscopic link segments 
 *
 * @author markr
 */
public interface MacroscopicNetworkLayer extends UntypedPhysicalLayer<Node, Link, MacroscopicLinkSegment> {
  
  /**
   * Collect the links
   * 
   * @return the links
   */
  @Override
  public abstract Links getLinks();

  /**
   * Collect the link segments
   * 
   * @return the linkSegments
   */
  @Override
  public abstract MacroscopicLinkSegments getLinkSegments();

  /**
   * Collect the nodes
   * 
   * @return the nodes
   */
  @Override
  public abstract Nodes getNodes();  
    
  /**
   * Provide access to registered macroscopic link segment types used across all macroscopic link segments
   * 
   * @return link segment types container class
   */
  public abstract MacroscopicLinkSegmentTypes getLinkSegmentTypes();
  
  /** Create a conjugate version of this layer, also known as the edge-to-vertex-dual representation, where all edges become vertices and all two adjacent edges (turns) become the edges on the conjugate version.
   *  When the provided id token is the same as an existing layer, vertex,edge,edge segmentids will continue numbering which might not be ideal. It is reocmmended to have a separate idToken for all conjugate layers such
   *  that all conjugate vertices, edges, edge segments are numbered uniquely within the context.   
   * 
   * @param idToken to use for generating ids within the layer
   * @return conjugate version of this layer
   */
  public abstract ConjugateMacroscopicNetworkLayer createConjugate(final IdGroupingToken idToken);

}
