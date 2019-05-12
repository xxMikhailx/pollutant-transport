package by.litelife.mk.pollutanttransport;

import by.litelife.mk.pollutanttransport.model.InputData;
import by.litelife.mk.pollutanttransport.util.GenerateSimpleGeojson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

    @GetMapping(value = "/")
    public ModelAndView main(Model model) {
        model.addAttribute("data", new InputData());
        return new ModelAndView("index");
    }

    @GetMapping(value = "/about-app")
    public ModelAndView about(Model model) {
        return new ModelAndView("static/about-application");
    }

    @PostMapping(value = "/simulate")
    public String simulate(Model model, @ModelAttribute("data") InputData inputData) {
        model.addAttribute("simulatedGeojson", GenerateSimpleGeojson.generateGeojson());
        model.addAttribute("inputData", inputData);
        return "index";
    }
}
