package com.b_ban.Weather.Weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// WeatherDto.java

@Getter
@Setter
@Builder
@AllArgsConstructor
public class WeatherDto {
    private Double temperature;
    private Double humidity;
    private Double rainfall;
    private String umbrellaMessage;
    private String time; // 기상청 날씨 데이터 기준 시간(정시 데이터만 제공)
    private String requestTime; // 페이지 새로고침한 시각

    private String clothesImage; // 날씨에 따른 옷차림 이미지
    private String clothesDetail;    // 상세 문구
    private String clothesSummary;   // 한 줄 요약

}
