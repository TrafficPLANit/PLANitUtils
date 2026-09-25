package org.goplanit.utils.id;

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
}
