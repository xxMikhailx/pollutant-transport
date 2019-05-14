package by.litelife.mk.pollutanttransport.service;

import by.litelife.mk.pollutanttransport.model.InputData;
import by.litelife.mk.pollutanttransport.util.ColorGradationsUtil;
import com.google.common.collect.ImmutableMap;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Geometry;
import mil.nga.sf.geojson.LineString;
import mil.nga.sf.geojson.Position;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class MapService {
    private static final String CONCENTRATION_PARAMETER = "concentration";
    private static final String COLOR_PARAMETER = "color";

    @Value("classpath:static/geojson/svisloch-java.geojson")
    Resource svislochRiver;

    public String simulate(InputData inputData) throws IOException {
        double mockRiverSpeed = 0.5; // in m/s

        double mockLon = 27.1635552;
        double mockLat = 54.0374023;

        double[] mockTimeValues = {0, 60, 120, 180}; // in minutes
        double[] mockConcentrationValues = {1, 0.6, 0.3, 0}; // in percents

        PolynomialSplineFunction interpolateFunction = linearInterpolateFunction(mockTimeValues, mockConcentrationValues);
        FeatureCollection simulationResult = new FeatureCollection();

        String svislochGeoJson = new String(Files.readAllBytes(svislochRiver.getFile().toPath()));

        FeatureCollection featureCollection = FeatureConverter.toFeatureCollection(svislochGeoJson);
        Feature feature = featureCollection.getFeatures().get(0);
        Geometry geometry = feature.getGeometry();
        LineString lineString = (LineString) geometry;

        ArrayList<Position> positionList = new ArrayList<>(lineString.getCoordinates());

        int startIndex = getIndexByLonLat(mockLon, mockLat, positionList);

        ArrayList<Position> subPositionList = new ArrayList<>(positionList.subList(startIndex, positionList.size() - 1));

        double currentConcentration = 1.0;
        int i = 0;
        double timeInMins = 0;
        while (currentConcentration > 0.1 && i < subPositionList.size() - 1) {
            Position currentPosition = subPositionList.get(i);
            Position nextPosition = subPositionList.get(i + 1);

            LineString currentLine = new LineString(Arrays.asList(currentPosition, nextPosition));
            Feature currentFeature = new Feature(currentLine);

            double distance = distance(currentPosition, nextPosition);
            timeInMins += (distance / mockRiverSpeed) / 60;
            currentConcentration = interpolateFunction.value(timeInMins);
            String currentColor = ColorGradationsUtil.getColor(currentConcentration);

            currentFeature.setProperties(ImmutableMap.of(CONCENTRATION_PARAMETER, currentConcentration,
                    COLOR_PARAMETER, currentColor));
            simulationResult.addFeature(currentFeature);
            i++;
        }

        return FeatureConverter.toStringValue(simulationResult);
    }

    public PolynomialSplineFunction linearInterpolateFunction(double[] time, double[] concentration) {
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        return linearInterpolator.interpolate(time, concentration);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    public static double distance(Position first, Position second) {
        double lon1 = first.getX();
        double lat1 = first.getY();
        double lon2 = second.getX();
        double lat2 = second.getY();

        double el1 = 0.0;
        double el2 = 0.0;

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private int getIndexByLonLat(double lon, double lat, ArrayList<Position> positions) {
        for (Position position : positions) {
            if (position.getX().equals(lon) && position.getY().equals(lat))
                return positions.indexOf(position);
        }
        return -1;
    }
}
