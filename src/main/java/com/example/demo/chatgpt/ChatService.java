package com.example.demo.chatgpt;

import com.example.demo.game.Game;
import com.example.demo.game.GameRepository;
import com.example.demo.guess.FeedbackDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class ChatService {

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GameRepository gameRepository;

    @Value("${openai.model}")
    private static String model;

    public static ChatRequest request = new ChatRequest("gpt-4o");

    @Value("${openai.api.url}")
    private String apiUrl;

    public ChatGameInfo createGame() {
        // Create a system message to instruct the AI
        request.addMessages(new Message("system", "You are an assistant designed to create new games for a spy game application. " +
                "Generate a unique game ID and a secret number for the game. " +
                "Return only the game ID and the secret(4 digits) in the following JSON format without any additional text:\n" +
                "{ \"gameId\": \"unique-id\", \"secret\": \"secret-number\" }"));

        // Call the AI API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            log.error("AI response is null or empty.");
            return new ChatGameInfo(null, null);
        }
        Message assistant = response.getChoices().get(0).getMessage();
        String content = assistant.getContent().trim();

        log.info("Raw AI Response: {}", content);
        try {
            Map<String, String> responseMap = objectMapper.readValue(content, new TypeReference<Map<String, String>>() {
            });
            String gameId = responseMap.get("gameId");
            String secret = responseMap.get("secret");

            if (gameId == null || secret == null) {
                log.error("AI response missing required fields: {}", content);
                return new ChatGameInfo(null, null);
            }

            log.info("chatId : {}", gameId);
            log.info("secret : {}", secret);

            return new ChatGameInfo(gameId, secret);
        } catch (Exception e) {
            log.error("Error parsing AI response: {}", e.getMessage());
            return new ChatGameInfo(null, null);
        }
    }

    public FeedbackDTO guessSecret(String guess, String gameId) {
        // create a request
        Game game = gameRepository.findById(gameId)
                .orElse(null);
        if (game == null) {
            return null;
        }
        String Secret = game.getSecret();
        log.info("Secret: {}", Secret);

        String prompt = String.format("I am playing a number guessing game. My guess is: %1$s. The secret number is: %2$s. Please compare each digit of my guess with the secret number and return the result as two integers: the first integer is the count of digits that are correct and in the correct position, and the second integer is the count of digits that are correct but in the wrong position. Return only these two integers separated by a comma.", guess, Secret);
        Message message = new Message("user", prompt);
        request.addMessages(message);
        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return null;
        }
        //the secret code received from AI
        log.info(response.getChoices().get(0).getMessage().getContent());
        Message assistant = new Message("assistant", response.getChoices().get(0).getMessage().getContent());
        request.addMessages(assistant);
        return AiMessageToFeedbackMapper.aiToFeedback(assistant.getContent());
    }

}
