package org.ucair.web;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping({ "/test" })
    public String test(final Map<String, Object> model) {
        return "test";
    }
}