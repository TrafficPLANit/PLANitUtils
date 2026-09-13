package org.goplanit.utils.geo;

import org.geotools.api.data.Query;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;

import java.io.IOException;
import java.util.Arrays;

/**
 * Utilities for geotools api related classes such as feature types and other intermediate wrappers of sptial
 * data that are more abstract than specific JTS instances and as such do not fit in the PlanitJtsUtils
 *
 * @author markr
 */
public class PlanitSimpleFeatureUtils {

  /** dummy constructor */
  private PlanitSimpleFeatureUtils() {}

  /**
   * Checks if the default geometry of a SimpleFeatureType matches any of the target classes.
   *
   * @param featureType The schema to check
   * @param targetClasses The JTS Geometry classes to validate against (e.g., Polygon.class)
   * @return true if the geometry matches one of the targets; false otherwise or if no geometry exists
   */
  @SafeVarargs
  public static boolean hasGeometryType(SimpleFeatureType featureType, Class<? extends Geometry>... targetClasses) {
    if (featureType == null) {
      return false;
    }

    GeometryDescriptor descriptor = featureType.getGeometryDescriptor();
    if (descriptor == null) {
      return false;
    }

    Class<?> binding = descriptor.getType().getBinding();

    return Arrays.stream(targetClasses).anyMatch(target -> target.isAssignableFrom(binding));
  }

  /**
   * Safely reads any primitive attribute from a SimpleFeature and guarantees
   * its extraction as a String, regardless of the underlying schema type.
   *
   * @param feature       The SimpleFeature record to read.
   * @param attributeName The name of the field.
   * @return the String value, null if missing
   */
  public static String readAsNormalizedString(SimpleFeature feature, String attributeName) {
    if (feature == null || attributeName == null) {
      return null;
    }

    Object rawValue = feature.getAttribute(attributeName);
    if (rawValue == null) {
      return null;
    }

    return String.valueOf(rawValue).trim();
  }

  /**
   * Count features in a feature source.
   *
   * @param featureSource feature source
   * @return feature count
   * @throws IOException when features cannot be read
   */
  public static int featureCount(SimpleFeatureSource featureSource) throws IOException {
    var count = featureSource.getCount(Query.ALL);
    if (count >= 0) {
      return count;
    }

    var featureCollection = featureSource.getFeatures();
    var iteratedCount = 0;
    try (SimpleFeatureIterator iterator = featureCollection.features()) {
      while (iterator.hasNext()) {
        iterator.next();
        iteratedCount++;
      }
    }
    return iteratedCount;
  }
}
