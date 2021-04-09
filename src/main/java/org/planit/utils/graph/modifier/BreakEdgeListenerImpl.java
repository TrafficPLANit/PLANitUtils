package org.planit.utils.graph.modifier;

import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Vertex;
import org.planit.utils.id.IdAbleImpl;
import org.planit.utils.id.IdGenerator;
import org.planit.utils.id.IdGroupingToken;

/**
 * A base class for BreakEdgeListener 
 * 
 * @author markr
 *
 */
public class BreakEdgeListenerImpl<V extends Vertex, E extends Edge> extends IdAbleImpl implements BreakEdgeListener<V,E> {
  
  /**
   * Every instance implementing this interface should generate its id using this method to ensure that we have a unique id across all break edge listeners
   * in case they are stored in a comparable based container
   * 
   * @return id for a break edge listener class instance
   */
  protected static long generateId() {
    return IdGenerator.generateId(IdGroupingToken.collectGlobalToken(),BreakEdgeSegmentListener.class);
  }
  
  /**
   * Constructor
   */
  public BreakEdgeListenerImpl() {
    super(generateId());
  }
  

  /**
   * {@inheritDoc}
   */
  @Override
  public void onBreakEdge(V vertex, E aToBreak, E breakToB) {
    /* empty implementation */
  }

}
