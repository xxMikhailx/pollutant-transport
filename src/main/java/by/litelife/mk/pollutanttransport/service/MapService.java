package by.litelife.mk.pollutanttransport.service;

import by.litelife.mk.pollutanttransport.model.InputData;
import by.litelife.mk.pollutanttransport.util.ColorGradationsUtil;
import by.litelife.mk.pollutanttransport.util.GeoUtil;
import com.google.common.collect.ImmutableMap;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Geometry;
import mil.nga.sf.geojson.LineString;
import mil.nga.sf.geojson.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MapService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapService.class);
    private static final String CONCENTRATION_PARAMETER = "concentration";
    private static final String COLOR_PARAMETER = "color";
    private static final double CONCENTRATION_MIN = 0;
    private static final double CONCENTRATION_MAX = 1;

    public String simulate(InputData inputData) throws IOException {
        double[] timeValues = {1, 2, 3};
        double[] concentrationValues = {1, 2, 3};

        FeatureCollection simulationResult = new FeatureCollection();

        ArrayList<Position> positionList = new ArrayList<>();
        int startIndex = getIndexByLonLat(inputData.getLng(), inputData.getLat(), positionList);
        ArrayList<Position> subPositionList = new ArrayList<>(positionList.subList(startIndex, positionList.size() - 1));

        double currentConcentration = 1.0;
        int i = 0;
        double timeInMins = 0;
        while (currentConcentration > 0.1 && currentConcentration <= CONCENTRATION_MAX && i < subPositionList.size() - 1) {
            Position currentPosition = subPositionList.get(i);
            Position nextPosition = subPositionList.get(i + 1);

            LineString currentLine = new LineString(Arrays.asList(currentPosition, nextPosition));
            Feature currentFeature = new Feature(currentLine);

            double distance = GeoUtil.distance(currentPosition, nextPosition);
            timeInMins += (distance / inputData.getRiverSpeed()) / 60;

            if (currentConcentration < CONCENTRATION_MIN) {
                LOGGER.warn("Concentration {} is less than 0.", currentConcentration);
                addFeatureProperties(CONCENTRATION_MIN, currentFeature, simulationResult);
                continue;

            } else if (currentConcentration > CONCENTRATION_MAX) {
                LOGGER.warn("Concentration {} is greater than 1.", currentConcentration);
                addFeatureProperties(CONCENTRATION_MAX, currentFeature, simulationResult);
                continue;
            }

            addFeatureProperties(currentConcentration, currentFeature, simulationResult);
            i++;
        }

        return FeatureConverter.toStringValue(simulationResult);
    }

    private void addFeatureProperties(double concentration, Feature feature, FeatureCollection featureCollection) {
        String currentColor = ColorGradationsUtil.getColor(concentration);

        feature.setProperties(ImmutableMap.of(CONCENTRATION_PARAMETER, concentration,
                COLOR_PARAMETER, currentColor));
        featureCollection.addFeature(feature);
    }

    private List<Position> extractPositionList(Resource geoJsonResource) throws IOException {
        String geoJson = new String(Files.readAllBytes(geoJsonResource.getFile().toPath()));

        FeatureCollection featureCollection = FeatureConverter.toFeatureCollection(geoJson);
        Feature feature = featureCollection.getFeatures().get(0);
        Geometry geometry = feature.getGeometry();
        LineString lineString = (LineString) geometry;

        return lineString.getCoordinates();
    }

    private int getIndexByLonLat(double lon, double lat, ArrayList<Position> positions) {
        for (Position position : positions) {
            if (position.getX().equals(lon) && position.getY().equals(lat))
                return positions.indexOf(position);
        }
        return -1;
    }
}
