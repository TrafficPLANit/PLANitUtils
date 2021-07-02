package org.planit.utils.id;

/**
 * Implementation of Idable interface including hash and equals based on id
 * 
 * @author markr
 *
 */
public abstract class IdAbleImpl implements IdAble {
  
  private long id;
  
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
