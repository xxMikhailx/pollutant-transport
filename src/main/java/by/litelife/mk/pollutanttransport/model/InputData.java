package by.litelife.mk.pollutanttransport.model;

import lombok.Data;

@Data
public class InputData {
    private double coefficientF;
    private double concentration;
    private double radius;
    private double lat;
    private double lng;
    private boolean useForecast;
}
