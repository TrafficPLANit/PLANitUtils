package org.goplanit.utils.network.virtual;

/**
 * Model free virtual network interface which is part of the zoning and holds all the virtual infrastructure connecting the zones to the physical road network.
 * 
 * @author markr
 */
public interface VirtualNetwork {

  /**
   * Access to connectoid segments
   * 
   * @return connectoidSegments
   */
  public abstract ConnectoidSegments getConnectoidSegments();

  /**
   * Access to connectoid edges
   * 
   * @return connectoidEdges
   */
  public abstract ConnectoidEdges getConnectoidEdges();

  /**
   * free up memory by clearing contents for garbage collection
   */
  public abstract void clear();
  
  /**
   * identical {@link #clear()} only now all underlying managed ids are also reset
   */
  public abstract void reset();   

  /**
   * Create a conjugate version of this virtual network, also known as the edge-to-vertex-dual representation, where all connectoidedges/edge segments become (dangling) conjugate
   * vertices. conjugate vertex ids are based on the original edge ids so do not require any token based id generation.
   * <p>
   * It is recommended to first create the conjugate of this virtual network BEFORE creating conjugates of network layers. The latter takes a conjugate zoning as input such that it
   * can connect the conjugate virtual nodes to the conjugate network layer where appropriate, otherwise these connections are ignored
   * 
   * @return conjugate version of this virtual network's edges/edgesgments
   */
  public abstract ConjugateVirtualNetwork createConjugate();

}