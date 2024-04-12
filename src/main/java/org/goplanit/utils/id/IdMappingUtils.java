package org.goplanit.utils.id;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.util.function.Function;

public class IdMappingUtils {

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
}
