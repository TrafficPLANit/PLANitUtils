package org.goplanit.utils.id;

import org.goplanit.utils.misc.LoggingUtils;
import org.goplanit.utils.misc.Pair;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Id utils for ExternableId instances
 *
 * @author markr
 */
public class ExternalIdAbleUtils {

  /**
   * Based on a pair of XMLids construct a combined XML id. In case entry of pair is null, inject "N/A"
   *
   * @param xmlIdPair pair of instances that are ExternalIdAbles
   * @param separator to use
   * @param postFixToApply apply to each XmlId
   * @return combined XmlId string in the form of "pair_first+postfix | pair_second+postfix"
   */
  public static String joinXmlIdPair(
          Pair<? extends ExternalIdAble,? extends ExternalIdAble> xmlIdPair,
          String separator,
          String postFixToApply){

    String noXmlId = "N/A"; // apply in case no Xml id is set
    StringBuilder sb = new StringBuilder();
    sb.append(
            (xmlIdPair.firstNotNull() && xmlIdPair.first().hasXmlId()) ?
                    xmlIdPair.first().getXmlId() : noXmlId).append(postFixToApply);
    sb.append(separator); // separator
    sb.append(xmlIdPair.secondNotNull() && xmlIdPair.second().hasXmlId() ?
            xmlIdPair.second().getXmlId() : noXmlId).append(postFixToApply);
    return sb.toString();
  }

  /**
   * Combine the ids of a collection of entities into a single bracketed list, e.g.
   * "[(id: 4, xmlId: 12, extId: 12),(id: 9, xmlId: 41, extId: 41)]", so many entities can be reported in one log
   * entry rather than one entry each
   *
   * @param entities to list, when null or empty an empty list is returned
   * @return combined string of the ids of all entities
   */
  public static String toIdsAsString(Collection<? extends ExternalIdAble> entities) {
    return toIdsAsString(entities, Integer.MAX_VALUE);
  }

  /**
   * Same as {@link #toIdsAsString(Collection)}, listing at most the given number of entities and stating how many
   * were left out, so a large collection does not flood a log entry
   *
   * @param entities to list, when null or empty an empty list is returned
   * @param maxEntries maximum number of entities to list
   * @return combined string of the ids of the listed entities
   */
  public static String toIdsAsString(Collection<? extends ExternalIdAble> entities, int maxEntries) {
    if (entities == null || entities.isEmpty()) {
      return LoggingUtils.surroundWithBrackets("");
    }

    var listedEntities = entities.stream().limit(maxEntries)
        .map(e -> "(" + e.getIdsAsString() + ")").collect(Collectors.joining(","));
    int notListedCount = entities.size() - Math.min(entities.size(), maxEntries);

    return LoggingUtils.surroundWithBrackets(notListedCount > 0 ?
        String.format("%s,... (%d more)", listedEntities, notListedCount) : listedEntities);
  }

  /**
   * Verify if an optional mapping gives an id for the key, for example a waiting area the user nominated for a node
   *
   * @param <K> type of the key
   * @param idMapping from key to id, may be null, may give null when the key is not mapped
   * @param key to map
   * @return true when the mapping exists and gives an id for the key, false otherwise
   */
  public static <K> boolean hasMappedId(Function<K, String> idMapping, K key) {
    return idMapping != null && idMapping.apply(key) != null;
  }

  /**
   * Verify if an optional mapping maps the key to the given id, for example whether the user nominated this access
   * link for a waiting area
   *
   * @param <K> type of the key
   * @param idMapping from key to id, may be null, may give null when the key is not mapped
   * @param key to map
   * @param id to compare with
   * @return true when the mapping exists and maps the key to this id, false otherwise
   */
  public static <K> boolean isMappedToId(Function<K, String> idMapping, K key, String id) {
    return hasMappedId(idMapping, key) && idMapping.apply(key).equals(id);
  }
}
