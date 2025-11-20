package com.b_ban.Weather.Weather.service;

import com.b_ban.Weather.Region.entity.Region;
import com.b_ban.Weather.Region.service.RegionService;
import com.b_ban.Weather.Weather.dto.WeatherDto;
import lombok.RequiredArgsConstructor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// WeatherService.java

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RegionService regionService;

    @Value("${weather.service-key}") // application.yml에서 API KEY 불러옴
    private String serviceKey; // application.yml에 작성한 값을 변수 serviceKey 안에 넣음

    @Value("${weather.api.url}") // application.yml에서 yml 불러옴
    private String apiUrl; // application.yml에 작성한 값을 변수 apiUrl 안에 넣음

    private final RestTemplate restTemplate = new RestTemplate(); // API 요청

    public WeatherDto getWeather(String parent, String child) {

        // DB에서 지역(구) 이름으로 nx, ny 좌표 찾기
        Region region = regionService.getRegion(parent, child);

        // 기상청 요청을 위한 날짜 + 기준시간 얻기
        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 날짜
        String baseTime = getNearestBaseTime(); // 시간 규칙 처리

        // 요청 URL 조립
        String url = apiUrl +
                "?serviceKey=" + serviceKey +
                "&pageNo=1&numOfRows=100&dataType=JSON" +
                "&base_date=" + baseDate +
                "&base_time=" + baseTime +
                "&nx=" + region.getNx() +
                "&ny=" + region.getNy();

        System.out.println("🔍 Request URL: " + url);

        // 호출
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        String json = response.getBody();
        System.out.println("📌 RAW JSON >>> " + json);
        return parseWeather(json);
    }

    // 기상청 시간 규칙: 매시각 40분 이전에는 이전 시간 조회
    private String getNearestBaseTime() {
        LocalTime now = LocalTime.now();

        // 현재 분이 30분 전이면 한 시간 전으로
        if (now.getMinute() < 30) {
            now = now.minusHours(1);
        }

        return now.format(DateTimeFormatter.ofPattern("HH00"));
    }



    // JSON 데이터 파싱 로직
    private WeatherDto parseWeather(String json) {
        JSONObject root = new JSONObject(json);
        JSONArray items = root.getJSONObject("response")
                .getJSONObject("body")
                .getJSONObject("items")
                .getJSONArray("item");

        Double temp = null, humidity = null, rain = null;
        String time = null;

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String category = item.getString("category");

            switch (category) {
                case "T1H": temp = item.getDouble("obsrValue"); break; // 온도
                case "REH": humidity = item.getDouble("obsrValue"); break; //  습도
                case "RN1": rain = item.getDouble("obsrValue"); break; // 강수량
            }
            time = item.getString("baseDate") + " " + item.getString("baseTime");
        }

        return new WeatherDto(temp, humidity, rain, time);
    }
}

