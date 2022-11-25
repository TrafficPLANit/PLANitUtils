package org.goplanit.utils.network.layer;

import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.macroscopic.*;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.layer.physical.Nodes;
import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;
import org.goplanit.utils.network.virtual.ConjugateVirtualNetwork;

/**
 * Macroscopic physical network layer consisting of nodes, links and macroscopic link segments 
 *
 * @author markr
 */
public interface MacroscopicNetworkLayer extends UntypedPhysicalLayer<Node, MacroscopicLink, MacroscopicLinkSegment> {
  
  /**
   * Collect the links
   *
   * @return the links
   */
  @Override
  public abstract MacroscopicLinks getLinks();

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

  /**
   * Convenience method to determine the maximum speed limit (km/h) across the layer for a given mode
   *
   * @param mode to use
   * @return found maximum applied speed limit
   */
  public default double findMaximumSpeedLimitKmH(Mode mode){
    return getLinkSegmentTypes().findMaximumSpeedLimit(mode);
  }

  /**
   * Convenience method to determine the maximum pace (h/km) across the layer for a given mode
   *
   * @param mode to use
   * @return found maximum applied speed limit
   */
  public default double findMaximumPaceHKm(Mode mode){
    return 1.0/findMaximumSpeedLimitKmH(mode);
  }
  
  /** Create a conjugate version of this layer, also known as the edge-to-vertex-dual representation, where all edges become vertices and all two adjacent edges (turns) become the edges on the conjugate version.
   *  When the provided id token is the same as an existing layer, vertex,edge,edge segmentids will continue numbering which might not be ideal. It is reocmmended to have a separate idToken for all conjugate layers such
   *  that all conjugate vertices, edges, edge segments are numbered uniquely within the context.   
   * 
   * @param idToken to use for generating ids within the layer
   * @param conjugateVirtualNetwork to use for encountered connections to zones, when null connectoid edges/edge segments are ignored 
   * @return conjugate version of this layer
   */
  public abstract ConjugateMacroscopicNetworkLayer createConjugate(final IdGroupingToken idToken, final ConjugateVirtualNetwork conjugateVirtualNetwork);

}
