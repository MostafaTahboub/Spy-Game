package com.example.demo.chatgpt;

import com.example.demo.game.Game;
import com.example.demo.game.GameMode;
import com.example.demo.game.GameRepository;
import com.example.demo.game.HintType;
import com.example.demo.guess.FeedbackDTO;
import com.example.demo.guess.Guess;
import com.example.demo.guess.GuessRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;


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
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuessRepository guessRepository;

    @Value("${openai.model}")
    private static String model;

    public static ChatRequest request = new ChatRequest("gpt-4o");

    @Value("${openai.api.url}")
    private String apiUrl;

    public ChatGameInfo createGame(GameMode mode, User user) {

        int secretLength = mode == GameMode.EXTREME ? 6 : 4;
        String systemMessage = String.format("You are an assistant designed to create new games for a spy game application. " +
                "Generate a unique game ID and a secret number for the game. " +
                "Return only the game ID and the secret(%d digits) in the following JSON format without any additional text:\n" +
                "{ \"gameId\": \"unique-id\", \"secret\": \"secret-number\" }", secretLength);

        request.addMessages(new Message("system", systemMessage));

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

            log.info("secret : {}", secret);

            return new ChatGameInfo(gameId, secret);
        } catch (Exception e) {
            log.error("Error parsing AI response: {}", e.getMessage());
            return new ChatGameInfo(null, null);
        }
    }

    public FeedbackDTO guessSecret(String guess, String gameId) {

        Game game = gameRepository.findById(gameId)
                .orElse(null);
        if (game == null) {
            return null;
        }
        String Secret = game.getSecret();
        log.info("Secret: {}", Secret);

        String prompt = String.format("I am playing a number guessing game. My guess is: %1$s. The secret number is: %2$s. Please compare each digit of my guess with the secret number and return the result as two integers: the first integer is the count of digits that are correct and in the correct position, and the second integer is the count of digits that are correct but in the wrong position. Return only these two integers separated by a comma without any thing else.", guess, Secret);
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


    public String getGuessFromAI (){
        String prompt = "I am playing a guessing game with four digits. I have the secret code. Please give me your guess, and I will provide feedback about it. The feedback will indicate the right numbers in the right place and the right numbers in the wrong place. Let's start! just return your generated guess without any additional text.";
        Message message = new Message("user", prompt);
        request.addMessages(message);

        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return null;
        }
        Message assistant = response.getChoices().get(0).getMessage();
        String guess = assistant.getContent().trim();
        log.info("AI Response: {}", guess);

        return guess;
    }

    public static String generateRandomSecret() {
        Random random = new Random();
        StringBuilder secretBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            secretBuilder.append(random.nextInt(10));
        }
        return secretBuilder.toString();
    }


    public String useHint(String gameId, HintType hintType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByName(username).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        if (user.getScore() >= 5 && user.getHints() < 3) {
            user.setScore(user.getScore() - 5);
            user.setHints(user.getHints() + 1);
            userRepository.save(user);

            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                throw new IllegalArgumentException("Game not found.");
            }
            String secret = game.getSecret();

            List<Guess> guesses = guessRepository.findGuessesByGameIdAndUserId(gameId, user.getId());

            Message message = getMessage(hintType, guesses, secret);
            request.addMessages(message);

            ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new IllegalArgumentException("AI response is null or empty.");
            }
            Message assistant = response.getChoices().get(0).getMessage();
            String hint = assistant.getContent().trim();
            log.info("AI Response: {}", hint);

            return hint;
        } else {
            throw new IllegalArgumentException("User cannot use more hints or does not have enough score.");
        }
    }

    private static Message getMessage(HintType hintType, List<Guess> guesses, String secret) {
        if (guesses.isEmpty()) {
            throw new IllegalArgumentException("Guess not found.");
        }
        Guess lastGuess = guesses.get(guesses.size() - 1);
        String guessString = lastGuess.getGuess();

        String prompt;
        switch (hintType) {
            case REVEAL_DIGIT:
                prompt = String.format("I am playing a number guessing game. I need a hint to guess the secret number. The secret number is: %1$s. Please reveal one digit of the secret number that does not exist in this guess number: %2$s. Return only the hint without any additional text.", secret, guessString);
                break;
            case TOO_HIGH_OR_TOO_LOW:
                prompt = String.format("I am playing a number guessing game. The secret number is: %1$s and my last guess is: %2$s. Please indicate if any digit in the guess that does not exist in the secret is too high or too low compared to the corresponding digit in the secret. Return only the hint without any additional text.", secret, guessString);
                break;
            case REVEAL_PARITY:
                prompt = String.format("I am playing a number guessing game. I need a hint to guess the secret number. The secret number is: %1$s. Please reveal if the secret number is even or odd. Return only the hint without any additional text.", secret);
                break;
            default:
                throw new IllegalArgumentException("Invalid hint type");
        }
        return new Message("user", prompt);
    }


}

