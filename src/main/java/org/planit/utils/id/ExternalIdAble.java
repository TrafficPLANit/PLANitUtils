package org.planit.utils.id;

/**
 * A class implementing this interface signals that it is external id-able. Anything that has an external id by definition should
 * also be Idable as well. Unlike an id, the external id is by definition modifiable.
 * 
 * further, something that can have an external id also has an xmlId reflecting a unique id used for persistence. This differs from
 * the regular id in that it is less stringent, it is modifiable, and only needs to be unique. It is not necessarily a number and it does
 * not need to be consecutively numbers from zero.
 * 
 * @author markr
 *
 */
public interface ExternalIdAble extends IdAble {
    
  /** get external id of the entity
   * @return external id
   */
  public abstract String getExternalId();
    
  /**
   * set the external id
   * 
   * @param externalId to set
   */
  public abstract void setExternalId(final String externalId);
    
  /** 
   * the id exposed to users as the "normal" id in the PLANit native xml format
   * 
   * @return xmlId
   */
  public abstract String getXmlId();  
  
  /**
   * set the external id
   * 
   * @param xmlId to set
   */
  public abstract void setXmlId(final String xmlId);
    
  /** check if external id is available or not
   * 
   * @return true when not null, false otherwise
   */
  public default boolean hasXmlId() {
    return getXmlId() != null;
  }  
  
  /** check if external id is available or not
   * @return true when nont null, false otherwise
   */
  public default boolean hasExternalId() {
    return getExternalId() != null;
  }

}