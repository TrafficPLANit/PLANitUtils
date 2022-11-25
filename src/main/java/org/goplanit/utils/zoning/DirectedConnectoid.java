package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * A directed connectoid is referring to an edge segment in a network (layer) which is directed for access
 * hence, the connectoid also being directed
 *
 * @author markr
 *
 */
public interface DirectedConnectoid extends Connectoid{
  
  /** the class to use for the additional directed connectoid id generation */
  public static final Class<DirectedConnectoid> DIRECTED_CONNECTOID_ID_CLASS = DirectedConnectoid.class;
  
  /** default node access is set to the downstream end of a link segment */
  public static boolean DEFAULT_NODE_ACCESS_DOWNSTREAM = true; 
  
  /** Collect the directed connectoid id
   * 
   * @return directed connectoid id
   */
  public abstract long getDirectedConnectoidId();  

  /** The edge segment that provides access
   * 
   * @return access edge segment
   */
  public abstract LinkSegment getAccessLinkSegment();
  
  /**
   * Replace the access link segment for this connectoid
   * 
   * @param accessEdgeSegment to use
   */
  public abstract void replaceAccessLinkSegment(LinkSegment accessEdgeSegment);
  
  /** set if the node access is downstream or not
   * 
   * @param nodeAccessDownstream true to set it downstream, false otherwise
   */
  public abstract void setNodeAccessDownstream(boolean nodeAccessDownstream);  
  
  /** determine if the node access is downstream or not
   * 
   * @return true when downstream, false otherwise, i.e., upstream
   */
  public abstract boolean isNodeAccessDownstream();
    
  
  /** the class for directed connectoid id generation
   * 
   * @return class to use
   */
  public default Class<DirectedConnectoid> getDirectedConnectoidIdClass(){
    return DIRECTED_CONNECTOID_ID_CLASS;
  }

  /** Based on the edge segment and the location (upstream/downstream) of the access point, collect
   * the access node
   * 
   * @return accessNode to use
   */
  public default Node getAccessNode() {
    if(isNodeAccessDownstream()) {
      return getAccessLinkSegment().getDownstreamVertex();
    }else {
      return getAccessLinkSegment().getUpstreamVertex();
    }
  }


  /** Verify if an access link segment is present
   * @return true when present, false otherwise
   */
  public default boolean hasAccessLinkSegment() {
    return getAccessLinkSegment()!=null;
  }
  
  
}
