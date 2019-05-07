package by.litelife.mk.pollutanttransport;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

    @GetMapping(value = "/")
    public ModelAndView main(Model model) {
        return new ModelAndView("index");
    }

}
