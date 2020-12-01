package org.planit.utils.id;

/**
 * A class implementing this interface signals that it is id-able
 * 
 * TODO: refactor so that id becomes internal id, and xmlId becomes the defacto available id
 * used to communicate with users
 * 
 * @author markr
 *
 */
public interface Idable {
  
  /** collect id of the entity
   * @return id
   */
  public long getId();
    
}
