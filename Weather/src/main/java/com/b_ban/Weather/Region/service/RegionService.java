package com.b_ban.Weather.Region.service;

import com.b_ban.Weather.Region.dto.RegionDto;
import com.b_ban.Weather.Region.entity.Region;
import com.b_ban.Weather.Region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// RegionService.java

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    // CSV 파일에서 지역 데이터를 읽어 List<Region>으로 만들어주는 서비스
    public List<Region> readCsv(){
        List<Region> regions = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(
                // src/main/resources 경로 안에 파일 읽음
                getClass().getResourceAsStream("/storage/init/regionList.csv"), "UTF-8")
        )) {
            // try 내용
            String line;
            boolean header = true;

            while((line = br.readLine()) != null) {

                if(header) { // 첫 줄 헤더 건너뛰기
                    header = false;
                    continue;
                }
                
                // 콤마로 분리해서 값 추출
                String[] tokens = line.split(",");

                // Region 객체 생성 후, 값 채우기
                Region region = new Region();
                region.setParentRegion(tokens[1]); // ex. 서울특별시
                region.setChildRegion(tokens[2]);  // ex. 종로구
                region.setNx(Integer.parseInt(tokens[3])); // ex. 60
                region.setNy(Integer.parseInt(tokens[4])); // ex. 127

                // 리스트에 추가
                regions.add(region);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regions;
    }

    // 지역 좌표 조회 메서드
    public Region getRegion(String parent, String child) {
        return regionRepository.findByParentRegionAndChildRegion(parent, child)
                .orElseThrow(() -> new IllegalArgumentException("해당 지역이 없습니다."));
    }

    // 검색용 메서드 추가
    public List<RegionDto> searchRegions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        String k = keyword.trim();
        String[] parts = k.split("\\s+");

        List<Region> regions;

        if (parts.length >= 2) {
            // "인천 서구", "서울 중구" 처럼 띄어쓰기 2개 이상 입력한 경우
            String city = parts[0];
            String district = parts[1];

            // 1차: 시+구 모두 포함하는 애들만 AND 검색
            regions = regionRepository
                    .findByParentRegionContainsAndChildRegionContains(city, district);

            // 혹시 결과가 0개면 → 그냥 전체 문자열로 OR 검색으로 fallback
            if (regions.isEmpty()) {
                regions = regionRepository
                        .findByParentRegionContainsOrChildRegionContains(k, k);
            }

        } else {
            // "서울", "서구" 같이 한 단어만 입력한 경우
            regions = regionRepository
                    .findByParentRegionContainsOrChildRegionContains(k, k);
        }

        return regions.stream()
                .map(r -> RegionDto.builder()
                        .id(r.getId())
                        .parentRegion(r.getParentRegion())
                        .childRegion(r.getChildRegion())
                        .build())
                .toList();
    }
}
