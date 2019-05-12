package by.litelife.mk.pollutanttransport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StaticController {
    @GetMapping(value = "/about-app")
    public ModelAndView about(Model model) {
        return new ModelAndView("static/about-application");
    }
}
