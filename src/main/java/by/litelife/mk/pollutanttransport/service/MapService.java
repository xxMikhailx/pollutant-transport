package by.litelife.mk.pollutanttransport.service;

import by.litelife.mk.pollutanttransport.client.OpenWeatherMapClient;
import by.litelife.mk.pollutanttransport.client.dto.LatLon;
import by.litelife.mk.pollutanttransport.client.dto.WeatherApi3HourlyResponse;
import by.litelife.mk.pollutanttransport.client.dto.WeatherApiFullResponse;
import by.litelife.mk.pollutanttransport.client.dto.Wind;
import by.litelife.mk.pollutanttransport.model.InputData;
import by.litelife.mk.pollutanttransport.util.CalculationUtil;
import by.litelife.mk.pollutanttransport.util.ColorGradationsUtil;
import com.google.common.collect.ImmutableMap;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Polygon;
import mil.nga.sf.geojson.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MapService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapService.class);
    private static final String CONCENTRATION_PARAMETER = "concentration";
    private static final String COLOR_PARAMETER = "color";
    private static final int ITERATIONS_NUMBER = 36;
    private static final double ITERATION_TIME_SEC = (180 / ITERATIONS_NUMBER) * 60;
    private static final double MIN_CONCENTRATION = 0.00004;

    @Autowired
    private OpenWeatherMapClient openWeatherMapClient;

    public String simulate(InputData inputData) {
        FeatureCollection simulationResult = new FeatureCollection();

        WeatherApiFullResponse weatherApiFullResponse = openWeatherMapClient.getWinds(new LatLon(inputData.getLat(),
                inputData.getLng()));
        List<WeatherApi3HourlyResponse> hourly3Requests = weatherApiFullResponse.getHourly3Requests();

        Wind firstWind = hourly3Requests.get(0).getWind();
        Pair<LatLon, LatLon> currentPair = CalculationUtil.calculateCircleLatLonPair(new LatLon(inputData.getLat(),
                inputData.getLng()), firstWind.getWindDirection(), inputData.getRadius());

        double distanceSummary = firstWind.getWindSpeed() * ITERATION_TIME_SEC;

        for (int i = 0; i < hourly3Requests.size() - 1; i++) {
            Wind currentWind = hourly3Requests.get(i).getWind();
            Wind nextWind = hourly3Requests.get(i + 1).getWind();
            double currentSpeed = currentWind.getWindSpeed();
            double currentDeg = currentWind.getWindDirection();

            double degreesDifference = CalculationUtil.calculateShortestDegreePath(currentDeg, nextWind.getWindDirection());
            double degreesStep = degreesDifference / ITERATIONS_NUMBER;
            double speedStep = (nextWind.getWindSpeed() - currentWind.getWindSpeed()) / ITERATIONS_NUMBER;

            for (int j = 0; j < ITERATIONS_NUMBER; j++) {
                distanceSummary = distanceSummary + (currentSpeed * ITERATION_TIME_SEC);
                double currentPointsDistance = currentSpeed * ITERATION_TIME_SEC;
                LOGGER.debug("Distance: {}", distanceSummary);
                LOGGER.debug("Distance between current points: {}", currentPointsDistance);

                Pair<LatLon, LatLon> nextPair = CalculationUtil.calculateNextLatLonPair(currentPair, currentDeg, currentPointsDistance);

                List<Position> positionList = Stream.of(currentPair.getFirst(), nextPair.getFirst(),
                        nextPair.getSecond(), currentPair.getSecond())
                        .map(latLon -> new Position(latLon.getLon(), latLon.getLat()))
                        .collect(Collectors.toList());
                List<List<Position>> polygonPositionList = new ArrayList<>();
                polygonPositionList.add(positionList);
                Polygon currentPolygon = new Polygon(polygonPositionList);
                Feature currentPolygonFeature = new Feature(currentPolygon);

                double currentConcentration = CalculationUtil.calculateConcentration(distanceSummary,
                        inputData.getCoefficientF(), inputData.getConcentration());
                LOGGER.debug("Concentration: {}", currentConcentration);
                if (currentConcentration < MIN_CONCENTRATION) {
                    return FeatureConverter.toStringValue(simulationResult);
                }
                addFeatureProperties(currentConcentration, currentPolygonFeature, simulationResult);

                currentDeg = CalculationUtil.calculateNextDegreesDirection(currentDeg, degreesStep);
                currentSpeed += speedStep;
                currentPair = nextPair;
            }
        }

        return FeatureConverter.toStringValue(simulationResult);
    }

    private void addFeatureProperties(double concentration, Feature feature, FeatureCollection featureCollection) {
        String currentColor = ColorGradationsUtil.getColor(concentration);

        feature.setProperties(ImmutableMap.of(CONCENTRATION_PARAMETER, concentration,
                COLOR_PARAMETER, currentColor));
        featureCollection.addFeature(feature);
    }
}
