package by.litelife.mk.pollutanttransport.util;

import mil.nga.sf.geojson.Position;
import org.apache.commons.math3.analysis.interpolation.NevilleInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public final class GeoUtil {
    private GeoUtil() {

    }

    public static PolynomialFunctionLagrangeForm linearInterpolateFunction(double[] time, double[] concentration) {
        NevilleInterpolator linearInterpolator = new NevilleInterpolator();
        return linearInterpolator.interpolate(time, concentration);
    }

    /**
     * Calculate distance between two points in latitude and longitude.
     * If you are not interested in height difference pass 0.0.
     * Uses Haversine method as its base.
     * <p>
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
}
