package com.example.demo.game;

import com.example.demo.chatgpt.ChatResponse;
import com.example.demo.chatgpt.ChatService;
import com.example.demo.chatgpt.Message;
import com.example.demo.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/AiGame")
public class ReverseGameController {

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ChatService chatService;

    @Value("${openai.api.url}")
    private String apiUrl;

    private String secret;

    @PreAuthorize("hasRole('PLAYER')")
    @PostMapping("/start")
    public ApiResponse<String> startGame() {
        secret = ChatService.generateRandomSecret();
        log.info("the secret is: {}", secret);
        return new ApiResponse<>("Game started. Secret generated.", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/ai-guess")
    public ApiResponse<String> getAIGuess() {
        String aiGuess = chatService.getGuessFromAI();
        return new ApiResponse<>(aiGuess, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PLAYER')")
    @PostMapping("/feedback")
    public ApiResponse<String> provideFeedback(@RequestParam int rightNumInRightPlace, @RequestParam int rightNumInWrongPlace) {
        String feedbackMessage = String.format("Feedback: %d digits are correct and in the correct place, %d digits are correct but in the wrong place.", rightNumInRightPlace, rightNumInWrongPlace);
        log.info("Sending feedback to AI: {}", feedbackMessage);

        if (rightNumInRightPlace == 4) {
            return handleCorrectGuess();
        }

        return handleNextGuess(feedbackMessage);
    }

    private ApiResponse<String> handleCorrectGuess() {
        String correctGuessPrompt = "You have guessed the correct number! Please generate analytics based on your last guess.";
        Message correctGuessMessage = new Message("user", correctGuessPrompt);
        ChatService.request.addMessages(correctGuessMessage);

        ChatResponse analyticsResponse = restTemplate.postForObject(apiUrl, ChatService.request, ChatResponse.class);
        if (analyticsResponse == null || analyticsResponse.getChoices() == null || analyticsResponse.getChoices().isEmpty()) {
            return new ApiResponse<>("Failed to get a response from AI.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Message analyticsMessage = analyticsResponse.getChoices().get(0).getMessage();
        String analytics = analyticsMessage.getContent().trim();
        log.info("AI's analytics: {}", analytics);

        ChatService.request.addMessages(new Message("assistant", analytics));
        return new ApiResponse<>(analytics, HttpStatus.OK);
    }


    private ApiResponse<String> handleNextGuess(String feedbackMessage) {
        String prompt = String.format("Here is the feedback for your last guess: %s. Please provide your next guess based on this feedback. Return only the guess (four digits) without any additional text.", feedbackMessage);
        Message message = new Message("user", prompt);
        ChatService.request.addMessages(message);

        ChatResponse response = restTemplate.postForObject(apiUrl, ChatService.request, ChatResponse.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return new ApiResponse<>("Failed to get a response from AI.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Message assistant = response.getChoices().get(0).getMessage();
        String nextGuess = assistant.getContent().trim();
        log.info("AI's next guess: {}", nextGuess);

        ChatService.request.addMessages(new Message("assistant", nextGuess));
        return new ApiResponse<>(nextGuess, HttpStatus.OK);
    }
}
