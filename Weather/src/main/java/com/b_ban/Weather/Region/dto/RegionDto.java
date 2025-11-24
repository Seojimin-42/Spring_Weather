package com.b_ban.Weather.Region.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionDto {
    private double pm25; // 초미세먼지(예보)
    private double pm10; // 미세먼지(예보)
    private double o3; // 오존(예보)
}
