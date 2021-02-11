package org.planit.utils.zoning;

import org.planit.utils.network.physical.LinkSegment;
import org.planit.utils.network.physical.Node;

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
  static boolean DEFAULT_NODE_ACCESS_DOWNSTREAM = true; 

  /** the edge segment that provides access
   * 
   * @return access edge segment
   */
  LinkSegment getAccessLinkSegment();
  
  /** set if the node access is downstream or not
   * 
   * @param nodeAccessDownstream true to set it downstream, false otherwise
   */
  void setNodeAccessDownstream(boolean nodeAccessDownstream);  
  
  /** determine if the node access is downstream or not
   * 
   * @return true when downstream, false otherwise, i.e., upstream
   */
  boolean isNodeAccessDownstream();
  
  
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
  
  
}
