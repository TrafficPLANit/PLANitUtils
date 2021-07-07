package org.planit.utils.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.planit.utils.id.ManagedIdEntities;

/** Container class for any graph entities and a factory to create them
 * 
 * @author markr
 *
 * @param <E> type of graph entity
 */
public interface GraphEntities<E extends GraphEntity> extends ManagedIdEntities<E> {

  /** Factory to create instance of graph entity (for this container class)
   * 
   * @return entity factory
   */
  @Override
  public abstract GraphEntityFactory<E> getFactory();
      
  /**
   * Force clone implementation
   * 
   * @return clone of entities
   */
  @Override  
  public abstract GraphEntities<E> clone();  
      
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
  public default Collection<E> getByExternalId(String externalId) {
    ArrayList<E> matches = new ArrayList<E>(1);  
    for(E entity : this) {
      if(entity.getExternalId().equals(externalId)) {
        matches.add(entity);      }
    }
    return matches;
  }    

}
