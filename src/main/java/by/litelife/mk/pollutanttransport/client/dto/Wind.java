package by.litelife.mk.pollutanttransport.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Wind {
    @JsonProperty("speed")
    private Double windSpeed;
    @JsonProperty("deg")
    private Double windDirection;
}
