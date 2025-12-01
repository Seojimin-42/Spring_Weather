package com.b_ban.Weather.Common.util;

import java.util.HashMap;
import java.util.Map;

// SolarTermDescription.java

public class SolarTermDescription {

    private static final Map<String, String> TERM_DESC = new HashMap<>();

    static {
        TERM_DESC.put("소한", "한겨울의 시작되는 시기,\n공기가 한층 더 차가워져요.");
        TERM_DESC.put("대한", "1년 중 가장 춥다는 절기.\n한파가 절정에 이뤄요.");
        TERM_DESC.put("입춘", "봄이 시작되는 시기.\n추위 속에서 봄기운이 스며들어요.");
        TERM_DESC.put("우수", "눈이 녹아 비가 된다는 절기.\n날씨가 조금씩 풀려요.");
        TERM_DESC.put("경칩", "겨울잠 자던 동물이 깨어나는 시기.\n완연한 봄의 시작이에요.");
        TERM_DESC.put("춘분", "밤과 낮의 길이가 거의 같은 시기.\n봄의 중간 지점이에요.");
        TERM_DESC.put("청명", "하늘이 맑고 투명한 시기.\n바깥 활동을 하기 좋은 날이에요.");
        TERM_DESC.put("곡우", "농사를 돕는 봄비가 내리는 시기.\n논밭이 촉촉해 지는 때예요.");
        TERM_DESC.put("입하", "여름의 시작.\n기온이 올라가고 녹음이 짙어져요.");
        TERM_DESC.put("소만", "만물이 자라는 시기.\n초록빛이 점점 짙어지는 때예요.");
        TERM_DESC.put("망종", "씨앗을 뿌리기 알맞은 절기.\n본격적인 농사 준비가 시작돼요.");
        TERM_DESC.put("하지", "낮이 가장 긴 날.\n여름의 절정이 다가와요.");
        TERM_DESC.put("소서", "덥기 시작하는 시기.\n더위가 더 강해질 때예요.");
        TERM_DESC.put("대서", "극심한 더위 절정.\n폭염이 이어져요.");
        TERM_DESC.put("입추", "가을의 시작.\n여름의 끝이 보이기 시작이에요.");
        TERM_DESC.put("처서", "더위가 한풀 꺾이는 시기.\n아침저녁 바람이 조금씩 선선해져요.");
        TERM_DESC.put("백로", "이슬이 맺히기 시작하는 가을의 초입.\n기온이 선선히 내려가요.");
        TERM_DESC.put("추분", "밤이 길어지는 시기.\n가을의 한가운데에 서 있는 때예요.");
        TERM_DESC.put("한로", "찬 이슬이 내리는 시기.\n가을 공기가 차가워지는 때예요.");
        TERM_DESC.put("상강", "서리가 내리기 시작하는 늦가을.\n곧 겨울이 다가오는 시기예요.");
        TERM_DESC.put("입동", "겨울의 시작.\n추위가 느껴지기 시작이에요.");
        TERM_DESC.put("소설", "눈이 조금씩 내리는 겨울 초입.\n초겨울의 차가운 기운이 느껴져요.");
        TERM_DESC.put("대설", "눈이 많이 내리기 시작하는 시기.\n겨울 풍경이 펼쳐지는 때예요.");
        TERM_DESC.put("동지", "밤이 가장 긴 날.\n팥죽을 먹는 날로 알려졌어요.");
    }

    public static String getDescription(String term) {
        return TERM_DESC.get(term);
    }

}
