package org.goplanit.utils.network.virtual.physical;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.pcu.PcuCapacitated;

/**
 * Connectoid segment represents a directional virtual segment connecting a centroid and a physical
 * node. It has a unique id across all connectoid segments
 * 
 * @author markr
 *
 */
public interface ConnectoidSegment extends LinkSegment, PcuCapacitated {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidLink getParent();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegment shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidSegment deepClone();

  /**
   * Connectoid segments are not capacity restricted by default, but can be used in conjunction with a capacitated network.
   * Therefore, they by default return {@code Double.MAX_VALUE} as their capacity. We have no limitation on capacity to ensure that
   * demand does not get "trapped" in zones, but can at least be loaded onto connectoid segments so it is present in the network during loading 
   * 
   */
  @Override
  public default double getCapacityOrDefaultPcuH() {
    return Double.MAX_VALUE;
  }
  
  /**
   * see {@link #getCapacityOrDefaultPcuH()} 
   * 
   */  
  @Override
  public default double getCapacityOrDefaultPcuHLane() {
    return Double.MAX_VALUE;
  }  

}
