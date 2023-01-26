package org.goplanit.utils.id;

/**
 * Implementation of ExternalIdable interface including hash and equals based on id
 * 
 * @author markr
 *
 */
public class ExternalIdAbleImpl extends IdAbleImpl implements ExternalIdAble {
    
  /** XML id */
  private String xmlId;
  
  /** external id */
  private String externalId;
  
  /** Constructor 
   * 
   * @param id to use
   */
  public ExternalIdAbleImpl(long id) {
    super(id);
    setXmlId(null);
    setExternalId(null);
  }    
  
  /** Copy constructor
   * 
   * @param other to copy from
   */
  public ExternalIdAbleImpl(ExternalIdAbleImpl other) {
    super(other);
    setXmlId(other.getXmlId());
    setExternalId(other.getExternalId());
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

  /**
   * {@inheritDoc}
   */  
  @Override
  public ExternalIdAbleImpl clone() {
    return new ExternalIdAbleImpl(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExternalIdAbleImpl deepClone(){
    return clone() /* no different with regular clone */;
  }


}
