package by.litelife.mk.pollutanttransport.model;

import java.util.ArrayList;
import java.util.List;

public class InputData {
    private List<TimeConcentrationPair> timeConcentrationPairs;
    private String timeConcentrationPairsJson;
    private double riverSpeed;
    private double lat;
    private double lng;

    public InputData() {
        timeConcentrationPairs = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            timeConcentrationPairs.add(new TimeConcentrationPair());
        }
    }

    public List<TimeConcentrationPair> getTimeConcentrationPairs() {
        return timeConcentrationPairs;
    }

    public void setTimeConcentrationPairs(List<TimeConcentrationPair> timeConcentrationPairs) {
        this.timeConcentrationPairs = timeConcentrationPairs;
    }

    public String getTimeConcentrationPairsJson() {
        return timeConcentrationPairsJson;
    }

    public void setTimeConcentrationPairsJson(String timeConcentrationPairsJson) {
        this.timeConcentrationPairsJson = timeConcentrationPairsJson;
    }

    public double getRiverSpeed() {
        return riverSpeed;
    }

    public void setRiverSpeed(double riverSpeed) {
        this.riverSpeed = riverSpeed;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
