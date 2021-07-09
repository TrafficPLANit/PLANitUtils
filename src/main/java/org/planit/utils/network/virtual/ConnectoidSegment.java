package org.planit.utils.network.virtual;

import org.planit.utils.graph.EdgeSegment;

/**
 * Connectoid segment represents a directional virtual segment connecting a centroid and a physical
 * node. It has a unique id across all connectoid segments
 * 
 * @author markr
 *
 */
public interface ConnectoidSegment extends EdgeSegment {

  /** additional id class for generating connectoid segment ids */
  public static Class<ConnectoidSegment> CONNECTOID_SEGMENT_ID_CLASS = ConnectoidSegment.class;
  
  /**
   * Return class used to generate unique connectoid edge segment ids via the id generator
   * 
   * @return class type
   */
  public default Class<ConnectoidSegment> getConnectoidSegmentIdClass(){
    return CONNECTOID_SEGMENT_ID_CLASS;
  }  

  /**
   * Collect the unqiue connectoid segment id
   * 
   * @return connectoid segment id
   */
  long getConnectoidSegmentId();

}
