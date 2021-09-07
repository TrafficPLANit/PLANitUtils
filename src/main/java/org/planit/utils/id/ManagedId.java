package org.planit.utils.id;

/**
 * Interface supporting methods that aid in classes that implement {@code IdAble} where this id is based
 * on a generated id via the IdGenerator using class signatures and grouping tokens to manage the id generation
 * for the instances
 * 
 * @author markr
 *
 */
public interface ManagedId extends IdAble {
  
  /**
   * recreate the internal id(s) and set them including the Idable id
   * 
   * @return
   */
  public abstract long recreateManagedIds(IdGroupingToken tokenId);
  
  /**
   * Each managed id class is expected to generate its ids based on its class signature.
   * To be able to generate the correct id the class used for id generation is to be provided
   * via this method call. 
   * 
   * @return idClass to use for generating ids for instances of this idable derived class
   */
  public abstract Class<? extends IdAble> getIdClass();   
  
  /**
   * Each class that has a managed id, should be able to reset any children that themselves are managedIdEntity containers. 
   * This ensures that when resetting such a container any child containers are also reset
   */
  public default void resetChildManagedIdEntities() {
    // by default do nothing, yet if the entity contains ManagedIdEntities derived classes, it should reset those by overriding this method
  }
 

}
