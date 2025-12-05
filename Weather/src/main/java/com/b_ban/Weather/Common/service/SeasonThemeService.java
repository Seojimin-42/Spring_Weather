package com.b_ban.Weather.Common.service;

import org.springframework.stereotype.Service;

// SeasonThemeService.java
@Service
public class SeasonThemeService {
    // 계절별 새 이미지 선택
    public String pickSeasonBirdImage(String solarTerm) {
        if (solarTerm == null) {
            return "spring_bird.png";
        }

        // 🌸 봄
        if (isInSolarTerm(solarTerm, "입춘", "우수", "경칩", "춘분", "청명", "곡우")) {
            // 봄 안에서도 좀 더 세분화하려면 여기서 조건 더 나눌 수도 있음
            return "spring_bird.png";
        }

        // 🔥 여름
        if (isInSolarTerm(solarTerm, "입하", "소만", "망종", "하지", "소서", "대서")) {
            return "summer_bird.png";
        }

        // 🍂 가을
        if (isInSolarTerm(solarTerm, "입추", "처서", "백로", "추분", "한로", "상강")) {
            return "fall_bird.png";
        }

        // ❄ 겨울
        if (isInSolarTerm(solarTerm, "입동", "소설", "대설", "동지", "소한", "대한")) {
            return "winter_bird.png";
        }

        return "spring_bird.png";
    }

    // 절기에 따른 배경색 Tailwind 클래스
    public String pickSeasonBgClass(String solarTerm) {
        if (solarTerm == null) {
            return "bg-[#fddfe8]";
        }

        // 🌸 봄 초기 / 중 / 말
        if (isInSolarTerm(solarTerm, "입춘", "우수", "경칩")) {
            return "bg-[#ffe7f3]";   // 연핑크
        }
        if (isInSolarTerm(solarTerm, "춘분", "청명")) {
            return "bg-[#ffc2ea]";   // 진한 핑크
        }
        if (isInSolarTerm(solarTerm, "곡우")) {
            return "bg-[#ffd4d4]";   // 여름로 넘어가는 핑크+연레드

            // 🔥 여름
        } else if (isInSolarTerm(solarTerm, "입하", "소만")) {
            return "bg-[#ffe0d5]";   // 연코랄
        } else if (isInSolarTerm(solarTerm, "망종", "하지")) {
            return "bg-[#ffb8b8]";   // 여름 한가운데
        } else if (isInSolarTerm(solarTerm, "소서", "대서")) {
            return "bg-[#ffc7aa]";   // 더위 막바지

            // 🍂 가을
        } else if (isInSolarTerm(solarTerm, "입추", "처서")) {
            return "bg-[#fff0d6]";   // 연한 주황
        } else if (isInSolarTerm(solarTerm, "백로", "추분")) {
            return "bg-[#ffdba8]";   // 가을 중간
        } else if (isInSolarTerm(solarTerm, "한로", "상강")) {
            return "bg-[#ffc78a]";   // 깊어지는 가을

            // ❄ 겨울
        } else if (isInSolarTerm(solarTerm, "입동", "소설")) {
            return "bg-[#e0f2ff]";   // 연하늘
        } else if (isInSolarTerm(solarTerm, "대설", "동지")) {
            return "bg-[#c8e0ff]";   // 차가운 파랑
        } else if (isInSolarTerm(solarTerm, "소한", "대한")) {
            return "bg-[#b3d1ff]";   // 한겨울 느낌
        }

        // 혹시 매칭 안 되면 기본값
        return "bg-[#fddfe8]";
    }

    /** 절기에 맞는 타이틀 글자 색 클래스 */
    public String getTitleTextClass(String solarTerm) {
        if (solarTerm == null) {
            return "text-pink-500"; // 기본값
        }

        if (isInSolarTerm(solarTerm, "입춘","우수","경칩","춘분","청명","곡우")) {
            return "text-pink-500";      // 봄
        }
        if (isInSolarTerm(solarTerm, "입하","소만","망종","하지","소서","대서")) {
            return "text-rose-500";      // 여름
        }
        if (isInSolarTerm(solarTerm, "입추","처서","백로","추분","한로","상강")) {
            return "text-orange-500";    // 가을
        }
        if (isInSolarTerm(solarTerm, "입동","소설","대설","동지","소한","대한")) {
            return "text-[#0ea5e9]";     // 겨울
        }

        return "text-sky-500";
    }

    public String getMemoBoxClass(String solarTerm) {

        if (solarTerm == null) {
            return "bg-[#f7f0f3] border-[#d8b8c7]";  // 기본(부드러운 핑크톤)
        }

        // 봄
        if (isInSolarTerm(solarTerm, "입춘","우수","경칩","춘분","청명","곡우")) {
            return "bg-[#ffe0ef] border-[#ff9ac7]";
        }

        // 여름
        if (isInSolarTerm(solarTerm, "입하","소만","망종","하지","소서","대서")) {
            return "bg-[#ffd9c8] border-[#ff8f65]";
        }

        // 가을
        if (isInSolarTerm(solarTerm, "입추","처서","백로","추분","한로","상강")) {
            return "bg-[#ffe7c2] border-[#ffb347]";
        }

        // 겨울
        if (isInSolarTerm(solarTerm, "입동","소설","대설","동지","소한","대한")) {
            return "bg-[#d6e9ff] border-[#78b7ff]";
        }

        return "bg-[#f7f0f3] border-[#d8b8c7]";
    }

    // 공통 헬퍼
    private boolean isInSolarTerm(String target, String... terms) {
        if (target == null) return false;
        for (String term : terms) {
            if (term.equals(target)) return true;
        }
        return false;
    }
}
