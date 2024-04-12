package org.goplanit.utils.id;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.misc.CharacterUtils;
import org.goplanit.utils.misc.StringUtils;

import java.util.function.Function;

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

  /**
   * create a function that takes a a class that extends {@link ExternalIdAble} and generate the appropriate id based on the user configuration
   *
   * @param <T>      ExternalIdable
   * @param clazz    to use
   * @param idMapper the type of mapping function to create
   * @return function that generates node id's for MATSIM node output
   *
   */
  public static <T extends ExternalIdAble> Function<T, String> createIdMappingFunction(Class<T> clazz, final IdMapperType idMapper) {
    switch (idMapper) {
      case ID:
        return (instance) -> instance!=null ? Long.toString(instance.getId()) : null;
      case EXTERNAL_ID:
        return (instance) -> instance!=null ? instance.getExternalId() : null;
      case XML:
        return (instance) -> instance!=null ? instance.getXmlId() : null;
      default:
        throw new PlanItRunTimeException(String.format("unknown id mapping type found for %s %s", clazz.getName(), idMapper));
    }
  }

  /** get id based on the id mapping type (ID, XML_ID, EXETERNAL_ID)
   *
   * @return id (in string format)
   */
  public default String getIdAsString(IdMapperType idMappingType){
    switch (idMappingType) {
      case ID:
        return String.valueOf(getId());
      case EXTERNAL_ID:
        return getExternalId();
      case XML:
        return getXmlId();
      default:
        throw new PlanItRunTimeException(String.format("Unknown id mapping type %s found", idMappingType.toString()));
    }
  }
    
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
   * Collect external id split by default comma separator
   *
   * @return split external ids as array
   */
  public default String[] getSplitExternalId(){
    return getSplitExternalId(CharacterUtils.COMMA);
  }

  /**
   * Collect external id split by custom  separator
   *
   * @param separator to split by
   * @return split external ids as array
   */
  public default String[] getSplitExternalId(char separator){
    return getExternalId().split(String.valueOf(separator));
  }


  /**
   * append the external id with additional id if non-empty, separated with provided separator
   *
   * @param appendWith to append external id with
   * @param separator to use
   */
  public default void appendExternalId(final String appendWith, final Character separator){
    PlanItRunTimeException.throwIfNull(separator,"Separator null");
    if(StringUtils.isNullOrBlank(appendWith)){
      return;
    }
    if(!hasExternalId()){
      setExternalId(appendWith);
      return;
    }
    setExternalId(String.join(separator.toString(), getExternalId(), appendWith));
  }

  /**
   * Returns string representation contain the three ids, id, XMLid and external id
   * @return string representation
   */
  public default String getIdsAsString(){
    return String.format("id: %d, xmlId: %s, extId: %s", getId(), getXmlId(), getExternalId());
  }

}
