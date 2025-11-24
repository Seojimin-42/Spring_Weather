package com.b_ban.Weather.Weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AirKoreaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.airkorea.key}")
    private String apiKey;

    @Value("${api.airkorea.url}")
    private String baseUrl;

    // -------------------------
    // 1) 고농도 초미세먼지 (50µg 초과) 여기서, Pm25 -> Pm2.5로 초미세먼지를 말함
    // -------------------------
    public String getHighPm25Forecast(String date) {
        String url = baseUrl + "/getMinuDustFrcstDspth50Over" +
                "?serviceKey=" + apiKey +
                "&returnType=json" +
                "&searchDate=" + date;

        try {
            String json = restTemplate.getForObject(url, String.class);
            JsonNode items = objectMapper.readTree(json)
                    .path("response").path("body").path("items");

            if (items.isArray() && items.size() > 0) {
                JsonNode first = items.get(0);
                // 전국 요약 (informOverall)만 일단 사용
                return first.path("informOverall").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "고농도 초미세먼지 정보 없음";
    }

    // -------------------------
    // 2) 대기질 예보통보 (종합: PM10 / PM2.5 / O3)
    // -------------------------
    public String getAirForecast(String date, String parentRegion, String informCode) {
        // informCode 예: "PM10", "PM2.5", "O3"
        String url = baseUrl + "/getMinuDustFrcstDspth" +
                "?serviceKey=" + apiKey +
                "&returnType=json" +
                "&searchDate=" + date +
                "&informCode=" + informCode;

        try {
            String json = restTemplate.getForObject(url, String.class);
            JsonNode items = objectMapper.readTree(json)
                    .path("response").path("body").path("items");

            if (items.isArray() && items.size() > 0) {
                JsonNode first = items.get(0);
                // "서울 : 나쁨,제주 : 보통,..." 이런 문자열
                String gradeText = first.path("informGrade").asText();

                String regionKey = normalizeRegionName(parentRegion); // "서울특별시" -> "서울"
                return extractRegionGrade(gradeText, regionKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "대기질 예보 정보 없음";
    }

    // 8개의 지역 시 이름 변환 (ex. "서울특별시" -> "서울" )
    private String normalizeRegionName(String parentRegion) {
        if (parentRegion == null) return "";

        String name = parentRegion.trim();

        // 뒤에 붙는 행정 단어 제거
        name = name.replace("특별시", "")
                .replace("광역시", "")
                .replace("특별자치시", "")
                .replace("특별자치도", "")
                .replace("도", "");

        // 예외 처리 필요시 여기 추가 가능 (예: "경기" → 경기남부/북부 등)
        return name; // 예: "서울", "부산", "제주", "경기"
    }

    // -------------------------
    // "서울 : 나쁨,제주 : 보통,..." 문자열에서 원하는 지역만 추출
    // -------------------------
    private String extractRegionGrade(String gradeText, String regionKey) {
        if (gradeText == null || gradeText.isEmpty() || regionKey.isEmpty()) {
            return "정보 없음";
        }

        String[] parts = gradeText.split(",");
        for (String part : parts) {
            part = part.trim(); // "서울 : 나쁨"
            if (part.startsWith(regionKey)) {
                return part; // "서울 : 나쁨"
            }
        }

        // 경기남부/북부처럼 세분화된 경우: "경기"로 시작하는 첫 번째를 사용
        if (regionKey.equals("경기")) {
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("경기")) {
                    return part; // "경기남부 : 나쁨" 같은 것
                }
            }
        }

        return "정보 없음";
    }
}
