package com.b_ban.Weather.Region.controller;

import com.b_ban.Weather.Common.util.SolarTermCalculator;
import com.b_ban.Weather.Common.util.SolarTermDescription;
import com.b_ban.Weather.Dust.service.DustService;
import com.b_ban.Weather.Region.dto.RegionDto;
import com.b_ban.Weather.Region.service.RegionService;
import com.b_ban.Weather.Weather.dto.WeatherDto;
import com.b_ban.Weather.Dust.service.DustService;
import com.b_ban.Weather.Weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

// RegionController.java

@Controller // 웹 요청 컨트롤러
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final WeatherService weatherService;
    private final DustService dustService;

    // 처음 접속하면 검색 페이지 보여주기
    @GetMapping("/")
    public String showMain(Model model) {
        model.addAttribute("seasonBirdImage", pickSeasonBirdImage());
        return "search";
    }

    // 계절별 새 이미지 선택 함수
    private String pickSeasonBirdImage() {
        int month = LocalDate.now().getMonthValue();
        if (month == 3 || month == 4 || month == 5) return "spring_bird.png";
        if (month == 6 || month == 7 || month == 8) return "summer_bird.png";
        if (month == 9 || month == 10 || month == 11) return "fall_bird.png";
        return "winter_bird.png";
    }

    // 검색 실행 (ex. /search?q=서울)
    @GetMapping("/search")
    public String searchRegions(@RequestParam(value = "q", required = false) String keyword,
                                Model model) {

        if (keyword == null || keyword.isBlank()) {
            return "redirect:/";
        }

        List<RegionDto> results = regionService.searchRegions(keyword);
        RegionDto selectedRegion = results.isEmpty() ? null : results.get(0);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRegion", selectedRegion);
        model.addAttribute("seasonBirdImage", pickSeasonBirdImage());

        return "search";
    }

    // 검색 자동완성
    @GetMapping("/search/suggest")
    @ResponseBody
    public List<RegionDto> suggestRegions(@RequestParam("q") String keyword) {
        return regionService.searchRegions(keyword);
    }

    // 사용자가 다른 구를 선택하거나 검색했을 때 해당 구 날씨 보여줌
    @GetMapping("/region/{city}/{district}")
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

        // ) 대기질 예보 통보
        String pm25 = dustService.getAirForecast(today, city,"PM25"); // 초미세먼지(예보)
        String pm10 = dustService.getAirForecast(today, city,"PM10");  // 미세먼지(예보)
        String o3 = dustService.getAirForecast(today, city,"O3");      // 오존(예보)

        model.addAttribute("pm25", pm25);     // 초미세먼지(예보)
        model.addAttribute("pm10", pm10);     // 미세먼지(예보)
        model.addAttribute("o3", o3);         // 오존(예보)

        // 미세먼지/초미세먼지 중 하나라도 "나쁨/매우 나쁨"이면 mask.png 반환 및 미세먼지 알림
        String maskImage = dustService.getMaskImageForForecast(pm10, pm25);
        String dustSummary = dustService.buildDustSummary(pm10, pm25);

        model.addAttribute("maskImage", maskImage);
        model.addAttribute("dustSummary", dustSummary);

        return "list";
    }
}
