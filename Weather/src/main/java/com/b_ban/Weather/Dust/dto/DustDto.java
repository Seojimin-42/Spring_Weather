package com.b_ban.Weather.Dust.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// DustDto.java

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DustDto {
    private double pm25; // 초미세먼지(예보)
    private double pm10; // 미세먼지(예보)
    private double o3; // 오존(예보)

    private String dustMessage; // (초)미세먼지 설명
    private String maskImage; // (초)미세먼지 이미지
}
