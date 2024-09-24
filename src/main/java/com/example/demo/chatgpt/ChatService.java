package com.example.demo.chatgpt;

import com.example.demo.guess.FeedbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ChatService {


    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private static String model;

    public static ChatRequest request = new ChatRequest("gpt-4o");

    @Value("${openai.api.url}")
    private String apiUrl;

    public String startGame() {
        // create a request
        request.addMessages(new Message("system", "Create a new for digits secret to play spy game with you and return it mapped with unique id to be able to play more that one game at a time." +
                "only return the game id as game id: ****** and the secret with comma around the game id and the secret as row text"));
        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }
        Message assistant = new Message("assistant", response.getChoices().get(0).getMessage().getContent());

        request.addMessages(assistant);
        //the game id received by AI
        log.info(response.getChoices().get(0).getMessage().getContent().split(",")[1]);
        return response.getChoices().get(0).getMessage().getContent();
    }

    public FeedbackDTO guessSecret(String guess, String gameId)
    {
        // create a request
        String prompt = String.format("My Guess is: %1$s just give me the number of correct numbers in correct place and the correct numbers in the wrong place as just two integers without anything else separated by comma for the game id: %2$s", guess, gameId);
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
