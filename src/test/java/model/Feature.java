package model;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Feature {

    public String id;
    public String type;
    public Geometry geometry;
    public Properties properties;

    public static Feature generateFeature() {
        Feature randomFeature = new Feature();
        randomFeature.id = randomString();
        randomFeature.type = "Feature";

        Geometry polygon = new Geometry();
        polygon.type = "Polygon";
        polygon.coordinates = new LinkedList<List<List<Integer>>>();
        polygon.coordinates.add(new LinkedList<List<Integer>>());
        polygon.coordinates.get(0).add(Arrays.asList(100,0));
        polygon.coordinates.get(0).add(Arrays.asList(101,0));
        polygon.coordinates.get(0).add(Arrays.asList(101,1));
        polygon.coordinates.get(0).add(Arrays.asList(100,1));
        polygon.coordinates.get(0).add(Arrays.asList(100,0));
        randomFeature.geometry = polygon;

        Properties properties = new Properties();
        properties.prop0 = randomString();
        randomFeature.properties = properties;

        return randomFeature;
    }

    private static String randomString() {
        String randomString = new Random().ints(97, 122 + 1)
                .limit(8)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return randomString;
    }

    public static class Geometry {
        public String type;
        public List<List<List<Integer>>> coordinates = null;
    }

    public static class Properties {
        public String prop0;
    }
}
