package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory interface for creating conjugate vertex instances
 * 
 * @author markr
 *
 */
public interface ConjugateDirectedVertexFactory extends GraphEntityFactory<ConjugateDirectedVertex> {

  /** Create a new conjugate directed vertex (without registering on this class)
   * 
   * @param originalEdge this directed vertex represents in the conjugate graph
   * @return created conjugate vertex
   */
  public abstract ConjugateDirectedVertex createNew(final DirectedEdge originalEdge);
  
  /**
   * Create and register new conjugate directed vertex
   *
   * @param originalEdge this directed vertex represents in the conjugate graph
   * @return new conjugate directed vertex created and registered
   */
  public abstract ConjugateDirectedVertex registerNew(final DirectedEdge originalEdge); 
  
}
