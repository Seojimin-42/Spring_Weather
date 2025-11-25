package com.b_ban.Weather.Region.dto;

import lombok.*;

// RegionDto.java

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionDto {
    private double pm25; // 초미세먼지(예보)
    private double pm10; // 미세먼지(예보)
    private double o3; // 오존(예보)

    private Long id;
    private String parentRegion;  // 시/도
    private String childRegion;   // 구/군
}
