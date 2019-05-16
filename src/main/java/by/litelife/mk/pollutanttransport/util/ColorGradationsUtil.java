package by.litelife.mk.pollutanttransport.util;

import org.apache.commons.lang.math.DoubleRange;

import java.util.HashMap;
import java.util.Map;

public final class ColorGradationsUtil {
    private static final Map<DoubleRange, String> concentrationColorMap = new HashMap<>();

    static {
        concentrationColorMap.put(new DoubleRange(0.0, 0.125), "#00C711");
        concentrationColorMap.put(new DoubleRange(0.125, 0.25), "#22AE12");
        concentrationColorMap.put(new DoubleRange(0.25, 0.375), "#459514");
        concentrationColorMap.put(new DoubleRange(0.375, 0.5), "#677C16");
        concentrationColorMap.put(new DoubleRange(0.5, 0.625), "#8A6317");
        concentrationColorMap.put(new DoubleRange(0.625, 0.75), "#AC4A19");
        concentrationColorMap.put(new DoubleRange(0.75, 0.875), "#CF311B");
        concentrationColorMap.put(new DoubleRange(0.875, 1.0), "#F2181D");
    }

    private ColorGradationsUtil() {

    }

    public static String getColor(double concentration) {
        for (Map.Entry<DoubleRange, String> entry : concentrationColorMap.entrySet()) {
            if (entry.getKey().containsDouble(concentration)) {
                return entry.getValue();
            }
        }
        throw new RuntimeException(String.format("Concentration %f is not in scope of 0 to 1.", concentration));
    }
}
