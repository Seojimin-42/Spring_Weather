package com.b_ban.Weather.Common.util;

import java.time.LocalDate;
import java.time.MonthDay;

// SolarTermCalculator.java

public class SolarTermCalculator {

    // 절기 이름 (기준일과 같은 순서)
    private static final String[] SOLAR_TERMS = {
            "소한", "대한",
            "입춘", "우수", "경칩", "춘분", "청명", "곡우",
            "입하", "소만", "망종", "하지", "소서", "대서",
            "입추", "처서", "백로", "추분", "한로", "상강",
            "입동", "소설", "대설", "동지"
            // 나중에 인덱스를 이용해 절기 이름을 반환하는 데 사용됨
    };

    // 각 절기의 "기준 시작일" (평년 기준, 해마다 ±1일 정도만 움직임)
    private static final MonthDay[] TERM_STARTS = {
            MonthDay.of(1, 5),   // 소한
            MonthDay.of(1, 20),  // 대한
            MonthDay.of(2, 4),   // 입춘
            MonthDay.of(2, 19),  // 우수
            MonthDay.of(3, 6),   // 경칩
            MonthDay.of(3, 21),  // 춘분
            MonthDay.of(4, 5),   // 청명
            MonthDay.of(4, 20),  // 곡우
            MonthDay.of(5, 5),   // 입하
            MonthDay.of(5, 21),  // 소만
            MonthDay.of(6, 6),   // 망종
            MonthDay.of(6, 21),  // 하지
            MonthDay.of(7, 7),   // 소서
            MonthDay.of(7, 23),  // 대서
            MonthDay.of(8, 7),   // 입추
            MonthDay.of(8, 23),  // 처서
            MonthDay.of(9, 8),   // 백로
            MonthDay.of(9, 23),  // 추분
            MonthDay.of(10, 8),  // 한로
            MonthDay.of(10, 23), // 상강
            MonthDay.of(11, 7),  // 입동
            MonthDay.of(11, 22), // 소설
            MonthDay.of(12, 7),  // 대설
            MonthDay.of(12, 22)  // 동지
    };

    public static String getCurrentSolarTerm() {
        return getSolarTerm(LocalDate.now());
    }

    public static String getSolarTerm(LocalDate date) {
        int year = date.getYear();

        // 기준일들을 올해 날짜로 변환
        String current = SOLAR_TERMS[0]; // 기본값: 소한
        for (int i = 0; i < TERM_STARTS.length; i++) {
            LocalDate start = TERM_STARTS[i].atYear(year); // 해당 월,일에 현재년도 추가
            if (!date.isBefore(start)) {   // 오늘이 절기 시작일과 같거나 이후라면
                current = SOLAR_TERMS[i]; // 현재 절기로 갱신
            } else {
                break; // 아직 시작하지 않은 절기를 만나면 반복 종료
            }
        }
        return current;
    }
}
