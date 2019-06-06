package by.litelife.mk.pollutanttransport.service;

import by.litelife.mk.pollutanttransport.model.InputData;
import by.litelife.mk.pollutanttransport.model.TimeConcentrationPair;
import by.litelife.mk.pollutanttransport.model.XYDto;
import by.litelife.mk.pollutanttransport.util.ColorGradationsUtil;
import by.litelife.mk.pollutanttransport.util.GeoUtil;
import com.google.common.collect.ImmutableMap;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Geometry;
import mil.nga.sf.geojson.LineString;
import mil.nga.sf.geojson.Position;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapService.class);
    private static final String CONCENTRATION_PARAMETER = "concentration";
    private static final String COLOR_PARAMETER = "color";
    private static final double CONCENTRATION_MIN = 0;
    private static final double CONCENTRATION_MAX = 1;

    @Value("classpath:static/geojson/svisloch-java.geojson")
    Resource svislochRiver;

    public RedirectAttributes simulate(InputData inputData, RedirectAttributes redirectAttributes) throws IOException {
        List<TimeConcentrationPair> timeConcentrationPairList = inputData.getTimeConcentrationPairs().stream()
                .sorted(Comparator.comparingDouble(TimeConcentrationPair::getTime))
                .collect(Collectors.toList());

        List<Double> timeValues = timeConcentrationPairList.stream()
                .map(TimeConcentrationPair::getTime)
                .collect(Collectors.toList());
        List<Double> concentrationValues = timeConcentrationPairList.stream()
                .map(TimeConcentrationPair::getConcentration)
                .map(c -> c / 100)
                .collect(Collectors.toList());

        calculateZeroPointsIfNeeded(timeValues, concentrationValues);

        double[] timeValuesArray = timeValues.stream()
                .mapToDouble(v -> v)
                .toArray();
        double[] concentrationValuesArray = concentrationValues.stream()
                .mapToDouble(v -> v)
                .toArray();

        PolynomialFunctionLagrangeForm interpolateFunction = GeoUtil.linearInterpolateFunction(timeValuesArray,
                concentrationValuesArray);
        FeatureCollection simulationResult = new FeatureCollection();
        List<XYDto> resultTimeConcentrationPairs = new ArrayList<>();

        ArrayList<Position> positionList = new ArrayList<>(extractPositionList(svislochRiver));
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

            interpolateFunction = getInterpolationFunctionWithinCurrentRange(timeValuesArray, concentrationValuesArray,
                    interpolateFunction, timeInMins);

            currentConcentration = interpolateFunction.value(timeInMins);

            if (currentConcentration < CONCENTRATION_MIN) {
                LOGGER.warn("Concentration {} is less than 0.", currentConcentration);
                addFeatureProperties(CONCENTRATION_MIN, currentFeature, simulationResult);
                continue;

            } else if (currentConcentration > CONCENTRATION_MAX) {
                LOGGER.warn("Concentration {} is greater than 1.", currentConcentration);
                addFeatureProperties(CONCENTRATION_MAX, currentFeature, simulationResult);
                continue;
            }

            resultTimeConcentrationPairs.add(new XYDto(timeInMins, currentConcentration));
            addFeatureProperties(currentConcentration, currentFeature, simulationResult);
            i++;
        }

        List<Double> chartXValues = new ArrayList<>();
        List<Double> chartYValues = new ArrayList<>();
        double[] chartTimeValuesArray = resultTimeConcentrationPairs.stream()
                .mapToDouble(XYDto::getX)
                .toArray();
        double[] chartConcentrationValuesArray = resultTimeConcentrationPairs.stream()
                .mapToDouble(XYDto::getY)
                .toArray();
        PolynomialSplineFunction chartInterpolateFunction = new LoessInterpolator().interpolate(chartTimeValuesArray,
                chartConcentrationValuesArray);

        double maxChartTimeValue = Arrays.stream(chartTimeValuesArray)
                .max()
                .getAsDouble();
        double minChartTimeValue = Arrays.stream(chartTimeValuesArray)
                .min()
                .getAsDouble();
        for (double j = minChartTimeValue; j <= maxChartTimeValue; j++) {
            chartXValues.add(j);
            chartYValues.add(chartInterpolateFunction.value(j) * 100);
        }

        redirectAttributes.addFlashAttribute("chartXValues", chartXValues);
        redirectAttributes.addFlashAttribute("chartYValues", chartYValues);
        redirectAttributes.addFlashAttribute("simulatedGeojson", FeatureConverter.toStringValue(simulationResult));
        return redirectAttributes;
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

    private void calculateZeroPointsIfNeeded(List<Double> timeValues, List<Double> concentrationValues) {
        if (timeValues.get(0) != 0.0) {
            double zeroTimeConcentrationValue = zeroValueCalculation(timeValues, concentrationValues);

            if (zeroTimeConcentrationValue > 1) {
                zeroTimeConcentrationValue = 1;
            }

            timeValues.add(0, 0.0);
            concentrationValues.add(0, zeroTimeConcentrationValue);
        }

        if (!concentrationValues.contains(0.0)) {
            List<Double> copyTimeValues = new ArrayList<>(timeValues);
            List<Double> copyConcentrationValues = new ArrayList<>(concentrationValues);
            Collections.reverse(copyTimeValues);
            Collections.reverse(copyConcentrationValues);

            double zeroConcentrationTimeValue = zeroValueCalculation(copyConcentrationValues, copyTimeValues);

            concentrationValues.add(0.0);
            timeValues.add(zeroConcentrationTimeValue);
        }
    }

    private double zeroValueCalculation(List<Double> zeroPointList, List<Double> calculationList) {
        double[] twoValuesFromBeginZeroPointList = zeroPointList.stream()
                .mapToDouble(v -> v)
                .limit(2)
                .toArray();
        double[] twoValuesFromBeginCalculationList = calculationList.stream()
                .mapToDouble(v -> v)
                .limit(2)
                .toArray();
        return GeoUtil.linearInterpolateFunction(twoValuesFromBeginZeroPointList,
                twoValuesFromBeginCalculationList).value(0.0);
    }

    private PolynomialFunctionLagrangeForm getInterpolationFunctionWithinCurrentRange(double[] timeValuesArray, double[] concentrationValuesArray,
                                                                                      PolynomialFunctionLagrangeForm interpolateFunction, double timeInMins) {
        for (int j = 0; j < timeValuesArray.length - 1; j++) {
            double currentTimeValue = timeValuesArray[j];
            double currentConcentrationValue = concentrationValuesArray[j];
            double nextTimeValue = timeValuesArray[j + 1];
            double nextConcentrationValue = concentrationValuesArray[j + 1];

            if (timeInMins >= currentTimeValue && timeInMins <= nextTimeValue) {
                double[] currentTimeRange = {currentTimeValue, nextTimeValue};
                double[] currentConcentrationRange = {currentConcentrationValue, nextConcentrationValue};
                interpolateFunction = GeoUtil.linearInterpolateFunction(currentTimeRange,
                        currentConcentrationRange);
            }
        }
        return interpolateFunction;
    }
}
