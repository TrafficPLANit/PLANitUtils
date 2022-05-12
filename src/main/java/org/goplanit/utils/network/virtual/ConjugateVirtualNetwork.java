package org.goplanit.utils.network.virtual;

import java.util.Map;

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
   * Access to conjugate edges
   * 
   * @return conjugate connectoid edges
   */
  public abstract ConjugateConnectoidEdges getConjugateConnectoidEdges();
  
  /**
   * Access to conjugate edge segments
   * 
   * @return conjugate connectoid edge segments
   */
  public abstract ConjugateConnectoidSegments getConjugateConnectoidEdgeSegments();    

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

  /** Extract mapping from original network centroid's to conjugate (dummy) node
   * @return mapping from centroid to its conjugate dummy noderepresenting the non-existing entry into the movement from centroid to any connected edge
   */
  public abstract Map<Centroid, ConjugateConnectoidNode> createCentroidToConjugateNodeMapping();


}