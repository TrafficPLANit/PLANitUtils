package org.goplanit.utils.id;

/**
 * A class implementing this interface signals that it is id-able
 * 
 * 
 * @author markr
 *
 */
public interface IdAble extends Comparable<IdAble> {
  
  /** Convenience method to generate an id using a unique class identifier and idToken which in turn delegates to
   * the {@code IdGenerator.generateId()}
   * 
   * @param classIdentifier to use
   * @param idtoken to use
   * @return generated id
   */
  public static long generateId(final Class<?> classIdentifier, final IdGroupingToken idtoken) {
    return IdGenerator.generateId(idtoken, classIdentifier);
  }   
  
  /** Collect id of the entity
   * 
   * @return id found
   */
  public abstract long getId();  
  
  /**
   * Create a shallow copy of this entity
   * 
   * @return shallow copy of entity
   */
  public abstract IdAble shallowClone();

  /**
   * An id entity should always support a deep copy, i.e., all "owned" members will be deep copied when a clone
   * of this instance is created via this call. To be used with caution if not called by managed id container related code
   *
   * @return deep copy of entity
   */
  public abstract IdAble deepClone();

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
      return this.getId() == ((IdAble) o).getId();
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
