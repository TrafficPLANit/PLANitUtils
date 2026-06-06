package org.goplanit.utils.geo;

/**
 * Marks a collection or manager as capable of producing a spatial index.
 * * @param <E> The type of entity inside the container, which must be GeometryEnabled.
 */
public interface GeometryIndexableContainer<E extends GeometryEnabled> {

  /**
   * Builds and returns a high-speed spatial index for the entities currently
   * managed by this container.
   */
  GeometryIndexedContainer<E> createSpatialIndex();

  /**
   * An optional utility to check if the container is ready for indexing
   * (e.g., if it actually contains elements with valid geometries).
   */
  default boolean isSpatiallyIndexable() {
    return true;
  }
}
