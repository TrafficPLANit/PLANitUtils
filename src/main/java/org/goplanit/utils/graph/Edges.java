package org.goplanit.utils.graph;

import org.goplanit.utils.network.layer.physical.Links;
import org.locationtech.jts.index.quadtree.Quadtree;

import java.util.function.BiConsumer;

/**
 * Container and factory class for edges in a graph, also to be used to create and register edges of any
 * (derived) type
 * 
 * @author markr
 */
public interface Edges extends GraphEntities<Edge>{       
  
  /**
   * Collect the edge factory to use for creating instances
   * 
   * @return edgeFactory to create edges for this container
   */
  @Override
  public abstract EdgeFactory getFactory();
  
  /**
   * shallow clone edges
   */
  @Override
  public abstract Edges shallowClone();

  /**
   * deep clone edges
   */
  @Override
  public abstract Edges deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Edges deepCloneWithMapping(BiConsumer<Edge, Edge> mapper);

}
