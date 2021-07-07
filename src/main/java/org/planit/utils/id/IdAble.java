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
public interface IdAble extends Comparable<IdAble>, Cloneable {
  
  /** collect id of the entity
   * @return id
   */
  public long getId();  
  
  /**
   * Create a shallow copy of this entity
   * 
   * @return shallow copy of entity
   */
  public abstract IdAble clone();

  /**
   * equals based on id
   * 
   * @param o object to compare id to
   * @return true when equal id, false otherwise
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @SuppressWarnings("unused")
  default boolean idEquals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof IdAble)) {
      return false;
    }
    
    if(o != null) {
      return Long.valueOf(this.getId()).equals(Long.valueOf(((IdAble)o).getId()));
    }
    
    return false;
  }
  
  /**
   * Generate hash code based on id
   * 
   * @return id based hash code
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  default int idHashCode() {
    return Long.valueOf(this.getId()).hashCode();
  }  
  
  /**
   * Compare based on id
   * 
   * @param o object to compare id to
   * @return compare result identical to Long.compare
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  default int compareTo(IdAble o) {
    return Long.compare(this.getId(), o.getId());
  }  
   
    
}
