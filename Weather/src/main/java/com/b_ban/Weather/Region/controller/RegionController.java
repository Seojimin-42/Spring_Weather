package com.b_ban.Weather.Region.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// WeatherController.java

@Controller // 웹 요청 컨트롤러
public class RegionController {
    @GetMapping("/list")
    public String index(Model model) {
        model.addAttribute("name", "날씨");
        return "list.html";
    }
}
