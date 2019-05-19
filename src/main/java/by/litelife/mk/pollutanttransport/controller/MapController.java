package by.litelife.mk.pollutanttransport.controller;

import by.litelife.mk.pollutanttransport.client.OpenWeatherMapClient;
import by.litelife.mk.pollutanttransport.client.dto.LatLon;
import by.litelife.mk.pollutanttransport.client.dto.WeatherApiFullRequest;
import by.litelife.mk.pollutanttransport.model.InputData;
import by.litelife.mk.pollutanttransport.service.MapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class MapController {

    @Autowired
    private OpenWeatherMapClient openWeatherMapClient;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MapService mapService;

    @GetMapping(value = "/")
    public ModelAndView main(Model model) {
        if (!model.containsAttribute("data")) {
            InputData inputData = new InputData();
            inputData.setCoefficientF(1);
            inputData.setConcentration(1);
            inputData.setRadius(1);
            model.addAttribute("data", inputData);
        }

        return new ModelAndView("index");
    }

    @PostMapping(value = "/simulate")
    public String simulate(RedirectAttributes redirectAttributes, @ModelAttribute("data") InputData inputData)
            throws IOException {
        String simulatedGeoJson = mapService.simulate(inputData);

        redirectAttributes.addFlashAttribute("simulatedGeojson", simulatedGeoJson);
        redirectAttributes.addFlashAttribute("data", inputData);
        return "redirect:/";
    }

    @GetMapping(value = "/test")
    @ResponseBody
    public ResponseEntity<WeatherApiFullRequest> main() {
        return new ResponseEntity<>(openWeatherMapClient.getWinds(new LatLon(53.895261, 27.554336)), HttpStatus.OK);
    }
}
