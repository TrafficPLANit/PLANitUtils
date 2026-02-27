package org.goplanit.utils.geo;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.misc.Pair;
import org.geotools.api.filter.Filter;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static org.goplanit.utils.geo.PlanitGeoDataStoreUtils.findOrCreateFileDataStore;

public class SimpleShapeFileParser {

  private static final Logger LOGGER = Logger.getLogger(SimpleShapeFileParser.class.getCanonicalName());

  /**
   * Parse a shape file and convert into a memory model of JTS features by layer applying the provided geotools filter
   *
   * @param location to parse from can be a local file or url
   * @param logStats when true log number of geometries per layer
   * @return map of feature type and the features by layer with layer name as key
   */
  public static Map<String, Pair<SimpleFeatureType, List<SimpleFeature>>> parseShapeFileAsJtsGeometries(
      String location, Filter filter, boolean logStats) {

    var featuresByLayer = new TreeMap<String,  Pair<SimpleFeatureType, List<SimpleFeature>>>();
    try {

      // Initialize the data store with connection parameters
      DataStore dataStore = findOrCreateFileDataStore(location);

      if(logStats) {
        LOGGER.info("Parsing Shapes from: "+ location);
      }

      var layers = dataStore.getTypeNames();
      for (String layerName : layers) {

        // Get the feature source
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(layerName);
        // Get the schema (metadata about the data structure)
        SimpleFeatureType schema = featureSource.getSchema();

        var layerGeometries = new ArrayList<SimpleFeature>(100);
        featuresByLayer.put(layerName, Pair.of(schema, layerGeometries));

        // Iterate through the features
        var features = filter !=null ? featureSource.getFeatures(filter) :featureSource.getFeatures();
        try (SimpleFeatureIterator iterator = features.features()) {
          while (iterator.hasNext()) {
            SimpleFeature feature = iterator.next();
            layerGeometries.add(feature);
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
    return featuresByLayer;
  }

  /**
   * Parse a shape file and convert into a memory model of JTS features by layer
   *
   * @param location to parse from can be a local file or url
   * @param logStats when true log number of geometries per layer
   * @return map of feature type and the features by layer with layer name as key
   */
  public static Map<String, Pair<SimpleFeatureType, List<SimpleFeature>>> parseShapeFileAsJtsGeometries(
      String location, boolean logStats) {
    return parseShapeFileAsJtsGeometries(location, null, logStats);
  }
}
