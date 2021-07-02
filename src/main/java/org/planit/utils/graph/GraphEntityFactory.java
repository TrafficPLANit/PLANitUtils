package org.planit.utils.graph;

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
public interface GraphEntityFactory<E extends GraphEntity> {

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
   * Create a shallow copy of the passed in entity, albeit with unique internal ids. Not registered
   * on the container.
   * 
   * @param entityToCopy the entity to copy
   * @return new entity based on passed in entity
   */
  public abstract E createUniqueCopyOf(GraphEntity entityToCopy); 
  
  /** same as {@code #createUniqueCopyOf(GraphEntity)} only now it is also registered on the container
   * 
   * @param entityToCopy the entity to copy
   * @return new entity based on passed in entity also registered
   */
  public abstract E registerUniqueCopyOf(final GraphEntity entityToCopy);   
}
