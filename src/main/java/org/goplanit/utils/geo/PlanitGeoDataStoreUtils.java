package org.goplanit.utils.geo;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FileDataStoreFinder;
import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.misc.UrlUtils;
import org.goplanit.utils.resource.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlanitGeoDataStoreUtils {

  private static final Logger LOGGER = Logger.getLogger(SimpleShapeFileParser.class.getCanonicalName());

  /**
   * Create a data store for database based type, e.g., geopackage. Use the dbType string to indicate which
   * type of database we're using
   * <ul>
   *   <li>"geopkg" for geopackage, see <a href="https://docs.geotools.org/latest/userguide/library/data/geopackage.html">geotools</a></li>
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
        LOGGER.severe(String.format("Unable to obtain data store for %s in location %s",params, outputFileNameWithPath));
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
   * @param params additional params to feed the datastore, ("dbtype", "geopkg") etc. (supplemented with param to create it)
   * @return created datastore compatible with chosen type
   */
   public static DataStore createFileDataBaseDataStore(
      String outputFileNameWithPath, Pair<String,Object>... params){
    return findFileDataBaseDataStoreWithParams(
        outputFileNameWithPath,
        Stream.concat(
            Arrays.stream(params), Stream.of(Pair.of("create",true))).collect(Collectors.toList()).toArray(new Pair[0]));
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
          dataStore = factory.createNewDataStore(params);
        }
    }catch (Exception e){
      LOGGER.severe("Cause: "+ (e.getMessage()));
    }
    return dataStore;
  }
}
