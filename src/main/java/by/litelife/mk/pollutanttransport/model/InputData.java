package by.litelife.mk.pollutanttransport.model;

public class InputData {
    private double coefficientF;
    private double concentration;
    private double radius;
    private double lat;
    private double lng;

    public InputData() {
    }

    public double getCoefficientF() {
        return coefficientF;
    }

    public void setCoefficientF(double coefficientF) {
        this.coefficientF = coefficientF;
    }

    public double getConcentration() {
        return concentration;
    }

    public void setConcentration(double concentration) {
        this.concentration = concentration;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
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
