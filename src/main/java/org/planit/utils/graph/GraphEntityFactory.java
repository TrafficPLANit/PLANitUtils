package org.planit.utils.graph;

import org.planit.utils.id.ExternalIdable;
import org.planit.utils.id.IdGroupingToken;

/** A base abstract class for factories that create graph entities. 
 * No methods because it is used as a placeholder to be able to let
 * derived classes override the return type of the super class's access to this factory 
 * for a more specific factory implementation 
 * 
 * @author markr
 *
 * @param <E> type the factory is creating instances for
 */
public interface GraphEntityFactory<E extends ExternalIdable> {

  /**
   * Each factory needs a token to allow all underlying factory methods to generated ids uniquely tied to the group token the entities 
   * belong to
   * 
   * @param tokenId, contiguous id generation within this group of entity instances created with the factory methods
   */
  public abstract void setIdGroupingToken(IdGroupingToken tokenId);

  /**
   * Collect the id grouping token used by this factory
   * 
   * @return idGroupingToken the id grouping token used by this builder
   */
  public abstract IdGroupingToken getIdGroupingToken();

  /**
   * recreate the ids for all passed in entities
   * 
   * @param entities to recreate ids for
   */
  public abstract void recreateIds(GraphEntities<E> entities);

  /**
   * create a shallow copy of the passed in entity, albeit with unique internal ids
   * 
   * @param entityToCopy the entity to copy
   * @return new entity based on passed in entity
   */
  public abstract E createUniqueCopyOf(E entityToCopy);  
}
