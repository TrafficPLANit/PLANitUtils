package org.planit.utils.id;

/**
 * Interface that allows implementation of interfaces that do not allow for ids to be set
 * to indicate that the implementation of this interface does allow for the id to be set.
 * 
 * @author markr
 *
 */
public interface IdSetter<T> {
  
  /**
   * overwrite the id of the implementing class
   * 
   * @param id to set
   */
  public void overwriteId(T id);

}
