package org.goplanit.utils.geo;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.locationtech.jts.geom.Geometry;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static org.goplanit.utils.geo.PlanitGeoDataStoreUtils.findOrCreateFileDataStore;

public class SimpleShapeFileParser {

  private static final Logger LOGGER = Logger.getLogger(SimpleShapeFileParser.class.getCanonicalName());

  /**
   * Parse a shape file and convert into a memory model of JTS geometries by layer
   *
   * @param location to parse from can be a local file or url
   * @param logStats when true log number of geometries per layer
   * @return map of geometry by layer in a list with layer name as key
   */
  public static Map<String, List<Geometry>> parseShapeFileAsJtsGeometries(String location, boolean logStats) {
    var geometriesByLayer = new TreeMap<String, List<Geometry>>();
    try {

      // Initialize the data store with connection parameters
      DataStore dataStore = findOrCreateFileDataStore(location);

      if(logStats) {
        LOGGER.info("Parsing Shapes from: "+ location);
      }

      var layers = dataStore.getTypeNames();
      for (String layerName : layers) {
        var layerGeometries = new ArrayList<Geometry>(100);
        geometriesByLayer.put(layerName, layerGeometries);

        // Get the feature source
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(layerName);
        // Get the schema (metadata about the data structure)
        SimpleFeatureType schema = featureSource.getSchema();

        // Iterate through the features
        try (SimpleFeatureIterator iterator = featureSource.getFeatures().features()) {
          while (iterator.hasNext()) {
            SimpleFeature feature = iterator.next();
            // Access the default geometry, which is a JTS Geometry object
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            layerGeometries.add(geometry);
          }
          if (logStats) {
            LOGGER.info(String.format("Parsed Shape file layer: %s - containing %d geometries",
                layerName, layerGeometries.size()));
          }
        }
        // Important: close the iterator
      }

      // Close the data store
      dataStore.dispose();

    }catch (IOException e) {
      throw new PlanItRunTimeException("Unable to complete parsing shape file from " + location,e);
    }
    return geometriesByLayer;
  }
}
