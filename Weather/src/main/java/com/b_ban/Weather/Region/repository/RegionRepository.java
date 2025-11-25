package com.b_ban.Weather.Region.repository;

import com.b_ban.Weather.Region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// RegionRepository.java

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByParentRegionAndChildRegion(String parentRegion, String childRegion); // 조회 기능 추가

    // parentRegion LIKE '%서울%' OR childRegion LIKE '%서울%'
    List<Region> findByParentRegionContainsOrChildRegionContains(String parentKeyword, String childKeyword);
}
