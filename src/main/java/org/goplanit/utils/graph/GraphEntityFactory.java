package org.goplanit.utils.graph;

import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Base of factories that create graph entities on an underlying container. The container is not specified here, as
 * this is a placeholder giving derived classes as much freedom as possible to implement this interface. Graph entities
 * are assumed to have managed ids, but need not be part of a managedIdEntities container within the graph, so a graph
 * can reuse edges, vertices, etc. across multiple graphs. Then take care when modifying the graph, as the reused
 * components change on all graphs that use them.
 * 
 * @author markr
 *
 * @param <E> type the factory is creating instances for
 */
public interface GraphEntityFactory<E extends GraphEntity> extends ManagedIdEntityFactory<E>{

  
}
