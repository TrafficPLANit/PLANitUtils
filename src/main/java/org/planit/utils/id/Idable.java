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
public interface Idable extends Comparable<Idable> {
  
  /** collect id of the entity
   * @return id
   */
  public long getId();
  
  /**
   * equals based on id
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @SuppressWarnings("unused")
  default boolean idEquals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Idable)) {
      return false;
    }
    
    if(o != null) {
      return Long.valueOf(this.getId()).equals(Long.valueOf(((Idable)o).getId()));
    }
    
    return false;
  }
  
  /**
   * Compare based on id
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  default int idHashCode() {
    return Long.valueOf(this.getId()).hashCode();
  }  
  
  /**
   * Compare based on id
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  default int compareTo(Idable o) {
    return Long.compare(this.getId(), o.getId());
  } 
   
    
}
