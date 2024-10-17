package com.example.demo.guess;

import com.example.demo.chatgpt.ChatService;
import com.example.demo.game.Game;
import com.example.demo.game.GameMode;
import com.example.demo.game.GameRepository;
import com.example.demo.game.GameStatus;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GuessService {

    @Autowired
    ChatService chatService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    private GuessRepository guessRepository;

    public FeedbackDTO processGuess(String guess, String gameId, String username) {
        User user = getUser(username);
        Game game = getGame(gameId);
        validateUserAndGame(user, game);

        FeedbackDTO feedback = chatService.guessSecret(guess, gameId);
        saveGuess(guess, game, user, feedback);

        user.setTries(user.getTries() - 1);
        userRepository.save(user);

        checkAndHandleGameCompletion(game, user, feedback);

        return feedback;
    }

    private User getUser(String username) {
        User user = userRepository.findByName(username).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user;
    }

    private Game getGame(String gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            throw new IllegalArgumentException("Game not found.");
        }
        return game;
    }

    private void validateUserAndGame(User user, Game game) {

        if (user.getStatus().equals(UserStatus.IDLE)) {
            throw new IllegalArgumentException("User is idle.");
        }
        if (game.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new IllegalArgumentException("User is not part of the game.");
        }
        if (game.getUsers().stream().allMatch(u -> u.getTries() == 0)) {
            finishGame(game);
            throw new IllegalArgumentException("All users have no tries left.");
        }
        if (user.getTries() == 0) {
            user.setStatus(UserStatus.IDLE);
            userRepository.save(user);
            throw new IllegalArgumentException("User has no tries left.");
        }
    }

    private void finishGame(Game game) {
        game.setStatus(GameStatus.FINISHED);
        game.getUsers().forEach(u -> u.setStatus(UserStatus.IDLE));
        gameRepository.save(game);
    }

    private void saveGuess(String guess, Game game, User user, FeedbackDTO feedback) {
        Guess newGuess = new Guess();
        newGuess.setGuess(guess);
        newGuess.setGame(game);
        newGuess.setUser(user);
        if (feedback instanceof NumberGuessingFeedbackDTO numberFeedback) {
            newGuess.setRightNumberInRightPlace(numberFeedback.getRightNumberInRightPlace());
            newGuess.setRightNumberInLWrongPlace(numberFeedback.getRightNumberInWrongPlace());
        }
        guessRepository.save(newGuess);
    }

    private void checkAndHandleGameCompletion(Game game, User user, FeedbackDTO feedback) {
        if (feedback instanceof NumberGuessingFeedbackDTO numberFeedback) {
            int correctNumbersInCorrectPlace = numberFeedback.getRightNumberInRightPlace();
            int requiredCorrectNumbers = game.getMode() == GameMode.EXTREME ? 6 : 4;

            if (correctNumbersInCorrectPlace == requiredCorrectNumbers) {
                finishGame(game);
                game.setWinnerId(user.getId());
                user.setScore(user.getScore() + (game.getMode() == GameMode.EXTREME ? 10 : 5));
                userRepository.save(user);
            }
        }
    }


}
