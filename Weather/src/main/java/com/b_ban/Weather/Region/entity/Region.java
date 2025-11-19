package com.b_ban.Weather.Region.entity;

import jakarta.persistence.*;
import lombok.*;

// Region.java

@Entity // 테이블 생성하기
@Table(name = "region")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String parentRegion;
    private String childRegion;
    private int nx;
    private int ny;

}

