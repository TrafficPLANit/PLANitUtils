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
   * recreate the internal id and set it
   * 
   * @return
   */
  public abstract long recreateId(IdGroupingToken tokenId);
  
  /**
   * Each managed id class is expected to generate its ids based on its class signature.
   * To be able to generate the correct id the class used for id generation is to be provided
   * via this method call. 
   * 
   * @return idClass to use for generating ids for instances of this idable derived class
   */
  public abstract Class<? extends IdAble> getIdClass();   

}
