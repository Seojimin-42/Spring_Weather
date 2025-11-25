package com.b_ban.Weather.Weather.service;

import com.b_ban.Weather.Region.entity.Region;
import com.b_ban.Weather.Region.service.RegionService;
import com.b_ban.Weather.Weather.dto.WeatherDto;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// WeatherService.java

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final RegionService regionService;
    private final RestTemplate restTemplate;

    @Value("${api.weather.key}") // application.yml에서 API KEY 불러옴
    private String serviceKey; // application.yml에 작성한 값을 변수 serviceKey 안에 넣음

    @Value("${api.weather.url}") // application.yml에서 yml 불러옴
    private String apiUrl; // application.yml에 작성한 값을 변수 apiUrl 안에 넣음

    public WeatherDto getWeather(String parent, String child) {

        // DB에서 지역(구) 이름으로 nx, ny 좌표 찾기
        Region region = regionService.getRegion(parent, child);

        // 기상청 요청을 위한 날짜 + 기준시간 얻기
        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 날짜
        String baseTime = getNearestBaseTime(); // 시간 규칙 처리

        // 키를 여기서 직접 URL Encode
        String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);

        // 최대 3번까지 이전 시각으로 다시 시도
        for (int retry = 0; retry < 3; retry++) {

        // API 요청 URL 생성 + 인코딩
        String url = String.format(
                "%s?serviceKey=%s&pageNo=1&numOfRows=100&dataType=JSON" +
                        "&base_date=%s&base_time=%s&nx=%d&ny=%d",
                apiUrl, encodedKey, baseDate, baseTime, region.getNx(), region.getNy()
        );

        log.info("🔍 Request URL: {}", url); // 공공데이터 api에서 데이터 값 잘 받아오는 지 확인

        // API 호출 + 예외 처리
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            log.info("✅ API status: {}", response.getStatusCode());
            log.info("✅ API body: {}", response.getBody());

            String json = response.getBody();
//            System.out.println("📌 RAW JSON >>> " + json);

            // --- 여기서 먼저 header(resultCode) 확인 ---
            JSONObject root = new JSONObject(json);
            JSONObject header = root.getJSONObject("response").getJSONObject("header");
            String resultCode = header.getString("resultCode");

            if ("00".equals(resultCode)) {
                // 정상 데이터 있을 때만 파싱
                return parseWeather(json);
            }

            // NO_DATA 인 경우 → 한 시간 이전으로 이동해서 다시 시도
            if ("03".equals(resultCode)) {
                log.warn("⚠ NO_DATA, 이전 시각으로 재시도");

                DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                LocalDateTime base = LocalDateTime.parse(baseDate + baseTime, dtFmt)
                        .minusHours(1);

                baseDate = base.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                baseTime = base.format(DateTimeFormatter.ofPattern("HHmm"));
                continue;   // for문 다음 루프에서 다시 요청
            }

            // 그 외 코드면 그대로 에러 처리
            log.error("❌ API Error resultCode={}, resultMsg={}",
                    resultCode, header.optString("resultMsg"));
            break;

        } catch (HttpClientErrorException e) {
            log.error("❌ API ERROR status: {}", e.getStatusCode());
            log.error("❌ API ERROR body: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

        // 여기까지 왔다는 건 3번 다 실패한 경우
        return WeatherDto.builder()
                .temperature(null)
                .humidity(null)
                .rainfall(null)
                .time("최근 기상청 데이터 없음")
                .requestTime(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH시 mm분 ss초")))
                .build();
    }

    // 기상청 시간 규칙: 매시각 40분 이전에는 이전 시간 조회
    private String getNearestBaseTime() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        // 매시각 40분 이전은 한 시간 전 데이터 사용
        if (minute < 40) {
            hour = (hour - 1 + 24) % 24; // 0시에서 이전으로 넘어갈 때 처리
        }

        return String.format("%02d00", hour);
    }

    // JSON 데이터 파싱 -> DTO로 반환
    private WeatherDto parseWeather(String json) {
        JSONObject root = new JSONObject(json);
        JSONObject response = root.optJSONObject("response");
        if (response == null) return emptyWeather("날씨 정보 없음");

        JSONObject header = response.optJSONObject("header");
        if (header != null && !"00".equals(header.optString("resultCode"))) {
            return emptyWeather(header.optString("resultMsg"));
        }

        JSONObject body = response.optJSONObject("body");
        if (body == null) return emptyWeather("관측값 없음");

        JSONObject itemsObj = body.optJSONObject("items");
        if (itemsObj == null) return emptyWeather("항목 없음");

        JSONArray items = itemsObj.optJSONArray("item");
        if (items == null || items.isEmpty()) return emptyWeather("측정소 없음");

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

            // 시간 가공 (YYYYMMDD HHmm → LocalDateTime)
            String baseDate = item.getString("baseDate");
            String baseTime = item.getString("baseTime");

            // ex. "1400" → "14:00"으로 바꾼 뒤 LocalDateTime으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
            time = LocalDateTime.parse(baseDate + " " + baseTime, formatter)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH시 mm분"));
        }

        return WeatherDto.builder()
                .temperature(temp) // ex. Service의 temp 값을 DTO의 temperature 필드에 넣는 것, temp=5.3이면 dto에 5.3을 넣음.
                .humidity(humidity)
                .rainfall(rain)
                .time(time) // 기상청 날씨 데이터 기준 시간
                .requestTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH시 mm분 ss초")))
                .build();
    }

    // 데이터가 없을 때 안전하게 반환하는 메서드
    private WeatherDto emptyWeather(String message) {
        return WeatherDto.builder()
                .temperature(null)
                .humidity(null)
                .rainfall(null)
                .time(message)  // "NO DATA" 같은 안내 문구로 사용
                .requestTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH시 mm분 ss초")))
                .build();
    }
}