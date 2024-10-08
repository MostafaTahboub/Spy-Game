package com.example.demo.chatgpt;

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

    @Value("${openai.model}")
    private static String model;

    public static ChatRequest request = new ChatRequest("gpt-4o");

    @Value("${openai.api.url}")
    private String apiUrl;

//    public ChatGameInfo startGame() {
//        // create a request
//        request.addMessages(new Message("system", "Create a new for digits secret to play spy game with you and return it mapped with unique id to be able to play more that one game at a time." +
//                "only return the game id ***** and the secret with comma between them and nothing else"));
//        // call the API
//        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
//        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
//            return new ChatGameInfo(null,null);
//        }
//        Message assistant = new Message("assistant", response.getChoices().get(0).getMessage().getContent());
//
//        request.addMessages(assistant);
//        String chatId = response.getChoices().get(0).getMessage().getContent().split(",")[0];
//        String secret = response.getChoices().get(0).getMessage().getContent().split(",")[1];
//
//        log.info("chatId : {}", chatId);
//        log.info("secret : {}", secret);
//        //the game id received by AI
////        log.info(response.getChoices().get(0).getMessage().getContent().split(",")[1]);
//        log.info(response.getChoices().get(0).getMessage().getContent());
//        return new ChatGameInfo(chatId, secret);
//    }

    public ChatGameInfo startGame() {
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

        // Log the raw AI response for debugging
        log.info("Raw AI Response: {}", content);

        // Parse the JSON response
        try {
            Map<String, String> responseMap = objectMapper.readValue(content, new TypeReference<Map<String, String>>() {});
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

    public FeedbackDTO guessSecret(String guess, String chatId) {
        // create a request
        String prompt = String.format("My Guess is: %1$s just give me the number of correct numbers in correct place and the correct numbers in the wrong place as just two integers without anything else separated by comma for the game id: %2$s", guess, chatId);
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
