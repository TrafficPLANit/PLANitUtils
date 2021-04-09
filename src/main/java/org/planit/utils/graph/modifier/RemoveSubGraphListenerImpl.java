package org.planit.utils.graph.modifier;

import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Vertex;
import org.planit.utils.id.IdAbleImpl;
import org.planit.utils.id.IdGenerator;
import org.planit.utils.id.IdGroupingToken;

/**
 * A base class implementation for remove sub graph listeners
 * 
 * @author markr
 *
 */
public class RemoveSubGraphListenerImpl<V extends Vertex, E extends Edge> extends IdAbleImpl implements RemoveSubGraphListener<V,E>{
  
  /**
   * Every instance implementing this interface should generate its id using this method to ensure that we have a unique id across all break edge listeners
   * in case they are stored in a comparable based container
   * 
   * @return id for a break edge listener class instance
   */
  protected static long generateId() {
    return IdGenerator.generateId(IdGroupingToken.collectGlobalToken(),RemoveSubGraphListenerImpl.class);
  }  
  
  /**
   * Constructor
   */
  public RemoveSubGraphListenerImpl() {
    super(generateId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRemoveSubGraphEdge(E edge) {
    /* empty implementation */
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void onRemoveSubGraphVertex(V vertex) {
    /* empty implementation */
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void onCompletion() {
    /* empty implementation */
  }
}
