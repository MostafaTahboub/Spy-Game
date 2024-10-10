package com.example.demo.guess;


import com.example.demo.chatgpt.ChatService;
import com.example.demo.game.Game;
import com.example.demo.game.GameRepository;
import com.example.demo.game.GameStatus;
import com.example.demo.response.ApiResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController()
@RequestMapping("/guess")
public class GuessController {
    @Autowired
    ChatService chatService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    GameRepository gameRepository;

    //the feedback should be returned
//    @PostMapping("/create")
//    public ApiResponse<String> makeAGuess(){
//        String response = chatService.startGame();
//        return new ApiResponse<>(response, HttpStatus.OK);
//    }
//    @PostMapping("/play")
//    public ApiResponse<FeedbackDTO> makeAGuess(@RequestBody String number, @RequestParam String chatId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        User user = userRepository.findByName(username).orElse(null);
//        Game game = gameRepository.findByChatID(chatId).orElse(null);
//        if (game == null || user == null) {
//            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
//        }
//
//        if (game.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
//            return new ApiResponse<>(null, HttpStatus.FORBIDDEN);
//        }
//        Guess guess = Guess.builder()
//                .guess(number)
//                .game(game)
//                .user(user)
//                .build();
//        game.getGuesses().add(guess);
//        gameRepository.save(game);
//        Optional<FeedbackDTO> response = Optional.ofNullable(chatService.guessSecret(number, chatId));
//        int CorrectNumbersInCorrectPlace = response.get().getRightNumberInRightPlace();
//        int CorrectNumbersInWrongPlace = response.get().getRightNumberInWrongPlace();
//
//        guess.setRightNumberInRightPlace(CorrectNumbersInCorrectPlace);
//        guess.setRightNumberInLWrongPlace(CorrectNumbersInWrongPlace);
////        guessRepository.save(guess);
//
//        if (CorrectNumbersInCorrectPlace == 4) { // should added in join also to check if the game is finished or not
//            game.setStatus(GameStatus.FINISHED); // and should add time validation on join
//            gameRepository.save(game);
//        }
//
//        return response.map(feedbackDTO -> new ApiResponse<>(feedbackDTO, HttpStatus.OK)).orElseGet(() -> new ApiResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
//    }
    @PostMapping("/play")
    public ApiResponse<FeedbackDTO> makeAGuess(@RequestBody String number, @RequestParam String gameId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByName(username).orElse(null);
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null || user == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        }

        if (user.getStatus().equals(UserStatus.IDLE)) {
            return new ApiResponse<>(null, HttpStatus.FORBIDDEN);
        }

        if (game.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            return new ApiResponse<>(null, HttpStatus.FORBIDDEN);
        }
//        Guess guess = Guess.builder().guess(number).game(game).user(user).build();
//        game.getGuesses().add(guess);
        gameRepository.save(game);
        Optional<FeedbackDTO> response = Optional.ofNullable(chatService.guessSecret(number, gameId));
        int CorrectNumbersInCorrectPlace = response.get().getRightNumberInRightPlace();
        int CorrectNumbersInWrongPlace = response.get().getRightNumberInWrongPlace();

//        guess.setRightNumberInRightPlace(CorrectNumbersInCorrectPlace);
//        guess.setRightNumberInLWrongPlace(CorrectNumbersInWrongPlace);
//        guessRepository.save(guess);

        if (CorrectNumbersInCorrectPlace == 4) { // should added in join also to check if the game is finished or not
            game.setStatus(GameStatus.FINISHED); // and should add time validation on join
            game.setWinnerId(user.getId());
            game.getUsers().forEach(u -> u.setStatus(UserStatus.IDLE));
            gameRepository.save(game);
        }

        return response.map(feedbackDTO -> new ApiResponse<>(feedbackDTO, HttpStatus.OK))
                .orElseGet(() -> new ApiResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
