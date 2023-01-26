package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.directed.ConjugateEdgeSegment;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.pcu.PcuCapacitated;

/**
 * Conjugate connectoid segment represents a directional virtual segment connecting two conjugate connectoid nodes, however, one is a dummy and one of the original underlying link segments is null.
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidSegment extends ConjugateEdgeSegment, PcuCapacitated {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidSegment clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidSegment deepClone();

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
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidEdge getParent();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default ConjugateConnectoidNode getUpstreamVertex() {
    return (ConjugateConnectoidNode) ConjugateEdgeSegment.super.getUpstreamVertex();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default ConjugateConnectoidNode getDownstreamVertex() {
    return (ConjugateConnectoidNode) ConjugateEdgeSegment.super.getDownstreamVertex();
  }
  
  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public default Pair<? extends LinkSegment,? extends LinkSegment> getOriginalAdjcentEdgeSegments(){
    return (Pair<? extends LinkSegment, ? extends LinkSegment>) ConjugateEdgeSegment.super.getOriginalAdjcentEdgeSegments();
  }  

}
