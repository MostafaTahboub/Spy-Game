package com.example.demo.game;

import com.example.demo.chatgpt.ChatService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

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
        if (game == null)
            throw new IllegalArgumentException("Game Request is null");

        gameRepository.save(game);
        return GameMapper.entityToDTO(game);
    }


    public GameDTO joinGame(String gameId, String password) {
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty() || !game.get().getPassword().equals(password)) {
            return null;
        }

        game.get().setStatus(GameStatus.ON_GOING);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByName(username);

        if (user.isEmpty() || isUserAlreadyInGame(game.get(), user.get())) {
            return null;
        }

        addUserToGame(game.get(), user.get());
        return GameMapper.entityToDTO(game.get());
    }

    public String startGame(String gameId) {
     return chatService.startGame();
    }

    public GameDTO leaveGame(String gameId) {
        Game gameFound = gameRepository.findById(gameId).orElse(null);
        if (gameFound == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByName(username);
        if (user.get().getGameList().stream()
                .noneMatch(game -> game.getId().equals(gameId))) {
            return null;
        }
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
