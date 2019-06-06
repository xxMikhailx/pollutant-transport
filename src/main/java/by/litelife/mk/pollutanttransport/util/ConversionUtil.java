package by.litelife.mk.pollutanttransport.util;

import by.litelife.mk.pollutanttransport.client.dto.LatLon;
import mil.nga.sf.geojson.Position;

public final class ConversionUtil {

    private ConversionUtil() {

    }

    public static Position convertLatLonToPosition(LatLon latLon) {
        return new Position(latLon.getLon(), latLon.getLat());
    }
}
