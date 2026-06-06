package org.goplanit.utils.geo;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.strtree.STRtree;
import java.util.List;

/**
 * Build a spatial index based on JTS STR tree given Geometry enabled entities
 *
 * @param <E> type of geometry enabled entity
 */
public class GeometryIndexedContainerImpl<E extends GeometryEnabled> implements GeometryIndexedContainer<E> {

  /** geotools container to use */
  private final STRtree tree = new STRtree();

  /** Constructor
   *
   */
  public GeometryIndexedContainerImpl(){}

  /**
   * {@inheritDoc}
   */
  public void buildIndex(Iterable<E> entities) {
    for (E entity : entities) {
      if (entity.hasGeometry()) {
        Envelope env = entity.getGeometry().getEnvelopeInternal();
        tree.insert(env, entity);
      }
    }
    tree.build();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public List<E> findInBounds(double minX, double minY, double maxX, double maxY) {
    Envelope queryEnv = new Envelope(minX, maxX, minY, maxY);
    return (List<E>) tree.query(queryEnv);
  }

}
