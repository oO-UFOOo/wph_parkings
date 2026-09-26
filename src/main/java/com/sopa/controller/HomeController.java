package com.sopa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard.html";
    }
}
