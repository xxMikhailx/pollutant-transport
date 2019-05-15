package by.litelife.mk.pollutanttransport.model;

public class TimeConcentrationPair {
    private int time;
    private double concentration;

    public TimeConcentrationPair() {
    }

    public TimeConcentrationPair(int time, double concentration) {
        this.time = time;
        this.concentration = concentration;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getConcentration() {
        return concentration;
    }

    public void setConcentration(double concentration) {
        this.concentration = concentration;
    }
}
