package org.goplanit.utils.geo;

import org.locationtech.jts.geom.Envelope;

import java.util.List;

/**
 * Interface for indexing geometries of single type that is geometry enabled
 *
 * @param <E> type of geometry enabled
 */
public interface GeometryIndexedContainer<E extends GeometryEnabled> {

  /**
   * Build the index
   * @param entities to build for
   */
  public void buildIndex(Iterable<E> entities);

  /**
   * look up based on index
   *
   * @param minX to use
   * @param minY to use
   * @param maxX to use
   * @param maxY to use
   * @return found entities
   */
  public abstract  List<E> query(double minX, double minY, double maxX, double maxY);

  /**
   * look up based on envelope
   *
   * @param envelope to use
   * @return found entities
   */
  public abstract List<E> query(Envelope envelope);
}
