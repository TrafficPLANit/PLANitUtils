package org.planit.utils.id;

/** A base abstract class for factories that create entities with a managed internal id with directed access
 * to the container the instances are to be registered on once created 
 * 
 * @author markr
 *
 * @param <E> type the factory is creating instances for
 */
public interface ContainerisedManagedIdEntityFactory<E extends ManagedId> extends ManagedIdEntityFactory<E> {
  
  /** same as {@code #createUniqueCopyOf(E)} only now it is also registered on the container
   * 
   * @param entityToCopy the entity to copy
   * @return new entity based on passed in entity also registered
   */
  public abstract E registerUniqueCopyOf(final ManagedId entityToCopy);
     
}
