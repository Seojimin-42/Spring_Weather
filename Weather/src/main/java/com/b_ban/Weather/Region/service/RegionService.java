package com.b_ban.Weather.Region.service;

import com.b_ban.Weather.Region.entity.Region;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// RegionService.java

@Service
public class RegionService {

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
}
