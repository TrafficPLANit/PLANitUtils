package org.goplanit.utils.graph;

public class EdgeUtils {

  /** Collect shared vertex between two edges if any exists
   * @param edge1 to check
   * @param edge2 to check
   * @return shared vertex, null if none is found
   */
  public static Vertex getSharedVertex(Edge edge1, Edge edge2) {
    if(edge1.getVertexA() == edge2.getVertexA() || edge1.getVertexA() == edge2.getVertexB()) {
      return edge1.getVertexA();
    }else if(edge1.getVertexB() == edge2.getVertexA() || edge1.getVertexB() == edge2.getVertexB()) {
      return edge1.getVertexB();
    }
    return null;
  }
}
