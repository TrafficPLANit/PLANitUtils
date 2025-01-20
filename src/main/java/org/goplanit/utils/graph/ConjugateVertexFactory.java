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
   * Create and register new conjugate vertex, populate the XMLId by either copying its internal id or using the
   * underlying original edge's XMLId. Optionally post-fix either.
   *
   *
   * @param originalEdge this node is the conjugate of
   * @param deriveFromOriginalEdge when true use original edg XML id, otherwise use internal id of conjugates
   * @param xmlIdPostFix to apply
   * @return new node created
   */
  public abstract ConjugateVertex registerNew(
          final Edge originalEdge, boolean deriveFromOriginalEdge, String xmlIdPostFix);
  
}
