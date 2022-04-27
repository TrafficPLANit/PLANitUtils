package org.goplanit.utils.network.virtual;

import java.util.Collection;
import java.util.Map;

import org.goplanit.utils.network.layer.physical.ConjugateNode;
import org.goplanit.utils.zoning.Centroid;

/**
 * Model free virtual network interface which is part of the zoning and holds all the virtual infrastructure connecting the zones to the physical road network.
 * 
 * @author markr
 */
public interface ConjugateVirtualNetwork {

  /**
   * Access to conjugate nodes
   * 
   * @return conjugate connectoid nodes
   */
  public abstract ConjugateConnectoidNodes getConjugateConnectoidNodes();

  /**
   * free up memory by clearing contents for garbage collection
   */
  public abstract void clear();
  
  /**
   * identical {@link #clear()} only now all underlying managed ids are also reset
   */
  public abstract void reset(); 
  
  /** Access to underlying original virtual network this conjugate represents
   * @return
   */
  public abstract VirtualNetwork getOriginalVirtualNetwork();

  /** Extract mapping from original network centroid's to conjugate node(s)
   * @return mapping from centroid to its conjugate node(s) original edge(segment)(s)
   */
  public abstract Map<Centroid, Collection<ConjugateConnectoidNode>> createCentroidToConjugateNodeMapping();


}