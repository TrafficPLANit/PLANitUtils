package org.planit.utils.graph.modifier.event;

import org.planit.utils.event.EventProducer;
import org.planit.utils.graph.modifier.BreakEdgeListener;
import org.planit.utils.graph.modifier.GraphModifier;
import org.planit.utils.graph.modifier.RemoveSubGraphListener;

/** Interface dedicated to the {@link GraphModifier} event related exposed methods. Specifies method signatures for registering
 * the appropriate listeners and event types that are supported on a graph modifier.
 *  
 * @author markr
 *
 */
public interface GraphModifierEventProducer extends EventProducer{

  /**
   * Register listeners for events fired by the graph modifier
   * TODO: replaces the dedicated listeners below and should hide the djutils event usage underneath as that is too loosely defined
   * to be considered pretty enough to use
   * 
   *  @param listener to register
   *  @param eventType to register listener for
   */
  public abstract void addListener(GraphModifierListener listener, GraphModifierEventType eventType);
  
  /**
   * register a listener that will be invoked whenever an entity of a subgraph is removed via {@link removeSubGraph}
   * 
   * @param listener to register
   */
  public abstract void registerRemoveSubGraphListener(RemoveSubGraphListener listener);
  
  /**
   * unregister a listener that is currently registered
   * 
   * @param listener to unregister
   */  
  public abstract void unregisterRemoveSubGraphListener(RemoveSubGraphListener listener);  
  
  /**
   * register a listener that will be invoked whenever a link is broken via {@link breakEdgesAt}
   * 
   * @param listener to register
   */  
  public void unregisterBreakEdgeListener(BreakEdgeListener listener);

  /**
   * unregister a listener that is currently registered
   * 
   * @param listener to unregister
   */   
  public void registerBreakEdgeListener(BreakEdgeListener listener);  
}
