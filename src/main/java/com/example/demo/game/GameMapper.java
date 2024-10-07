package com.example.demo.game;

import com.example.demo.user.UserMapper;
import lombok.Builder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Component
public class GameMapper {

    public static Game requestToEntity(@Validated GameRequest gameRequest) {
        if (gameRequest == null)
            return null;

        return Game.builder()
                .id(UUID.randomUUID().toString())
                .password(gameRequest.getPassword())
                .startsAt(gameRequest.getStartsAt())
                .endsAt(gameRequest.getStartsAt().plusMinutes(20))
                .status(GameStatus.CREATED)
                .build();
    }

    public static GameDTO entityToDTO(Game game) {

        return GameDTO.builder()
                .id(game.getId())
                .password(game.getPassword())
                .startsAt(game.getStartsAt())
                .endsAt(game.getEndsAt())
                .status(game.getStatus())
                .winnerId(game.getWinnerId())
                .players(game.getUsers().stream().map(UserMapper::entityToDTO).collect(Collectors.toList()))
                .build();

    }


}
