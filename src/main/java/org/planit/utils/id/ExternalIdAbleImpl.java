package org.planit.utils.id;

/**
 * Implementation of ExternalIdable interface
 * 
 * @author markr
 *
 */
public class ExternalIdAbleImpl implements ExternalIdable{
  
  private long id;
  
  private String xmlId;
  
  private String externalId;
  
  /** constructor 
   * 
   * @param id to use
   */
  public ExternalIdAbleImpl(long id) {
    setId(id);
    setXmlId(null);
    setExternalId(null);
  }  
  
  /** Copy constructor
   * 
   * @param other to copy from
   */
  public ExternalIdAbleImpl(ExternalIdAbleImpl other) {
    setId(other.getId());
    setXmlId(other.getXmlId());
    setExternalId(other.getExternalId());
  }
  

  /** set the id 
   * 
   * @param id to set
   */
  protected void setId(long id) {
   this.id = id;    
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
  public String getExternalId() {
    return externalId;
  }

  /**
   * {@inheritDoc}
   */  
  @Override
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  /**
   * {@inheritDoc}
   */  
  @Override
  public String getXmlId() {
    return xmlId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setXmlId(String xmlId) {
    this.xmlId = xmlId;
  }

}
