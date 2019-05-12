package by.litelife.mk.pollutanttransport.service;

import by.litelife.mk.pollutanttransport.model.InputData;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Geometry;
import mil.nga.sf.geojson.LineString;
import mil.nga.sf.geojson.Position;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapService {
    @Value("classpath:static/geojson/svisloch-java.geojson")
    Resource svislochRiver;


    public String simulate(InputData inputData) throws IOException {
        String svislochGeoJson = new String(Files.readAllBytes(svislochRiver.getFile().toPath()));

        FeatureCollection featureCollection = FeatureConverter.toFeatureCollection(svislochGeoJson);
        Feature feature = featureCollection.getFeatures().get(0);
        Geometry geometry = feature.getGeometry();
        LineString lineString = (LineString) geometry;

        List<Position> positionList = lineString.getCoordinates().stream()
                .limit(3)
                .collect(Collectors.toList());
        LineString lineString1 = new LineString(positionList);

        Feature feature1 = new Feature(lineString1);

        FeatureCollection featureCollection1 = new FeatureCollection(feature1);
        String featureCollectionContent = FeatureConverter.toStringValue(featureCollection1);

        return "";
    }
}
