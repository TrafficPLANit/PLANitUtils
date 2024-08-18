package org.goplanit.utils.id;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.util.function.Function;

/**
 * Convenience methods for id mapping
 *
 * @author markr
 */
public class IdMappingUtils {

  public static final Function<ExternalIdAble, String> ID_TO_STRING_MAPPING =
      (instance) -> instance!=null ? Long.toString(instance.getId()) : null;

  public static final Function<ExternalIdAble, String> XML_ID_TO_STRING_MAPPING =
      (instance) -> instance!=null ? instance.getXmlId() : null;

  public static final Function<ExternalIdAble, String> EXTERNAL_ID_TO_STRING_MAPPING =
      (instance) -> instance!=null ? instance.getExternalId() : null;

  /**
   * create an explicit newly created function that takes a class that extends {@link ExternalIdAble} and generate
   * the appropriate id based on the user configuration
   *
   * @param <T>      ExternalIdable
   * @param clazz    to use
   * @param idMapper the type of mapping function to create
   * @return function that generates string forms of an id
   *
   */
  public static <T extends ExternalIdAble> Function<T, String> createIdMappingFunction(
      Class<T> clazz, final IdMapperType idMapper) {
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

  /**
   * collect one of the predefined functions that take an externalIdAble class and generate
   * the appropriate id based on the user configuration
   *
   * @param idMapper the type of mapping function to create
   * @return function that generates string forms of an id
   *
   */
  public static Function<ExternalIdAble, String> getIdMappingFunction(final IdMapperType idMapper) {
    switch (idMapper) {
      case ID:
        return ID_TO_STRING_MAPPING;
      case EXTERNAL_ID:
        return EXTERNAL_ID_TO_STRING_MAPPING;
      case XML:
        return XML_ID_TO_STRING_MAPPING;
      default:
        throw new PlanItRunTimeException("Unknown id mapping type found" + idMapper);
    }
  }
}
