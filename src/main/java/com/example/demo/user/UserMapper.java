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
    public static UserDTO toDTO(UserRequest request) {
        if(request == null)
            return null;
        return UserDTO.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
    public static User dtoToEntity(UserDTO dto) {
        if(dto == null)
            return null;
        return User.builder()

                .name(dto.getUserName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
    }
    public static UserDTO entityToDTO(User entity) {
        if(entity == null)
            return null;
        return UserDTO.builder()
                .id(entity.getId())
                .userName(entity.getName())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .status(entity.getStatus())
//                .gameList(null)
                .role(entity.getRole())
                .build();
    }
    public static UserDTO entityToDTO(User entity, boolean withGames) {
        if(!withGames)
            return entityToDTO(entity);
        return UserDTO.builder()
                .id(entity.getId())
                .userName(entity.getName())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .role(entity.getRole())
                //after create the game DTO
//                .gameList(entity.getGameList().stream().map(temp->{
//                    temp.setUsers(null);
//                    return temp;
//                }).toList())
                .build();

    }

    public static User requestToEntity(UserRequest request) {
        if(request == null)
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
                .build();
    }
}

