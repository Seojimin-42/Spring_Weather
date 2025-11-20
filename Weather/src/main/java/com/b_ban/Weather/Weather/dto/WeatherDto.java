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
    private double temperature;
    private double humidity;
    private double rainfall;
    private String time;
}
