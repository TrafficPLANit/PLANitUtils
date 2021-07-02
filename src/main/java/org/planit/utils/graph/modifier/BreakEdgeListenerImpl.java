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
public class BreakEdgeListenerImpl extends IdAbleImpl implements BreakEdgeListener {
  
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
   * Copy constructor
   * 
   * @param breakEdgeListenerImpl to copy
   */
  public BreakEdgeListenerImpl(BreakEdgeListenerImpl breakEdgeListenerImpl) {
    super(breakEdgeListenerImpl);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onBreakEdge(Vertex vertex, Edge aToBreak, Edge breakToB) {
    /* empty implementation */
  }

  /**
   * {@inheritDoc}
   */  
  @Override
  public BreakEdgeListenerImpl clone() {
    return new BreakEdgeListenerImpl(this);
  }

}
