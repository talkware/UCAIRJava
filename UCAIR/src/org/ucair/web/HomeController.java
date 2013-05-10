package org.ucair.web;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping({ "/" })
    public String test(final Map<String, Object> model) {
        return "redirect:/search";
    }
}