package by.litelife.mk.pollutanttransport.client;

import by.litelife.mk.pollutanttransport.client.dto.LatLon;
import by.litelife.mk.pollutanttransport.client.dto.WeatherApiFullResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "weatherapi", url = "https://api.openweathermap.org/data/2.5/")
public interface OpenWeatherMapClient {

    @RequestMapping(method = RequestMethod.GET, value = "/forecast")
    WeatherApiFullResponse getWinds(@SpringQueryMap LatLon latLon);
}
