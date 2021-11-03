package org.goplanit.utils.graph.modifier;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.id.IdAbleImpl;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;

/**
 * A base class implementation for remove sub graph listeners
 * 
 * @author markr
 *
 */
public class RemoveSubGraphListenerImpl extends IdAbleImpl implements RemoveSubGraphListener{
  
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
   * Copy constructor
   * 
   * @param removeSubGraphListenerImpl to copy
   */
  public RemoveSubGraphListenerImpl(RemoveSubGraphListenerImpl removeSubGraphListenerImpl) {
    super(removeSubGraphListenerImpl);
  }  

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRemoveSubGraphEdge(Edge edge) {
    /* empty implementation */
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void onRemoveSubGraphVertex(Vertex vertex) {
    /* empty implementation */
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void onCompletion() {
    /* empty implementation */
  }
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public RemoveSubGraphListenerImpl clone() {
    return new RemoveSubGraphListenerImpl(this);
  }  
}
