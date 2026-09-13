package org.goplanit.utils.geo;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilities for stable string representations of GeoTools simple features.
 */
public final class PlanitSimpleFeatureStringUtils {

  /** Utility class */
  private PlanitSimpleFeatureStringUtils() {}

  /**
   * Create a stable sorted schema descriptor string representation.
   *
   * @param featureType to represent
   * @return schema descriptor strings
   */
  public static List<String> asSortedSchemaDescriptorStrings(SimpleFeatureType featureType) {
    return featureType.getAttributeDescriptors().stream()
        .map(descriptor -> String.format(
            "%s:%s",
            descriptor.getLocalName(),
            descriptor.getType().getBinding().getCanonicalName()))
        .sorted()
        .collect(Collectors.toList());
  }

  /**
   * Collect stable feature string hashes and counts without retaining each complete feature string.
   *
   * @param featureSource to read
   * @param coordinateTolerance absolute coordinate tolerance
   * @return feature string hash counts
   * @throws IOException when features cannot be read
   */
  public static Map<String, Integer> collectStableFeatureStringHashCounts(
      SimpleFeatureSource featureSource, double coordinateTolerance) throws IOException {
    var featureHashCounts = new HashMap<String, Integer>();
    var featureCollection = featureSource.getFeatures();
    try (SimpleFeatureIterator iterator = featureCollection.features()) {
      while (iterator.hasNext()) {
        featureHashCounts.merge(createSha256Hash(asStableFeatureString(iterator.next(), coordinateTolerance)),
            1, Integer::sum);
      }
    }
    return featureHashCounts;
  }

  /**
   * Create a stable feature string representation.
   *
   * @param feature to represent
   * @param coordinateTolerance absolute coordinate tolerance
   * @return stable feature string
   */
  public static String asStableFeatureString(SimpleFeature feature, double coordinateTolerance) {
    return feature.getProperties().stream()
        .sorted(Comparator.comparing(property -> property.getName().toString()))
        .map(property -> asStablePropertyString(property, coordinateTolerance))
        .collect(Collectors.joining("|"));
  }

  /**
   * Create a stable property string representation.
   *
   * @param property to represent
   * @param coordinateTolerance absolute coordinate tolerance
   * @return stable property string
   */
  public static String asStablePropertyString(Property property, double coordinateTolerance) {
    return property.getName() + "=" + asStableValueString(property.getValue(), coordinateTolerance);
  }

  /**
   * Create a stable value string representation.
   *
   * @param value to represent
   * @param coordinateTolerance absolute coordinate tolerance
   * @return stable value string
   */
  public static String asStableValueString(Object value, double coordinateTolerance) {
    if (value == null) {
      return "<null>";
    }
    if (value instanceof Geometry) {
      return asStableGeometryString((Geometry) value, coordinateTolerance);
    }
    if (value instanceof Float || value instanceof Double) {
      return String.format(
          Locale.ROOT, "%.9f", roundToTolerance(((Number) value).doubleValue(), coordinateTolerance));
    }
    if (value instanceof Number || value instanceof Boolean || value instanceof CharSequence) {
      return Objects.toString(value);
    }
    if (value.getClass().isArray()) {
      var entries = new ArrayList<String>();
      for (int index = 0; index < Array.getLength(value); index++) {
        entries.add(asStableValueString(Array.get(value, index), coordinateTolerance));
      }
      return entries.toString();
    }
    if (value instanceof Collection<?>) {
      return ((Collection<?>) value).stream()
          .map(entry -> asStableValueString(entry, coordinateTolerance))
          .collect(Collectors.joining(",", "[", "]"));
    }
    return Objects.toString(value);
  }

  /**
   * Create a stable geometry string representation.
   *
   * @param geometry to represent
   * @param coordinateTolerance absolute coordinate tolerance
   * @return stable geometry string
   */
  public static String asStableGeometryString(Geometry geometry, double coordinateTolerance) {
    var roundedGeometry = (Geometry) geometry.copy();
    roundedGeometry.apply((CoordinateFilter) coordinate -> {
      coordinate.x = roundToTolerance(coordinate.x, coordinateTolerance);
      coordinate.y = roundToTolerance(coordinate.y, coordinateTolerance);
      coordinate.z = roundToTolerance(coordinate.z, coordinateTolerance);
    });
    roundedGeometry.geometryChanged();
    roundedGeometry.normalize();
    return roundedGeometry.toText();
  }

  /**
   * Round a value to the configured tolerance.
   *
   * @param value to round
   * @param tolerance tolerance to use
   * @return rounded value
   */
  public static double roundToTolerance(double value, double tolerance) {
    if (!Double.isFinite(value) || tolerance <= 0) {
      return value;
    }
    return Math.rint(value / tolerance) * tolerance;
  }

  /**
   * Create SHA-256 hash for a value.
   *
   * @param value value to hash
   * @return base64 encoded SHA-256 hash
   */
  public static String createSha256Hash(String value) {
    try {
      var digest = MessageDigest.getInstance("SHA-256");
      return Base64.getEncoder().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm unavailable", e);
    }
  }
}
