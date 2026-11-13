package org.goplanit.utils.id;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.misc.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.goplanit.utils.misc.Pair;

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
        throw new PlanItRunTimeException(String.format("Unknown id mapping type found for %s %s",
                clazz.getName(), idMapper));
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

  /**
   * Map each entity to its id of the given type, sort the ids as text, and join them with commas, see
   * {@link StringUtils#toSortedCommaSeparatedString(Collection, Function)}. Every entity must have an id of that
   * type, e.g. an external id when mapping external ids
   *
   * @param entities to map
   * @param idMapper the type of id to use
   * @return the joined ids, empty when there are no entities
   */
  public static String toSortedCommaSeparatedIds(
      Collection<? extends ExternalIdAble> entities, final IdMapperType idMapper) {
    return StringUtils.toSortedCommaSeparatedString(entities, getIdMappingFunction(idMapper));
  }

  /**
   * Find the entities with each of the given ids, all ids of one type, in one pass over the entities, so it is
   * cheaper than looking up one id at a time. For example, external ids "12" and "40" over nodes where two nodes have
   * external id "12" and none has "40" give "12" with those two nodes, and no entry for "40"
   *
   * @param <T> type of entity
   * @param entities to search
   * @param mappedIds to find, each in the form the id type gives
   * @param idMapper the type of id the ids are
   * @return per id found, the entities with it; an id no entity has is not a key
   */
  public static <T extends ExternalIdAble> Map<String, List<T>> findEntitiesByMappedIds(
      Iterable<? extends T> entities, Collection<String> mappedIds, final IdMapperType idMapper) {
    var wanted = new HashSet<>(mappedIds);
    var idOf = getIdMappingFunction(idMapper);
    var found = new HashMap<String, List<T>>();
    for (T entity : entities) {
      var id = idOf.apply(entity);
      if (id != null && wanted.contains(id)) {
        found.computeIfAbsent(id, key -> new ArrayList<>(1)).add(entity);
      }
    }
    return found;
  }

  /**
   * Find the entities of each list of ids, with one pass over the entities for each type of id listed, see
   * {@link #findEntitiesByMappedIds(Iterable, Collection, IdMapperType)}. For example, a list of external ids "12" and
   * "40" and a list of XML id "7" give, for the first list, the entities with external id "12" or "40", and for the
   * second, those with XML id "7". An id no entity has is handed to recordNotFound and left out of its list
   *
   * @param <T> type of entity
   * @param entities to search
   * @param idLists lists of ids, each with the type of id they are
   * @param recordNotFound receives each id no entity has, once per list it is in
   * @return per list, in the order of the lists, the entities found, in the order of their ids in the list
   */
  public static <T extends ExternalIdAble> List<List<T>> findEntitiesOfEachIdList(
      Iterable<? extends T> entities, List<Pair<Set<String>, IdMapperType>> idLists, Consumer<String> recordNotFound) {
    var foundByIdOfType = new EnumMap<IdMapperType, Map<String, List<T>>>(IdMapperType.class);
    idLists.stream().collect(Collectors.groupingBy(Pair::second,
            Collectors.flatMapping(idList -> idList.first().stream(), Collectors.toSet())))
        .forEach((idType, ids) -> foundByIdOfType.put(idType, findEntitiesByMappedIds(entities, ids, idType)));
    var foundOfEachList = new ArrayList<List<T>>();
    for (var idList : idLists) {
      var found = new ArrayList<T>();
      for (var id : idList.first()) {
        var withId = foundByIdOfType.get(idList.second()).get(id);
        if (withId == null) {
          recordNotFound.accept(id);
        } else {
          found.addAll(withId);
        }
      }
      foundOfEachList.add(found);
    }
    return foundOfEachList;
  }
}
