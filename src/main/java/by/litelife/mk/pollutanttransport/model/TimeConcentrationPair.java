package by.litelife.mk.pollutanttransport.model;

public class TimeConcentrationPair {
    private double time;
    private double concentration;

    public TimeConcentrationPair() {
    }

    public TimeConcentrationPair(double time, double concentration) {
        this.time = time;
        this.concentration = concentration;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getConcentration() {
        return concentration;
    }

    public void setConcentration(double concentration) {
        this.concentration = concentration;
    }
}
