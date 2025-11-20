package com.b_ban.Weather.Region.controller;

import com.b_ban.Weather.Region.service.RegionService;
import com.b_ban.Weather.Weather.dto.WeatherDto;
import com.b_ban.Weather.Weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// WeatherController.java

@Controller // 웹 요청 컨트롤러
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final WeatherService weatherService;

    // 처음 접속한 사용자에게 기본 지역 날씨 보여줌
    @GetMapping("/")
    public String home(Model model) {
        String parent = "서울특별시";
        String child = "종로구";

        model.addAttribute("regionName", parent + " " + child);
        model.addAttribute("weather", weatherService.getWeather(parent, child));

        return "list";
    }

    // 사용자가 다른 구를 선택하거나 검색했을 때 해당 구 날씨 보여줌
    @GetMapping("/{city}/{district}")
    public String getRegionWeather( @PathVariable String city,
                                    @PathVariable String district,
                                    Model model) {

        String fullName = city + " " + district;
        WeatherDto weather = weatherService.getWeather(city, district);

        model.addAttribute("regionName", fullName);
        model.addAttribute("weather", weather);

        return "list";
    }
}
