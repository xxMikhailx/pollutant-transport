package by.litelife.mk.pollutanttransport.util;

public class GenerateSimpleGeojson {
    public static String generateGeojson() {
        return "{\n" +
                "  \"type\": \"FeatureCollection\",\n" +
                "  \"features\": [\n" +
                "    {\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"LineString\",\n" +
                "        \"coordinates\": [\n" +
                "          [\n" +
                "            27.1635552,\n" +
                "            54.0374023\n" +
                "          ],\n" +
                "          [\n" +
                "            27.1635552,\n" +
                "            54.0377614\n" +
                "          ],\n" +
                "          [\n" +
                "            27.1635981,\n" +
                "            54.0381647\n" +
                "          ]\n" +
                "        ]\n" +
                "      },\n" +
                "      \"properties\": {\n" +
                "        \n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
    }
}
