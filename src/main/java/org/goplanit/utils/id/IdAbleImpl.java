package org.goplanit.utils.id;

/**
 * Implementation of Idable interface including hash and equals based on id
 * 
 * @author markr
 *
 */
public abstract class IdAbleImpl implements IdAble {
  
  /** the id */
  private long id;
    
  /** Convenience method to generate and set the id using a unique class identifier and idToken which in turn delegates to
   * the {@code IdGenerator.generateId()}
   * 
   * @param classIdentifier to use
   * @param idtoken to use
   * @return generated and set id
   */
  protected long generateAndSetId(final Class<?> classIdentifier, final IdGroupingToken idtoken) {
    long newId = IdAble.generateId(classIdentifier, idtoken);
    setId(newId);
    return newId;
  }
  
  /**
   * Generate an id based on provided token and class
   * 
   * @param idGroupingToken to use
   * @param clazz           to register for
   * @return generated id
   */
  protected static long generateId(IdGroupingToken idGroupingToken, Class<? extends IdAble> clazz) {
    return IdGenerator.generateId(idGroupingToken, clazz);
  }  
  
  /** set the id 
   * 
   * @param id to set
   */
  protected void setId(long id) {
   this.id = id;    
  }    
      
  /** constructor 
   * 
   * @param id to use
   */
  public IdAbleImpl(long id) {
    setId(id);
  }  
    
  /** Copy constructor
   * 
   * @param other to copy from
   */
  public IdAbleImpl(IdAbleImpl other) {
    setId(other.getId());
  }
  

  /**
   * {@inheritDoc}
   */  
  @Override
  public long getId() {
    return id;
  }
    
  /**
   * {@inheritDoc}
   */  
  @Override
  public int hashCode() {
    return idHashCode();
  }

  /**
   * {@inheritDoc}
   */    
  @Override
  public boolean equals(Object o) {
    return idEquals(o);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public abstract IdAble clone();

}
