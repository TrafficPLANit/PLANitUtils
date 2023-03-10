package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.zoning.Centroid;

/** Factory interface for creating centroid vertex instances
 * 
 * @author markr
 *
 */
public interface CentroidVertexFactory extends GraphEntityFactory<CentroidVertex> {


  /**
   * Create and register new entity
   *
   * @return new node created
   */
  public abstract CentroidVertex registerNew();

  /** Create a new CentroidVertex (without registering on this class)
   *
   * @param parent centroid of the vertex
   * @return created vertex
   */
  public abstract CentroidVertex createNew(Centroid parent);

  /**
   * Create and register new entity
   *
   * @return new centroid vertex created
   */
  public abstract CentroidVertex registerNew(Centroid parent);

}
