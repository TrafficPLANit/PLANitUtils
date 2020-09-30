package org.planit.utils.id;

/**
 * Interface that allows implementations of other interfaces that do not allow for ids to be set
 * to indicate that they do allow for multiple ids to be set. It is up to the implementing class to determine
 * the meaning of these ids
 * 
 * @author markr
 *
 */
public interface MultiIdSetter<T> {
  
  /**
   * overwrite ids of the implementing class
   * 
   * @param ids to set
   */
  @SuppressWarnings("unchecked")
  public void overwriteIds(T... ids);

}
