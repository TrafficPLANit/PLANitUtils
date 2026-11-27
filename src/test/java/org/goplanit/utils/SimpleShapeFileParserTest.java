package org.goplanit.utils;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.data.Transaction;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DefaultTransaction;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.goplanit.utils.geo.SimpleShapeFileParser;
import org.goplanit.utils.misc.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.goplanit.utils.geo.PlanitGeoDataStoreUtils.findOrCreateFileDataStore;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SimpleShapeFileParserTest {

    private static final GeometryFactory gf = new GeometryFactory();
    private static final double SYD_LON = 151.21;
    private static final double SYD_LAT = -33.85;

    private static void createLayer(String fileName, String typeName) throws Exception {
        // 1. Define the Schema (Attributes)
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName(typeName);
        typeBuilder.setCRS(CRS.decode("EPSG:4326")); // WGS84
        typeBuilder.add("the_geom", Polygon.class);
        typeBuilder.add("name", String.class);
        typeBuilder.add("size_idx", Integer.class);
        SimpleFeatureType TYPE = typeBuilder.buildFeatureType();

        // 2. Create Features
        DefaultFeatureCollection collection = new DefaultFeatureCollection();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

        for (int i = 1; i <= 5; i++) {
            double offset = i * 0.01; // Increase size
            Polygon poly = createGeometry(typeName, offset);

            featureBuilder.add(poly);
            featureBuilder.add(typeName + " " + i);
            featureBuilder.add(i);
            collection.add(featureBuilder.buildFeature(null));
        }

        // 3. Write to Shapefile
        DataStore dataStore = findOrCreateFileDataStore(fileName);
        dataStore.createSchema(TYPE);

        Transaction transaction = new DefaultTransaction("create");
        String createdTypeName = dataStore.getTypeNames()[0];
        SimpleFeatureStore featureStore = (SimpleFeatureStore) dataStore.getFeatureSource(createdTypeName);
        featureStore.setTransaction(transaction);

        try {
            featureStore.addFeatures(collection);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            transaction.close();
            dataStore.dispose();
        }
    }

    private static Polygon createGeometry(String type, double size) {
        Coordinate[] coords;
        switch (type) {
            case "Rectangle":
                coords = new Coordinate[]{
                    new Coordinate(SYD_LON, SYD_LAT),
                    new Coordinate(SYD_LON + size, SYD_LAT),
                    new Coordinate(SYD_LON + size, SYD_LAT + size),
                    new Coordinate(SYD_LON, SYD_LAT + size),
                    new Coordinate(SYD_LON, SYD_LAT)
                };
                break;
            case "Triangle":
                coords = new Coordinate[]{
                    new Coordinate(SYD_LON, SYD_LAT),
                    new Coordinate(SYD_LON + size, SYD_LAT),
                    new Coordinate(SYD_LON + (size / 2), SYD_LAT + size),
                    new Coordinate(SYD_LON, SYD_LAT)
                };
                break;
            case "Trapezium":
                coords = new Coordinate[]{
                    new Coordinate(SYD_LON, SYD_LAT),
                    new Coordinate(SYD_LON + size, SYD_LAT),
                    new Coordinate(SYD_LON + (size * 0.7), SYD_LAT + size),
                    new Coordinate(SYD_LON + (size * 0.3), SYD_LAT + size),
                    new Coordinate(SYD_LON, SYD_LAT)
                };
                break;
            default:
                throw new IllegalArgumentException();
        }
        return gf.createPolygon(coords);
    }

    @Test
    public void createAndParseShapeFileTest(@TempDir Path tempDir){
        var rectangleFile = tempDir.resolve("rectangles.shp");
        var triangleFile = tempDir.resolve("triangles.shp");
        var trapeziumFile = tempDir.resolve("trapeziums.shp");

        try {
            createLayer(rectangleFile.toString(), "Rectangle");
            createLayer(triangleFile.toString(), "Triangle");
            createLayer(trapeziumFile.toString(), "Trapezium");
        } catch (Exception e) {
            fail(e);
        }

        // now parse each file
        var rectangleResult = SimpleShapeFileParser.parseShapeFileAsJtsGeometries(rectangleFile.toString(), true);
        assertEquals(1, rectangleResult.size());
        assertEquals(Long.valueOf(5), rectangleResult.values().stream().findFirst().orElseGet(
            () -> Pair.of(null,new ArrayList<>())).second().size());
        assertEquals("rectangles", rectangleResult.values().stream().findFirst().orElseGet(
            () -> Pair.of(null,new ArrayList<>())).first().getTypeName());
        var triangleResult = SimpleShapeFileParser.parseShapeFileAsJtsGeometries(triangleFile.toString(), true);
        assertEquals(1, triangleResult.size());
        assertEquals(5, triangleResult.values().stream().findFirst().orElseGet(
            () -> Pair.of(null,new ArrayList<>())).second().size());
        assertEquals("triangles", triangleResult.values().stream().findFirst().orElseGet(
            () -> Pair.of(null,new ArrayList<>())).first().getTypeName());
        var trapeziumResult = SimpleShapeFileParser.parseShapeFileAsJtsGeometries(trapeziumFile.toString(), true);
        assertEquals(1, trapeziumResult.size());
        assertEquals("trapeziums", trapeziumResult.values().stream().findFirst().orElseGet(
                () -> Pair.of(null,new ArrayList<>())).first().getTypeName());
        assertEquals(5, trapeziumResult.values().stream().findFirst().orElseGet(
            () -> Pair.of(null,new ArrayList<>())).second().size());

        try {
            Files.delete(rectangleFile.toAbsolutePath());
            Files.delete(triangleFile.toAbsolutePath());
            Files.delete(trapeziumFile.toAbsolutePath());
        }catch (Exception e){
            fail(e);
        }
    }

}