package com.b_ban.Weather.Memo.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class MemoService {

    private final List<String> memos = List.of(
            "오늘도 수고 많았어요.",
            "너무 잘하려고 애쓰지 않아도 괜찮아요.",
            "지금까지 버틴 것만으로도 정말 대단해요.",
            "잠깐 쉬어가도 괜찮아요.",
            "천천히 가도 결국 도착하게 돼요.",
            "오늘의 수고가 내일의 나를 만들어요.",
            "완벽하지 않아도 충분히 멋져요.",
            "따뜻한 차 한 잔 마시면서 잠깐 쉬어가요.",
            "오늘 하루도 잘 버텨줘서 고마워요.",
            "스스로를 꼭 안아주고 싶은 날이에요."
    );

    private final Random random = new Random();

    public String getRandomQuote() {
        if (memos.isEmpty()) return "오늘도 편안한 하루 보내길 바랄게요.";
        int index = random.nextInt(memos.size());
        return memos.get(index);
    }

}
