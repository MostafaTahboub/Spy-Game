package com.example.demo.chatgpt;

import com.example.demo.guess.FeedbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;

//TODO: make it as a service and make it remember the previous texts

@Slf4j
@Service
public class ChatService {

    public static ChatRequest request;
    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public String startGame() {
        // create a request

//        ChatRequest request = new ChatRequest(model);
        request = new ChatRequest();
        request.setModel(model);
        request.setTemperature(0.3);
        request.addMessages(new Message("system", "Create a new for digits secret and return it"));
        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }
        Message assistant = new Message("assistant", response.getChoices().get(0).getMessage().getContent());

        request.addMessages(assistant);
        // return the first response
        return "Game created successfully :) \n Let's start guessing";
    }
    public String guessSecret(String guess)
    {
        // create a request
        String prompt = String.format("My Guess is: %1$s just give me the number of correct numbers in correct place and the correct numbers in the wrong place as just two integers without anything else", guess);
        Message message = new Message("user", prompt);
//        ChatRequest request = new ChatRequest(model);
        request.addMessages(message);
        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        Message assistant = new Message("assistant", response.getChoices().get(0).getMessage().getContent());
        request.addMessages(assistant);
        // return the first response
        return assistant.getContent();
    }
//    public String chat(String prompt)
//    {
//        ChatRequest request = new ChatRequest(model, prompt);
//
//        // call the API
//        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
//        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
//            return "No response";
//        }
//
//        // return the first response
//        return response.getChoices().get(0).getMessage().getContent();
//    }
}
