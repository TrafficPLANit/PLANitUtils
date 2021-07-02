package org.planit.utils.graph;

import java.util.ArrayList;
import java.util.Collection;
import org.planit.utils.wrapper.LongMapWrapper;

/** Container class for any graph entities and a factory to create them
 * 
 * @author markr
 *
 * @param <E> type of graph entity
 */
public interface GraphEntities<E extends GraphEntity> extends LongMapWrapper<E>, Cloneable {

  /** Factory to create instance of graph entity (for this container class)
   * 
   * @return entity factory
   */
  public abstract GraphEntityFactory<E> getFactory();
    
  /**
   * recreate the ids for all registered entities
   * 
   * @param entities to recreate ids for
   */
  public abstract void recreateIds();  
  
  /**
   * Force clone implementation
   * 
   * @return clone of entities
   */
  public abstract GraphEntities<E> clone();  
    
  /** Verify if present
   * 
   * @param id to verify
   * @return true when present false otherwise
   */
  public default boolean contains(long id) {
    return get(id) != null;
  }
  
  /**
   * Return an entity by its XML id
   * 
   * Note: not an efficient implementation since it loops over all entities in linear time to identify the correct one, 
   * preferably use {@link #get(Object)} instead whenever possible.
   * 
   * @param xmlId the XML id of the entity
   * @return the specified entity instance
   */
  public default E getByXmlId(String xmlId) {
    return findFirst(entity -> xmlId.equals(entity.getXmlId()));
  }  
  
  /** Collect all entities based on a matching external id. Entities are not indexed by external id so this is
   *  a very inefficient linear search through all registered entities.
   *  
   * @param externalId to match
   * @return found matching links
   */  
  public default Collection<? extends E> getByExternalId(String externalId) {
    ArrayList<E> matches = new ArrayList<E>(1);  
    for(E entity : this) {
      if(entity.getExternalId().equals(externalId)) {
        matches.add(entity);      }
    }
    return matches;
  }    

}
