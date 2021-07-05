package org.planit.utils.graph;

import org.planit.utils.id.ContainerisedManagedIdEntityFactory;

/** A base abstract class for factories that create graph entities on an underlying container 
 * No specification of the container here because it is used as a placeholder to be able to let
 * derived classes implement this interface with as much freedome as possible 
 * 
 * @author markr
 *
 * @param <E> type the factory is creating instances for
 */
public interface GraphEntityFactory<E extends GraphEntity> extends ContainerisedManagedIdEntityFactory<E>{

  
}
