package by.litelife.mk.pollutanttransport.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WeatherApi3HourlyResponse {
    @JsonProperty("dt_txt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    @JsonProperty("wind")
    private Wind wind;
}
