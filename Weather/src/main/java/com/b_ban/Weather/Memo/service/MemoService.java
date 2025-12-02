package com.b_ban.Weather.Memo.service;

import com.b_ban.Weather.Common.service.SeasonThemeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

// MemoService.java

@Service
public class MemoService {

    private final List<String> memos = List.of(
            "오늘도\n좋은 하루\n보내세요.",
            "너무 잘하려고\n애쓰지 않아도\n괜찮아요.",
            "지금까지\n버틴 것만으로도\n정말 대단해요.",
            "잠깐 쉬어가도\n괜찮아요.",
            "천천히 가도\n결국\n도착하게 돼요.",
            "오늘의 수고가\n내일의 나를\n만들어요.",
            "완벽하지 않아도\n충분히 멋져요.",
            "따뜻한 차\n한 잔 마시면서\n잠깐 쉬어가요.",
            "오늘 하루도\n잘 버텨줘서\n고마워요.",
            "스스로를\n꼭 안아주세요.",
            "한번 쉬어가는 것도\n용기예요.",
            "당신은\n지금도\n잘하고 있어요.",
            "마음이 무거운 날엔\n조금 더 천천히\n걸어가도 괜찮아요.",
            "괜찮아요.\n지금 힘든 것도\n언젠가 지나가요.",
            "당신의 노력이\n언젠가\n빛날 거예요.",
            "당신의 하루에\n작은 빛이 되길.",
            "지친 마음에도\n햇살은\n다시 찾아와요.",
            "괜찮아요.\n지금 힘든 것도\n언젠가 지나가요.",
            "지금의 나,\n충분히 강하다!",
            "시작이 반이다!\n지금 시작해요!"
    );

    private final Random random = new Random();

    public String getRandomQuote() {
        if (memos.isEmpty()) return "오늘도 편안한 하루 보내길 바랄게요.";
        int index = random.nextInt(memos.size());
        return memos.get(index);
    }

    public String getMemoBoxClass(String solarTerm, SeasonThemeService themeService) {
        return themeService.getMemoBoxClass(solarTerm);
    }

}
