package org.goplanit.utils.graph;

import org.goplanit.utils.id.ManagedIdEntityFactory;

/** A base abstract class for factories that create graph entities on an underlying container 
 * No specification of the container here because it is used as a placeholder to be able to let
 * derived classes implement this interface with as much freedom as possible. Also note that 
 * graph entities are assumed to have managed ids, but we do not require them to be part of a managedIdEntities container
 * within the graph. This way, a graph can reuse edges, vertices, etc. if needed across multiple graphs. In that particular
 * case, care must be taken when modifying the graph since the resued components will be modified on all graphs that use this
 * instance.  
 * 
 * @author markr
 *
 * @param <E> type the factory is creating instances for
 */
public interface GraphEntityFactory<E extends GraphEntity> extends ManagedIdEntityFactory<E>{

  
}
