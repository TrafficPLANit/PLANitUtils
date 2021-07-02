package org.planit.utils.graph;

import org.planit.utils.id.IdGroupingToken;

/**
 * Build network elements based on chosen network view. Implementations are registered on the network class which uses it to construct network elements
 * 
 * @author markr
 *
 */
public interface GraphBuilder<V extends Vertex, E extends Edge> {

  /**
   * Create a new vertex instance
   * 
   * @return created vertex
   */
  public V createVertex();

  /**
   * Each builder needs a group if token to allow all underlying factory methods to generated ids uniquely tied to the group the entities belong to
   * 
   * @param groupId, contiguous id generation within this group for instances created with the factory methods
   */
  public abstract void setIdGroupingToken(IdGroupingToken groupId);

  /**
   * Collect the id grouping token used by this builder
   * 
   * @return idGroupingToken the id grouping token used by this builder
   */
  public abstract IdGroupingToken getIdGroupingToken();

  /**
   * recreate the ids for all passed in vertices
   * 
   * @param vertices to recreate ids for
   */
  public abstract void recreateIds(Vertices<? extends V> vertices);

}
