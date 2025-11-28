package com.b_ban.Weather.Region.dto;

import lombok.*;

// RegionDto.java

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionDto {

    private Long id;
    private String parentRegion;  // 시/도
    private String childRegion;   // 구/군
}
