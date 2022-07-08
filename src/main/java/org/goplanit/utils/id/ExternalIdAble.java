package org.goplanit.utils.id;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.misc.CharacterUtils;
import org.goplanit.utils.misc.StringUtils;

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
  
  /**
   * set the external id
   * 
   * @param xmlId to set
   */
  public default void setXmlId(final long xmlId) {
    setXmlId(String.valueOf(xmlId));
  }
      
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


  /**
   * append the external id with additional id if non-empty, separated with comma
   *
   * @param appendWith to append external id with
   */
  public default void appendExternalId(final String appendWith){
    appendExternalId(appendWith, CharacterUtils.COMMA);
  }

  /**
   * append the external id with additional id if non-empty, separated with provided separator
   *
   * @param appendWith to append external id with
   * @param separator to use
   */
  public default void appendExternalId(final String appendWith, final Character separator){
    PlanItRunTimeException.throwIfNull(separator,"Separator null");
    if(!hasExternalId()){
      setExternalId(appendWith);
    }
    if(StringUtils.isNullOrBlank(appendWith)){
      return;
    }
    setExternalId(String.join(separator.toString(), getExternalId(), appendWith));
  }

}
