package org.goplanit.utils.id;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities for entities with an id
 *
 * @author markr
 */
public class IdAbleUtils {

  /** static utility class, not to be instantiated */
  private IdAbleUtils() {
  }

  /**
   * Find the entity with the given id
   *
   * @param <T> type of entity
   * @param entities to look in
   * @param id to look for
   * @return the first entity with this id, null when none
   */
  public static <T extends IdAble> T findById(Iterable<T> entities, long id) {
    for (var entity : entities) {
      if (entity.getId() == id) {
        return entity;
      }
    }
    return null;
  }

  /**
   * Collect the ids of the given entities, sorted, e.g., as a key that is the same whatever order the entities are in
   *
   * @param entities to collect the ids of
   * @return their ids, sorted
   */
  public static List<Long> collectSortedIds(Collection<? extends IdAble> entities) {
    return entities.stream().map(IdAble::getId).sorted().collect(Collectors.toList());
  }
}
