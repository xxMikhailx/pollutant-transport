package by.litelife.mk.pollutanttransport.util;

import org.apache.commons.lang.math.DoubleRange;

import java.util.HashMap;
import java.util.Map;

public final class ColorGradationsUtil {
    private static final Map<DoubleRange, String> concentrationColorMap = new HashMap<>();

    static {
        concentrationColorMap.put(new DoubleRange(0.00001, 0.0000001), "#00C711");
        concentrationColorMap.put(new DoubleRange(0.00004, 0.0001), "#22AE12");
        concentrationColorMap.put(new DoubleRange(0.0001, 0.0003), "#459514");
        concentrationColorMap.put(new DoubleRange(0.0003, 0.0008), "#677C16");
        concentrationColorMap.put(new DoubleRange(0.0005, 0.001), "#8A6317");
        concentrationColorMap.put(new DoubleRange(0.001, 0.005), "#AC4A19");
        concentrationColorMap.put(new DoubleRange(0.005, 0.1), "#CF311B");
        concentrationColorMap.put(new DoubleRange(0.1, 5), "#F2181D");
    }

    private ColorGradationsUtil() {

    }

    public static String getColor(double concentration) {
        for (Map.Entry<DoubleRange, String> entry : concentrationColorMap.entrySet()) {
            if (entry.getKey().containsDouble(concentration)) {
                return entry.getValue();
            }
        }
        throw new RuntimeException(String.format("Concentration %f is not in scope of 0.0000001 to 5.", concentration));
    }
}
