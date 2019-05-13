package by.litelife.mk.pollutanttransport.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiFullRequest {
    @JsonProperty("list")
    private List<WeatherApiDailyRequest> dailyRequests;
}
