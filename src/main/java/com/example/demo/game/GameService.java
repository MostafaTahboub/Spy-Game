package com.example.demo.game;

import com.example.demo.chatgpt.ChatGameInfo;
import com.example.demo.chatgpt.ChatService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    public GameDTO createGame(@Validated GameRequest gameRequest) {
        Game game = GameMapper.requestToEntity(gameRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByName(username);
        if (user.isEmpty() || user.get().getStatus() == UserStatus.IN_GAME) {
            return null;
        }
        if (game == null) {
            throw new IllegalArgumentException("Game Request is null");
        }
        ChatGameInfo chatGameInfo = chatService.createGame(game.getMode(), user.get());
        if (chatGameInfo == null) {
            log.info("Failed to start game with ChatService");
            throw new IllegalStateException("Failed to start game with ChatService");
        }
        log.info("chat Id : {}", chatGameInfo.getChatId());
        log.info("secret : {}", chatGameInfo.getSecret());

        game.setChatID(chatGameInfo.getChatId());
        game.setSecret(chatGameInfo.getSecret());
        gameRepository.save(game);

        return GameMapper.entityToDTO(game);
    }


    public GameDTO joinGame(String gameId, String password) {
        Optional<Game> game = gameRepository.findById(gameId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByName(username);
        if (game.isEmpty() || !game.get().getPassword().equals(password)) {
            return null;
        }

        if (game.get().getMode() == GameMode.EXTREME && user.get().getScore() < 50) {
            throw new IllegalArgumentException("User does not have enough score to play extreme mode.");
        }

        log.info("start time : {}", game.get().getStartsAt());
        log.info("end time : {}", game.get().getEndsAt());
        log.info("current time : {}", LocalDateTime.now());
        if (LocalDateTime.now().isAfter(game.get().getEndsAt())) {
            return null;
        }
        if (LocalDateTime.now().isBefore(game.get().getStartsAt())) {
            return null;
        }
        if (game.get().getStatus() == GameStatus.FINISHED) {
            return null;
        }

        game.get().setStatus(GameStatus.ON_GOING);

        if (user.isEmpty() || isUserAlreadyInGame(game.get(), user.get()) || user.get().getStatus() == UserStatus.IN_GAME) {
            return null;
        }
        user.get().setStatus(UserStatus.IN_GAME);
        user.get().setTries(10);
        user.get().setHints(0);
        userRepository.save(user.get());
        addUserToGame(game.get(), user.get());
        return GameMapper.entityToDTO(game.get());
    }

    public GameDTO leaveGame(String gameId) {
        Game gameFound = gameRepository.findById(gameId).orElse(null);
        if (gameFound == null) {
            return null;
        }
        if (LocalDateTime.now().isAfter(gameFound.getEndsAt())) {
            return null;
        }
        if (LocalDateTime.now().isBefore(gameFound.getStartsAt())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByName(username);
        if (user.get().getGameList().stream()
                .noneMatch(game -> game.getId().equals(gameId))) {
            return null;
        }
        user.get().setScore(user.get().getScore() - 5);
        user.get().setStatus(UserStatus.IDLE);
        userRepository.save(user.get());
        gameFound.getUsers().remove(user.get());
        gameRepository.save(gameFound);
        return GameMapper.entityToDTO(gameFound);
    }

    public GameDTO deleteGame(String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return null;
        }
        gameRepository.deleteById(id);
        return GameMapper.entityToDTO(game);
    }


    public GameDTO getGame(String id) {
        Optional<Game> game = gameRepository.findById(id);
        return game.map(GameMapper::entityToDTO)
                .orElse(null);
    }

    private boolean isUserAlreadyInGame(Game game, User user) {
        return game.getUsers().stream()
                .anyMatch(gameUser -> gameUser.getId().equals(user.getId()));
    }

    private void addUserToGame(Game game, User user) {
        user.setStatus(UserStatus.IN_GAME);
        userRepository.save(user);

        List<User> users = game.getUsers();
        users.add(user);
        game.setUsers(users);
        gameRepository.save(game);
    }
}
