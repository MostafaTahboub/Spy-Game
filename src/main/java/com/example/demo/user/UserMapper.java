package com.example.demo.user;

import lombok.Builder;
import org.springframework.stereotype.Component;

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
                .gameList(null)
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
                //after create the game DTO
//                .gameList(entity.getGameList().stream().map(temp->{
//                    temp.setUsers(null);
//                    return temp;
//                }).toList())
                .build();

    }
}

