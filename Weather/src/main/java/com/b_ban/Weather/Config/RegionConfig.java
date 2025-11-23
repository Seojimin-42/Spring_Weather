package com.b_ban.Weather.Config;

import com.b_ban.Weather.Region.entity.Region;
import com.b_ban.Weather.Region.repository.RegionRepository;
import com.b_ban.Weather.Region.service.RegionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

// RegionConfig
// 서버 켜질 때, 지역 CSV를 읽어서 DB를 자동으로 초기화하는 코드

@Component // 스프링이 자동으로 등록하는 Bean 클래스
@RequiredArgsConstructor
public class RegionConfig {

    private final RegionRepository regionRepository;
    private final RegionService regionService;

    @PostConstruct // Bean 생성 직후 1회만 실행
    public void init(){
        if(regionRepository.count() == 0) { // DB 테이블에 데이터가 하나도 없을 때
            List<Region> regions = regionService.readCsv();
            regionRepository.saveAll(regions);
            System.out.println("지역 데이터 초기화 완료 : " + regions.size() + "건");
        }
    }
}
