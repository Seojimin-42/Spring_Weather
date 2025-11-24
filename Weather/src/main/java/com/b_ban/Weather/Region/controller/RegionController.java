package com.b_ban.Weather.Region.controller;

import com.b_ban.Weather.Common.util.SolarTermCalculator;
import com.b_ban.Weather.Common.util.SolarTermDescription;
import com.b_ban.Weather.Region.service.RegionService;
import com.b_ban.Weather.Weather.dto.WeatherDto;
import com.b_ban.Weather.Weather.service.AirKoreaService;
import com.b_ban.Weather.Weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

// RegionController.java

@Controller // 웹 요청 컨트롤러
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final WeatherService weatherService;
    private final AirKoreaService airKoreaService;

    // 처음 접속한 사용자에게 기본 지역 날씨 보여줌
    @GetMapping("/")
    public String home(Model model) {
        String parent = "서울특별시";
        String child = "종로구";

        model.addAttribute("regionName", parent + " " + child);
        model.addAttribute("weather", weatherService.getWeather(parent, child));

        String solarTerm = SolarTermCalculator.getCurrentSolarTerm();

        model.addAttribute("solarTerm", solarTerm); // 절기 추가
        model.addAttribute("solarTermDesc", SolarTermDescription.getDescription(solarTerm)); // 절기 설명 추가

        String today = LocalDate.now().toString(); // 오늘날짜

        // 1) 고농도 PM2.5 (50 초과)
        String highPM = airKoreaService.getHighPm25Forecast(today);

        // 2) 대기질 예보 통보
        String pm25 = airKoreaService.getAirForecast(today, parent,"PM25"); // 초미세먼지(예보)
        String pm10 = airKoreaService.getAirForecast(today, parent,"PM10");  // 미세먼지(예보)
        String o3 = airKoreaService.getAirForecast(today, parent,"O3");      // 오존(예보)

        model.addAttribute("highPM", highPM); // 고농도
        model.addAttribute("pm25", pm25);     // 초미세먼지(예보)
        model.addAttribute("pm10", pm10);     // 미세먼지(예보)
        model.addAttribute("o3", o3);         // 오존(예보)

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

        String solarTerm = SolarTermCalculator.getCurrentSolarTerm();

        model.addAttribute("solarTerm", solarTerm); // 절기 추가
        model.addAttribute("solarTermDesc", SolarTermDescription.getDescription(solarTerm)); // 절기 설명 추가

        String today = LocalDate.now().toString(); // 오늘날짜

        // 1) 고농도 PM2.5 (50 초과)
        String highPM = airKoreaService.getHighPm25Forecast(today);

        // 2) 대기질 예보 통보
        String pm25 = airKoreaService.getAirForecast(today, city,"PM25"); // 초미세먼지(예보)
        String pm10 = airKoreaService.getAirForecast(today, city,"PM10");  // 미세먼지(예보)
        String o3 = airKoreaService.getAirForecast(today, city,"O3");      // 오존(예보)

        model.addAttribute("highPM", highPM); // 고농도
        model.addAttribute("pm25", pm25);     // 초미세먼지(예보)
        model.addAttribute("pm10", pm10);     // 미세먼지(예보)
        model.addAttribute("o3", o3);         // 오존(예보)

        return "list";
    }

}
