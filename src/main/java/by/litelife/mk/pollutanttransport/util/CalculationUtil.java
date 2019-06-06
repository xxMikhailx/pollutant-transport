package by.litelife.mk.pollutanttransport.util;

import by.litelife.mk.pollutanttransport.client.dto.LatLon;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public final class CalculationUtil {
    private static final double COEFFICIENT_F_THRESHOLD = 1.5;
    private static final double MAX_CONCENTRATION_DISTANCE = 20;
    private static final double DEVIATION_VALUE = 5;

    private CalculationUtil() {

    }

    public static double calculateConcentration(double distance, double coefficientF, double maxConcentration) {
        double distanceRelation = distance / MAX_CONCENTRATION_DISTANCE;

        if (distanceRelation <= 8) {
            return -1;
        }

        if (coefficientF <= COEFFICIENT_F_THRESHOLD) {
            double mainCoefficient = distanceRelation /
                    ((3.58 * Math.pow(distanceRelation, 2)) - (35.2 * distanceRelation) + 120);
            return mainCoefficient * maxConcentration;
        } else {
            double mainCoefficient = 1 /
                    ((0.1 * Math.pow(distanceRelation, 2)) + (2.47 * distanceRelation) - 17.8);
            return mainCoefficient * maxConcentration;
        }
    }

    public static Pair<LatLon, LatLon> calculateNextLatLonPair(Pair<LatLon, LatLon> currentLatLonPair, double degrees, double distance) {
        LatLon leftLatLon = calculateLatLonByDegreesAndDistance(currentLatLonPair.getFirst(), degrees + DEVIATION_VALUE, distance);
        LatLon rightLatLon = calculateLatLonByDegreesAndDistance(currentLatLonPair.getSecond(), degrees - DEVIATION_VALUE, distance);
        return Pair.create(leftLatLon, rightLatLon);
    }

    public static Pair<LatLon, LatLon> calculateCircleLatLonPair(LatLon centralLatLon, double degrees, double radius) {
        LatLon leftLatLon = calculateLatLonByDegreesAndDistance(centralLatLon, degrees + 90, radius);
        LatLon rightLatLon = calculateLatLonByDegreesAndDistance(centralLatLon, degrees - 90, radius);
        return Pair.create(leftLatLon, rightLatLon);
    }

    public static List<LatLon> calculateSemicircleLatLonList(LatLon first, LatLon second, double degrees, double radius) {
        double initialDegrees = calculateNextDegreesDirection(degrees, -90);
        LatLon centralLatLon = calculateCentralLatLonByBetweenTwoPoints(first, second);
        List<LatLon> semicircleLatLonList = new ArrayList<>();

        for (double i = 5; i < 175; i++) {
            semicircleLatLonList.add(calculateLatLonByDegreesAndDistance(centralLatLon, initialDegrees + i, radius));
        }

        semicircleLatLonList.add(first);
        semicircleLatLonList.add(second);
        return semicircleLatLonList;
    }

    public static LatLon calculateCentralLatLonByBetweenTwoPoints(LatLon first, LatLon second) {
        LatLon centralPoint = new LatLon();
        centralPoint.setLat((first.getLat() + second.getLat()) / 2);
        centralPoint.setLon((first.getLon() + second.getLon()) / 2);
        return centralPoint;
    }

    private static LatLon calculateLatLonByDegreesAndDistance(LatLon point, double degrees, double distance) {
        double radiansDegrees = Math.toRadians(degrees);

        double lonCos = Math.cos(radiansDegrees);
        double latSin = Math.sin(radiansDegrees);

        double latCoefficient = 111134.85555555555555555555555556 - 559.822 * Math.cos(Math.toRadians(point.getLat()) * 2) + 1.175 * Math.cos(Math.toRadians(point.getLat()) * 4);
        double lonCoefficient = 111134.85555555555555555555555556 * Math.cos(Math.toRadians(point.getLat()));

        double lonRadius = distance / lonCoefficient;
        double latRadius = distance / latCoefficient;

        double lonResult = lonRadius * lonCos + point.getLon();
        double latResult = latRadius * latSin + point.getLat();

        return new LatLon(latResult, lonResult);
    }
}
