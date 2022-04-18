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
   
       
}
