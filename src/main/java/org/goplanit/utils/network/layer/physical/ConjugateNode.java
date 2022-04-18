package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;

/**
 * Conjugate Node is the conjugate of a normal link
 * 
 * @author markr
 *
 */
public interface ConjugateNode extends ConjugateDirectedVertex {
  
  /** id class for generating ids */
  public static final Class<ConjugateNode> CONJUGATE_NODE_ID_CLASS = ConjugateNode.class; 
  
  /**
   * Return class used to generate unique conjugate node ids via the id generator if required
   * 
   * @return class type
   */
  public default Class<? extends ConjugateNode> getConjugateNodeIdClass(){
    return CONJUGATE_NODE_ID_CLASS;
  }
       
}
