package org.goplanit.utils.geo;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.Filter;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.Pair;
import org.jamel.dbf.DbfReader;
import org.jamel.dbf.structure.DbfDataType;
import org.jamel.dbf.structure.DbfField;
import org.jamel.dbf.structure.DbfRow;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static org.goplanit.utils.geo.PlanitGeoDataStoreUtils.findOrCreateFileDataStore;

public class SimpleShapeFileParser {

  private static final Logger LOGGER = Logger.getLogger(SimpleShapeFileParser.class.getCanonicalName());

  /**
   * Experimental to read DBF outside of geotools but with different library to support newer DBF formats.
   * read header fields
   * todo: not tested
   *
   * @param dbfReader to use
   * @return Dbf fields found
   */
  public static List<DbfField> getDbfFields(DbfReader dbfReader){
    List<DbfField> fields = new ArrayList<>();
    int fieldCount = dbfReader.getHeader().getFieldsCount();
    for (int i = 0; i < fieldCount; i++) {
      fields.add(dbfReader.getHeader().getField(i));
    }
    return fields;
  }

  /**
   * Experimental to read DBF outside of geotools but with different library to support newer DBF formats.
   * given a reader produce DBF rows present
   * todo: not tested
   *
   * @param dbfReader to read in alternative form
   * @return feature type found in DBF
   */
  public static List<DbfRow> getDbfRows(DbfReader dbfReader){

    // Use Dbf iterable (enhanced for loop) to get rows
    List<DbfRow> dbfRows = new ArrayList<>();
    for (int i=0;i<dbfReader.getRecordCount(); i++){
      dbfRows.add(dbfReader.nextRow());
    }
    return dbfRows;
  }

  /**
   * Experimental to read DBF outside of geotools but with different library to support newer DBF formats.
   * create a DBF reader to use
   * todo: not tested
   *
   * @param dbfFileLocation to read in alternative form
   * @return the reader
   * @throws FileNotFoundException if error occurs
   */
  public static DbfReader createDbfReader(String dbfFileLocation) throws FileNotFoundException {
    return new DbfReader(new FileInputStream(dbfFileLocation));
  }

  /**
   * Experimental to read DBF outside of geotools but with different library to support newer DBF formats.
   * Produce feature type the dbf corresponds to
   * todo: not tested
   *
   * @param dbfFileLocation to read in alternative form
   * @return feature type found in DBF
   * @throws FileNotFoundException if error occurs
   */
  public static Pair<DbfReader,SimpleFeatureType> parseDbfExplicitlyAsSimpleFeatureType(
      String dbfFileLocation) throws FileNotFoundException {

      // Build feature type from DBF dynamically
      DbfReader reader = new DbfReader(new FileInputStream(dbfFileLocation));
      int fieldCount = reader.getHeader().getFieldsCount();  // total number of fields
      List<DbfField> fields = getDbfFields(reader);

      SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
      typeBuilder.setName("dummy_layer");
      typeBuilder.add("the_geom", Geometry.class);

      for (DbfField field : fields) {
        DbfDataType type = field.getDataType();
        switch (type) {
          case CHAR:
            typeBuilder.add(field.getName(), String.class);
            break;
          case NUMERIC:
          case FLOAT:
            typeBuilder.add(field.getName(), Double.class);
            break;
          case LOGICAL:               // boolean
            typeBuilder.add(field.getName(), Boolean.class);
            break;
          case DATE:
            typeBuilder.add(field.getName(), java.util.Date.class);
            break;
          default:
            typeBuilder.add(field.getName(), String.class);
            break;
        }
      }

      return Pair.of(reader, typeBuilder.buildFeatureType());
  }


  /**
   * Experimental to read DBF outside of geotools but with different library to support newer DBF formats
   * todo: not tested
   *
   * @param shpFile to parse
   * @param dbfFile to read in alternative form
   * @return feature collection found
   * @throws Exception if error occurs
   */
  public static DefaultFeatureCollection readShapefileWithDbf(File shpFile, File dbfFile) throws Exception {

    var result = parseDbfExplicitlyAsSimpleFeatureType(dbfFile.toString());
    DbfReader reader = result.first();
    SimpleFeatureType featureType = result.second();

    DefaultFeatureCollection collection = new DefaultFeatureCollection();

    // Open shapefile geometries
    ShpFiles shpFiles = new ShpFiles(shpFile);
    GeometryFactory geometryFactory = new GeometryFactory();
    try (ShapefileReader shpReader =
             new ShapefileReader(shpFiles, false, false, geometryFactory)) {

      var dbfRows = getDbfRows(reader);
      var dbfFields = getDbfFields(reader);

      while (shpReader.hasNext()) {
        ShapefileReader.Record record = shpReader.nextRecord();
        Geometry geom = (Geometry) record.shape();

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        builder.set("the_geom", geom);

        for (DbfRow row : dbfRows) {
          for (DbfField field : dbfFields) {
            Object value = row.getObject(field.getName());
            builder.set(field.getName(), value);
          }
        }

        SimpleFeature feature = builder.buildFeature(null);
        collection.add(feature);
      }
    }

    reader.close();
    return collection;
  }

  /**
   * Parse a shape file and convert into a memory model of JTS features by layer applying the provided geotools filter
   *
   * @param location to parse from can be a local file or url
   * @param filter to use
   * @param logStats when true log number of geometries per layer
   * @return map of feature type and the features by layer with layer name as key
   */
  public static Map<String, Pair<SimpleFeatureType, List<SimpleFeature>>> parseShapeFileAsJtsGeometries(
      String location, Filter filter, boolean logStats){

    var featuresByLayer = new TreeMap<String,  Pair<SimpleFeatureType, List<SimpleFeature>>>();
    try {

      // Initialize the data store with connection parameters
      DataStore dataStore = findOrCreateFileDataStore(location);

      if(logStats) {
        LOGGER.info("Parsing Shapes from: "+ location);
        LOGGER.info("TypeNames: "+ Arrays.toString(dataStore.getTypeNames()));
      }

      var layers = dataStore.getTypeNames();
      for (String layerName : layers) {

        // Get the feature source
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(layerName);
        if(logStats) {
          LOGGER.info("Feature count: "+ featureSource.getFeatures().size());
          LOGGER.info(filter != null ? "Filter: "+ filter : "No filter applied");
        }
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

    }catch (IndexOutOfBoundsException e){
      LOGGER.severe("Error parsing shape file, perhaps dbf is using non-standard field names or format, or read" +
          "from wrong location");
      try {
        // todo: not tested needs work
        readShapefileWithDbf(
                new File(location), new File(FileUtils.getFileNameWithoutExtension(location) + ".dbf"));
      }catch(Exception e1){
        e.printStackTrace();
      }
    } catch(IOException e) {
      e.printStackTrace();
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
