package by.litelife.mk.pollutanttransport.service;

import by.litelife.mk.pollutanttransport.client.OpenWeatherMapClient;
import by.litelife.mk.pollutanttransport.client.dto.LatLon;
import by.litelife.mk.pollutanttransport.client.dto.WeatherApi3HourlyResponse;
import by.litelife.mk.pollutanttransport.client.dto.WeatherApiFullResponse;
import by.litelife.mk.pollutanttransport.client.dto.Wind;
import by.litelife.mk.pollutanttransport.model.InputData;
import by.litelife.mk.pollutanttransport.util.CalculationUtil;
import by.litelife.mk.pollutanttransport.util.ColorGradationsUtil;
import by.litelife.mk.pollutanttransport.util.ConversionUtil;
import by.litelife.mk.pollutanttransport.util.GeoUtil;
import com.google.common.collect.ImmutableMap;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Polygon;
import mil.nga.sf.geojson.Position;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static by.litelife.mk.pollutanttransport.util.CalculationUtil.calculateSemicircleLatLonList;
import static by.litelife.mk.pollutanttransport.util.ConversionUtil.convertLatLonToPosition;

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
        double currentDeg = firstWind.getWindDirection();

        for (int i = 0; i < hourly3Requests.size() - 1; i++) {
            Wind currentWind = hourly3Requests.get(i).getWind();
            Wind nextWind = hourly3Requests.get(i + 1).getWind();
            double currentSpeed = currentWind.getWindSpeed();
            double speedStep = (nextWind.getWindSpeed() - currentWind.getWindSpeed()) / ITERATIONS_NUMBER;

            for (int j = 0; j < ITERATIONS_NUMBER; j++) {
                distanceSummary = distanceSummary + (currentSpeed * ITERATION_TIME_SEC);
                double currentPointsDistance = currentSpeed * ITERATION_TIME_SEC;
                LOGGER.debug("Distance: {}", distanceSummary);
                LOGGER.debug("Distance between current points: {}", currentPointsDistance);

                Pair<LatLon, LatLon> nextPair = CalculationUtil.calculateNextLatLonPair(currentPair, currentDeg, currentPointsDistance);

                List<Position> positionList = Stream.of(currentPair.getFirst(), nextPair.getFirst(),
                        nextPair.getSecond(), currentPair.getSecond())
                        .map(ConversionUtil::convertLatLonToPosition)
                        .collect(Collectors.toList());
                List<List<Position>> polygonPositionList = new ArrayList<>();
                polygonPositionList.add(positionList);
                Polygon currentPolygon = new Polygon(polygonPositionList);
                Feature currentPolygonFeature = new Feature(currentPolygon);

                double currentConcentration = CalculationUtil.calculateConcentration(distanceSummary,
                        inputData.getCoefficientF(), inputData.getConcentration());
                LOGGER.debug("Concentration: {}", currentConcentration);
                if (currentConcentration < MIN_CONCENTRATION) {
                    Feature semicircleFeature = generateSemicircleFeature(currentPair.getFirst(), currentPair.getSecond(), currentDeg);
                    addFeatureProperties(currentConcentration, semicircleFeature, simulationResult);
                    return FeatureConverter.toStringValue(simulationResult);
                }
                addFeatureProperties(currentConcentration, currentPolygonFeature, simulationResult);

                if (i == hourly3Requests.size() - 1) {
                    Feature semicircleFeature = generateSemicircleFeature(currentPair.getFirst(), currentPair.getSecond(), currentDeg);
                    addFeatureProperties(currentConcentration, semicircleFeature, simulationResult);
                }

                currentSpeed += speedStep;
                currentPair = nextPair;
            }
        }

        return FeatureConverter.toStringValue(simulationResult);
    }

    private Feature generateSemicircleFeature(LatLon first, LatLon second, double degrees) {
        double radius = GeoUtil.distance(convertLatLonToPosition(first), convertLatLonToPosition(second)) / 2;
        List<Position> positionList = calculateSemicircleLatLonList(first, second, degrees, radius).stream()
                .map(ConversionUtil::convertLatLonToPosition)
                .collect(Collectors.toList());

        List<List<Position>> polygonPositionList = new ArrayList<>();
        polygonPositionList.add(positionList);
        return new Feature(new Polygon(polygonPositionList));
    }

    private void addFeatureProperties(double concentration, Feature feature, FeatureCollection featureCollection) {
        String currentColor = ColorGradationsUtil.getColor(concentration);

        feature.setProperties(ImmutableMap.of(CONCENTRATION_PARAMETER, concentration,
                COLOR_PARAMETER, currentColor));
        featureCollection.addFeature(feature);
    }
}
