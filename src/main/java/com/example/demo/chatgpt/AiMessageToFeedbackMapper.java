package com.example.demo.chatgpt;

import com.example.demo.guess.FeedbackDTO;
import com.example.demo.guess.NumberGuessingFeedbackDTO;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Builder
@Component
public class AiMessageToFeedbackMapper {
    public static FeedbackDTO aiToFeedback(String aiResponse) {
        String[] parts = aiResponse.split(",");
        int rightNumberInRightPlace = Integer.parseInt(parts[0].trim());
        int rightNumberInWrongPlace = Integer.parseInt(parts[1].trim());
        return new NumberGuessingFeedbackDTO(rightNumberInRightPlace, rightNumberInWrongPlace);
    }
}
