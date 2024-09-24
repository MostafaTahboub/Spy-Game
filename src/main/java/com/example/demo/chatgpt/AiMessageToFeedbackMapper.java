package com.example.demo.chatgpt;

import com.example.demo.guess.FeedbackDTO;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Builder
@Component
public class AiMessageToFeedbackMapper {
    public static FeedbackDTO aiToFeedback(String aiResponse) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        String[] result = aiResponse.split(" ");
        feedbackDTO.setRightNumberInRightPlace(result[0]);
        feedbackDTO.setRightNumberInWrongPlace(result[1]);
        return feedbackDTO;
    }
}
