package org.goplanit.utils.geo;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;

import java.util.List;

/**
 * Utilities for operations performed on geometry objects
 */
public class PlanitGeometryOperationUtils {

  /**
   * Create a copy that is indexed for fast spatial comparisons of type PreparedPolygon
   * @param polygon to prep
   * @return prepped polygon
   */
  public static PreparedPolygon extractPreparedPolygonForQuickSpatialComparisons(Polygon polygon){
    return (PreparedPolygon) PreparedGeometryFactory.prepare(polygon);
  }

  /**
   * Combine all polygons provided to take the outer area and make that a single new polygon.
   * <p>
   *   Overlap allowed, but if gaps exist result will be a multi polygon with multiple entries
   * </p>
   *
   * @param polygons to take union
   * @return single polygon reflecting outer boundary of the polygons
   */
  public static Geometry extractUnionFromPolygons(List<Geometry> polygons){
    return CascadedPolygonUnion.union(polygons);
  }

  /**
   * Extract largest polygon present from the multi polygon and make a copy
   * @param multi polygon to examine
   * @return found largest polygon
   */
  public static Polygon getLargestPolygon(MultiPolygon multi) {
    Polygon largest = null;
    double maxArea = -1;

    for (int i = 0; i < multi.getNumGeometries(); i++) {
      Geometry geom = multi.getGeometryN(i);

      if (geom instanceof Polygon) {
        double area = geom.getArea();
        if (area > maxArea) {
          maxArea = area;
          largest = (Polygon) geom.copy();
        }
      }
    }

    return largest;
  }
}
