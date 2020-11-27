package org.planit.utils.id;

/**
 * A class implementing this interface signals that it is external id-able. Anything that has an external id by definition should
 * also be Idable as well. Unlike an id, the external id is by defintiion modifiable.
 * 
 * @author markr
 *
 */
public interface ExternalIdable extends Idable {
    
  /** get external id of the entity
   * @return external id
   */
  public Object getExternalId();
  
  /**
   * set the external id
   * 
   * @param externalId to set
   */
  public void setExternalId(final Object externalId);
  
  /** check if external id is available or not
   * @return true when nont null, false otherwise
   */
  default boolean hasExternalId() {
    return getExternalId() != null;
  }

}
