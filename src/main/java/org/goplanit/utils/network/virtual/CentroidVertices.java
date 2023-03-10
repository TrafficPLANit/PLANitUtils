package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.VertexFactory;

import java.util.function.BiConsumer;

/**
 * Container class for centroid vertices and creating instances within this container via factory.
 * 
 * @author markr
 */
public interface CentroidVertices extends GraphEntities<CentroidVertex> {
      
  /**
   * Collect the vertex factory to use for creating instances
   * 
   * @return vertexFactory to create edges for this container
   */
  @Override
  public abstract CentroidVertexFactory getFactory();
  
  /**
   * shallow clone vertices
   */
  @Override
  public abstract CentroidVertices shallowClone();

  /**
   * deep clone vertices
   */
  @Override
  public abstract CentroidVertices deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract CentroidVertices deepCloneWithMapping(BiConsumer<CentroidVertex, CentroidVertex> mapper);
}
