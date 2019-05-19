package by.litelife.mk.pollutanttransport.util;

import by.litelife.mk.pollutanttransport.client.dto.LatLon;
import org.springframework.data.util.Pair;

public final class CalculationUtil {
    private static final double COEFFICIENT_F_THRESHOLD = 1.5;
    private static final double MAX_CONCENTRATION_DISTANCE = 20;
    private static final double MAX_CIRCLE_DEGREE = 360;
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

    public static double calculateShortestDegreePath(double currentWindDegree, double nextWindDegree) {
        double firstDifference = nextWindDegree - currentWindDegree;
        if (firstDifference == 0) {
            return 0;
        }

        double secondDifference = MAX_CIRCLE_DEGREE - Math.abs(firstDifference);

        if (Math.abs(firstDifference) <= Math.abs(secondDifference)) {
            return firstDifference;
        } else if (firstDifference < 0) {
            return Math.abs(secondDifference);
        } else {
            return -secondDifference;
        }
    }

    public static double calculateNextDegreesDirection(double currentDegrees, double degreesStep) {
        currentDegrees += degreesStep;

        if (currentDegrees < 0) {
            currentDegrees += MAX_CIRCLE_DEGREE;
        }
        if (currentDegrees >= MAX_CIRCLE_DEGREE) {
            currentDegrees -= MAX_CIRCLE_DEGREE;
        }

        return currentDegrees;
    }

    public static Pair<LatLon, LatLon> calculateNextLatLonPair(Pair<LatLon, LatLon> currentLatLonPair, double degrees, double distance) {
        LatLon leftLatLon = calculateLatLonByDegreesAndDistance(currentLatLonPair.getFirst(), degrees + DEVIATION_VALUE, distance);
        LatLon rightLatLon = calculateLatLonByDegreesAndDistance(currentLatLonPair.getSecond(), degrees - DEVIATION_VALUE, distance);
        return Pair.of(leftLatLon, rightLatLon);
    }

    public static Pair<LatLon, LatLon> calculateCircleLatLonPair(LatLon centralLatLon, double degrees, double radius) {
        LatLon leftLatLon = calculateLatLonByDegreesAndDistance(centralLatLon, degrees + 90, radius);
        LatLon rightLatLon = calculateLatLonByDegreesAndDistance(centralLatLon, degrees - 90, radius);
        return Pair.of(leftLatLon, rightLatLon);
    }

    private static LatLon calculateLatLonByDegreesAndDistance(LatLon point, double degrees, double distance) {
        double radiansDegrees = Math.toRadians(degrees);

        double lonCos = Math.cos(radiansDegrees);
        double latSin = Math.sin(radiansDegrees);

        double latCoefficient = 111111;
        double lonCoefficient = 111111 * Math.cos(Math.toRadians(point.getLat()));

        double lonRadius = distance / lonCoefficient;
        double latRadius = distance / latCoefficient;

        double lonResult = lonRadius * lonCos + point.getLon();
        double latResult = latRadius * latSin + point.getLat();

        return new LatLon(latResult, lonResult);
    }
}
