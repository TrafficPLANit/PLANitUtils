package org.planit.utils.id;

/**
 * A class implementing this interface signals that it is id-able
 * 
 * 
 * @author markr
 *
 */
public interface IdAble extends Comparable<IdAble>, Cloneable {
  
  /** Convenience method to generate an id using a unique class identifier and idToken which in turn delegates to
   * the {@code IdGenerator.generateId()}
   * 
   * @param classIdentifier to use
   * @param idtoken to use
   */
  public static long generateId(final Class<?> classIdentifier, final IdGroupingToken idtoken) {
    return IdGenerator.generateId(idtoken, classIdentifier);
  }   
  
  /** collect id of the entity
   * @return id
   */
  public abstract long getId();  
  
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
  public default boolean idEquals(Object o) {
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
  public default int idHashCode() {
    return Long.valueOf(this.getId()).hashCode();
  }  
  
  /**
   * Compare based on id
   * 
   * @param o object to compare id to
   * @return compare result identical to Long.compare
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public default int compareTo(IdAble o) {
    return Long.compare(this.getId(), o.getId());
  }  
   
    
}
