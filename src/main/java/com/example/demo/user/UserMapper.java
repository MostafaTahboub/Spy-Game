package com.example.demo.user;

import com.example.demo.game.Game;
import com.example.demo.guess.Guess;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Builder
@Component
public class UserMapper {

    public static UserDTO entityToDTO(User entity) {
        if (entity == null)
            return null;
        return UserDTO.builder()
                .id(entity.getId())
                .userName(entity.getName())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .status(entity.getStatus())
//                .gameList(entity.getGameList().stream().map(GameMapper::entityToDTO).collect(Collectors.toList()))
                .role(entity.getRole())
                .score(entity.getScore())
                .build();
    }

    public static User requestToEntity(UserRequest request) {
        if (request == null)
            return null;
        return User.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getUserName())
                .email(request.getEmail())
                .password(new BCryptPasswordEncoder().encode(request.getPassword()))
                .role(UserRole.PLAYER)
                .status(UserStatus.IDLE)
                .gameList(new ArrayList<Game>())
                .guessList(new ArrayList<Guess>())
                .score(15)
                .build();
    }
}

