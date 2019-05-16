package by.litelife.mk.pollutanttransport;

import by.litelife.mk.pollutanttransport.model.InputData;
import by.litelife.mk.pollutanttransport.model.TimeConcentrationPair;
import by.litelife.mk.pollutanttransport.util.GenerateSimpleGeojson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @GetMapping(value = "/")
    public ModelAndView main(Model model) {
        if (!model.containsAttribute("data")) {
            InputData inputData = new InputData(generateTimeConcentrationPairList());
            inputData.setRiverSpeed(0.1);
            model.addAttribute("data", inputData);
        }

        return new ModelAndView("index");
    }

    @GetMapping(value = "/about-app")
    public ModelAndView about(Model model) {
        return new ModelAndView("static/about-application");
    }

    @PostMapping(value = "/simulate")
    public String simulate(RedirectAttributes redirectAttributes, @ModelAttribute("data") InputData inputData) {
        redirectAttributes.addFlashAttribute("simulatedGeojson", GenerateSimpleGeojson.generateGeojson());
        redirectAttributes.addFlashAttribute("data", inputData);
        return "redirect:/";
    }

    private List<TimeConcentrationPair> generateTimeConcentrationPairList() {
        List<TimeConcentrationPair> timeConcentrationPairs = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            timeConcentrationPairs.add(new TimeConcentrationPair());
        }

        return timeConcentrationPairs;
    }
}
