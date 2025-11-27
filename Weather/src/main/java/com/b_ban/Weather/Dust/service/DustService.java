package com.b_ban.Weather.Dust.service;

import com.b_ban.Weather.Dust.dto.DustDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

// DustService.java

@Service
@RequiredArgsConstructor
public class DustService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // application.yml
    @Value("${api.airkorea.key}")
    private String apiKey;

    @Value("${api.airkorea.url}")
    private String baseUrl;

    // -------------------------
    // 대기질 예보통보 (종합: PM10 / PM2.5 / O3)
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
            JsonNode root = objectMapper.readTree(json);
            JsonNode header = root.path("response").path("header");
            String resultCode = header.path("resultCode").asText();
            String resultMsg = header.path("resultMsg").asText();

            JsonNode body  = root.path("response").path("body");
            JsonNode items = body.path("items");

            boolean hasData =
                    ("00".equals(resultCode) || "200".equals(resultCode))
                            && items.isArray()
                            && items.size() > 0;

            //  오늘 날짜인데 데이터가 없으면 -> 어제로 한 번 더 재시도
            if (!hasData) {
                LocalDate d = LocalDate.parse(date);
                if (d.isEqual(LocalDate.now())) {
                    String yesterday = d.minusDays(1).toString();
                    return getAirForecast(yesterday, parentRegion, informCode);
                }
                // 오늘도 아니고, 어제도 아니면 그냥 "예보 없음"
                return "대기질 예보 정보 없음";
            }

            // 여기까지 왔으면 items 에 최소 1개는 있음
            JsonNode first = items.get(0);
            // "서울 : 나쁨,제주 : 보통,..." 이런 문자열
            String gradeText = first.path("informGrade").asText();

            String regionKey = normalizeRegionName(parentRegion); // "서울특별시" -> "서울"
            return extractRegionGrade(gradeText, regionKey);

        } catch (Exception e) {
            e.printStackTrace();
            return "대기질 예보 정보 없음";
        }
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

    private String pickMaskImage(String dustMessage) {
        if (dustMessage == null) return null;

        // "나쁨", "매우 나쁨" 둘 다 '나쁨'이라는 글자가 포함되니까 이렇게 체크하면 둘 다 걸림
        if (dustMessage.contains("나쁨")) {
            return "mask.png";
        }

        // 좋음/보통일 때는 마스크 X
        return null;
    }

    public DustDto toDustDto(String date, String parentRegion) {

        // PM10 / PM2.5 예보 문자열 가져오기
        String pm10Grade  = getAirForecast(date, parentRegion, "PM10");   // 예: "서울 : 나쁨"
        String pm25Grade  = getAirForecast(date, parentRegion, "PM2.5");  // 예: "서울 : 보통"

        // 화면에 보여줄 문구 (취향껏 바꿔도 됨)
        String message = pm10Grade + " / " + pm25Grade;

        // 둘 중 하나라도 "나쁨"이 포함돼 있으면 mask.png
        String combined = pm10Grade + " " + pm25Grade;
        String maskImage = pickMaskImage(combined);

        return DustDto.builder()
                .dustMessage(message)
                .maskImage(maskImage)
                .build();
    }

    private String buildDustMessage(Double pm10, Double pm25) {
        // 아주 간단 버전 (원하면 더 세밀하게 나눌 수 있음)
        if (pm10 == null && pm25 == null) return "미세먼지 정보 없음";

        boolean badPm10 = pm10 != null && pm10 >= 81;
        boolean badPm25 = pm25 != null && pm25 >= 36;

        if (badPm10 || badPm25) return "미세먼지가 나쁨 이상이에요. 마스크를 챙기는 게 좋아요!";
        return "공기 상태가 나쁘지 않아요.";
    }

    private String pickMaskImage(Double pm10, Double pm25) {
        if (pm10 == null && pm25 == null) return null;

        boolean badPm10 = pm10 != null && pm10 >= 81;  // 나쁨 기준
        boolean badPm25 = pm25 != null && pm25 >= 36;  // 나쁨 기준

        if (badPm10 || badPm25) {
            return "mask.png"; // /images/bring/mask.png
        }
        return null;
    }

    public String getMaskImageForForecast(String pm10Grade, String pm25Grade) {
        // null 방지용으로 안전하게 문자열 합치기
        String combined =
                (pm10Grade == null ? "" : pm10Grade) + " " +
                        (pm25Grade == null ? "" : pm25Grade);

        return pickMaskImage(combined);  // "나쁨" 들어 있으면 mask.png, 아니면 null
    }

    public String buildDustSummary(String pm10Grade, String pm25Grade) {
        String dustSummary;

        boolean bad =
                (pm10Grade != null && pm10Grade.contains("나쁨")) ||
                        (pm25Grade != null && pm25Grade.contains("나쁨"));

        if (bad) {
            dustSummary = "(초)미세먼지가 나쁨 이상이에요. 마스크를 착용하는 게 좋아요.";
        } else {
            dustSummary = "공기 상태는 나쁘지 않아요.";
        }

        return dustSummary;
    }
}
