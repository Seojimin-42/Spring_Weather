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

    private String time; // 기상청 날씨 데이터 기준 시간(정시 데이터만 제공)
    private String requestTime; // 페이지 새로고침한 시각


}
