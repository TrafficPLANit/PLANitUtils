package org.goplanit.utils.geo;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.Pair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlanitGeoDataStoreUtils {

  private static final Logger LOGGER = Logger.getLogger(SimpleShapeFileParser.class.getCanonicalName());

  /** Shape file extension */
  public static final String SHAPE_FILE_EXTENSION = ".shp";

  /** GeoPackage file extension */
  public static final String GEOPACKAGE_EXTENSION = ".gpkg";

  /**
   * Create a data store for database based type, e.g., geopackage. Use the dbType string to indicate which
   * type of database we're using
   * <ul>
   *   <li>"geopkg" for geopackage, s
   *   ee <a href="https://docs.geotools.org/latest/userguide/library/data/geopackage.html">geotools</a></li>
   * </ul>
   * @param outputFileNameWithPath the file to store the result in/read from
   * @param params params to feed the datastore, e.g., ("create,true), ("read-only", true), in addition to
   *               auto-supplemented ("database",outputFileNameWithPath)
   * @return created datastore compatible with chosen type
   */
  public static DataStore findFileDataBaseDataStoreWithParams(
      String outputFileNameWithPath, Pair<String,Object>... params){
    DataStore datastore = null;
    var resolvedFile = FileUtils.resolveFileFromAbsoluteOrRelativeString(outputFileNameWithPath);
    var databaseParam = Pair.of("database", resolvedFile);
    var paramMap = Stream.concat(Stream.of(databaseParam), Stream.of(params)).collect(
        Collectors.toMap(Pair::first, Pair::second));
    try {
      datastore = DataStoreFinder.getDataStore(paramMap);
      if(datastore == null){
        LOGGER.severe(String.format("Unable to obtain data store for %s in location %s",
                params, outputFileNameWithPath));
      }
    }catch (Exception e){
      LOGGER.severe("Cause: "+ (e.getMessage()));
      return null;
    }
    return datastore;
  }

   /**
   * Create a data store for single file database based type, e.g., geopackage. Use the dbType string to indicate which
   * type of database we're using
   * <ul>
   *   <li>"geopkg" for geopackage, see <a href="https://docs.geotools.org/latest/userguide/library/data/geopackage.html">geotools</a></li>
   * </ul>
   * @param outputFileNameWithPath the file to store the result in/read from
   * @param params additional params to feed the datastore, ("dbtype", "geopkg") etc.
    *               (supplemented with param to create it)
   * @return created datastore compatible with chosen type
   */
   public static DataStore createFileDataBaseDataStore(
      String outputFileNameWithPath, Pair<String,Object>... params){
    return findFileDataBaseDataStoreWithParams(
        outputFileNameWithPath,
        Stream.concat(
            Arrays.stream(params), Stream.of(Pair.of("create",true))).collect(
                    Collectors.toList()).toArray(new Pair[0]));
  }

  /**
   * Locate the resource (file with geometries) and obtain the relevant datastore that will be able to access it.
   * If it does not exist do nothing.
   *
   * @param geoResourceLocation the location of the geometries
   * @return geotools data store for the provided location, null if it does not exist
   * @throws IOException in case of error
   */
  public static DataStore findFileDataStore(String geoResourceLocation) throws IOException {

    // FIND EXISTING:
    // Initialize the data store with basic connection parameters of just the location - nothing else
    var params = new HashMap<String, Object>();
    // convert to universal URL
    var locationAsFile = FileUtils.resolveFileFromAbsoluteOrRelativeString(geoResourceLocation);
    if(!locationAsFile.exists()) {
      return null;
    }
    params.put("url", locationAsFile.toURI().toURL());
    return DataStoreFinder.getDataStore(params);
  }

  /**
   * Locate the resource (file with geometries) and obtain the relevant datastore that will be able to access it.
   * If it does not exist create a new empty data store compatible with the file format
   *
   * @param geoResourceLocation the location of the geometries
   * @return geotools data store for the provided location if match could be found
   * @throws IOException in case of error
   */
  public static DataStore findOrCreateFileDataStore(String geoResourceLocation) throws IOException {

    // FIND EXISTING (may also be able to create new data store since version upgrade directly)
    DataStore dataStore = findFileDataStore(geoResourceLocation);
    if(dataStore!= null){
      return dataStore;
    }

    // create NEW FILE data store explicitly if location suggests it is simple file based
    try {
        var fileExtension = FileUtils.getExtension(geoResourceLocation);
        var factory = FileDataStoreFinder.getDataStoreFactory(fileExtension);
        if(factory != null){
          var params = new HashMap<String, Object>();
          var url = FileUtils.resolveFileFromAbsoluteOrRelativeString(geoResourceLocation).toURI().toURL();
          params.put("url", url);
          params.put("charset", StandardCharsets.UTF_8); // needed to make robust
          dataStore = factory.createNewDataStore(params);
        }
    }catch (Exception e){
      LOGGER.severe("Cause: "+ (e.getMessage()));
    }
    return dataStore;
  }

  /**
   * Open a GIS dataset file for reading.
   *
   * @param datasetFile dataset file
   * @return data store
   * @throws IOException when file cannot be read
   */
  public static DataStore openDataStore(Path datasetFile) throws IOException {
    return openDataStore(datasetFile, true);
  }

  /**
   * Open a GIS dataset file.
   *
   * @param datasetFile dataset file
   * @param readOnly when true open database-backed stores in read-only mode
   * @return data store
   * @throws IOException when file cannot be read
   */
  public static DataStore openDataStore(Path datasetFile, boolean readOnly) throws IOException {
    var fileName = datasetFile.getFileName().toString().toLowerCase(Locale.ROOT);
    if (fileName.endsWith(GEOPACKAGE_EXTENSION)) {
      return findFileDataBaseDataStoreWithParams(
          datasetFile.toAbsolutePath().toString(),
          Pair.of("dbtype", "geopkg"),
          Pair.of("read-only", readOnly));
    }
    return findFileDataStore(datasetFile.toAbsolutePath().toString());
  }

  /**
   * Count all features in a dataset.
   *
   * @param datasetFile dataset file
   * @return total feature count
   * @throws IOException when features cannot be read
   */
  public static int totalFeatureCount(Path datasetFile) throws IOException {
    return layerFeatureCounts(datasetFile).values().stream().mapToInt(Integer::intValue).sum();
  }

  /**
   * Count features per layer in a dataset.
   *
   * @param datasetFile dataset file
   * @return layer feature counts
   * @throws IOException when features cannot be read
   */
  public static Map<String, Integer> layerFeatureCounts(Path datasetFile) throws IOException {
    DataStore dataStore = null;
    try {
      dataStore = openDataStore(datasetFile);
      if (dataStore == null) {
        throw new IllegalStateException("Unable to open GIS dataset: " + datasetFile);
      }
      var featureCounts = new LinkedHashMap<String, Integer>();
      for (String typeName : sortedTypeNames(dataStore)) {
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
        featureCounts.put(typeName, PlanitSimpleFeatureUtils.featureCount(featureSource));
      }
      return featureCounts;
    } finally {
      if (dataStore != null) {
        dataStore.dispose();
      }
    }
  }

  /**
   * Collect sorted type names.
   *
   * @param dataStore to use
   * @return sorted type names
   * @throws IOException when type names cannot be read
   */
  public static List<String> sortedTypeNames(DataStore dataStore) throws IOException {
    return Arrays.stream(dataStore.getTypeNames()).sorted().collect(Collectors.toList());
  }
}
