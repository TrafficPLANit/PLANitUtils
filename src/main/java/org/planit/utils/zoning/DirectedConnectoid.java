package org.planit.utils.zoning;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.network.layer.physical.LinkSegment;
import org.planit.utils.network.layer.physical.Node;

/**
 * A directed connectoid is referring to an edge segment in a network (layer) which is directed for access
 * hence, the connectoid also being directed
 * 
 * TODO: we can potentially get rid of UndirectedConnectoid and let DirectedConnectoid extend from base since they
 * now both rely on accessNodes
 * 
 * @author markr
 *
 */
public interface DirectedConnectoid extends Connectoid{
  
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
   * @param exitEdgeSegment to use
   */
  public abstract void replaceAccessLinkSegment(EdgeSegment exitEdgeSegment);  
  
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
    
  
  /** Based on the edge segment and the location (upstream/downstream) of the access point, collect
   * the access node
   * 
   * @return accessNode to use
   */
  public default Node getAccessNode() {
    if(isNodeAccessDownstream()) {
      return (Node)getAccessLinkSegment().getDownstreamVertex();
    }else {
      return (Node)getAccessLinkSegment().getUpstreamVertex();
    }
  }


  /** Verify if an access link segment is present
   * @return true when present, false otherwise
   */
  public default boolean hasAccessLinkSegment() {
    return getAccessLinkSegment()!=null;
  }
  
  
}
