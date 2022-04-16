package org.goplanit.utils.graph;

/** Factory interface for creating conjugate vertex instances
 * 
 * @author markr
 *
 */
public interface ConjugateVertexFactory extends GraphEntityFactory<ConjugateVertex> {

  /** Create a new conjugate vertex (without registering on this class)
   * 
   * @param originalEdge this conjugate will represent 
   * @return created conjugate vertex
   */
  public abstract ConjugateVertex createNew(final Edge originalEdge);
  
  /**
   * Create and register new conjugate vertex
   *
   * @param originalEdge this conjugate will represent
   * @return new conjugate vertex created
   */
  public abstract ConjugateVertex registerNew(final Edge originalEdge); 
  
}
