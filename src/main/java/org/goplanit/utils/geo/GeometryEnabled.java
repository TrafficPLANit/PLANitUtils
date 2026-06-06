package org.goplanit.utils.geo;

import org.locationtech.jts.geom.Geometry;

/**
 * Separate interface on availability of geometry such that we can do things with it in a geometry centric approach
 *
 * @author markr
 */
public interface GeometryEnabled {

  /**
   * @return true if the entity has a valid JTS geometry assigned
   */
  default boolean hasGeometry(){
    return getGeometry() != null;
  }

  /**
   * Helper to quickly get boundaries without digging into the Geometry
   * envelope object every time.
   * @return [minX, minY, maxX, maxY]
   */
  default double[] getGeometryBounds2D() {
    if (!hasGeometry() || getGeometry() == null) {
      return new double[]{0, 0, 0, 0};
    }
    var env = getGeometry().getEnvelopeInternal();
    return new double[]{env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY()};
  }

  /**
   * Natively returns the JTS geometry.
   */
  Geometry getGeometry();

}