package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO createUser(UserDTO userDTO) {
        User user = UserMapper.dtoToEntity(userDTO);
        Optional<User> found = userRepository.findByName(userDTO.getUserName());
        if (found.isEmpty()) {
            User addedUser = userRepository.save(user);
            return UserMapper.entityToDTO(addedUser);
        }
        return null;

    }

    public Optional<User> findById(String id) {

        return userRepository.findById(id);
    }

    public UserDTO putUser(UserDTO user) {
        Optional<User> userEntity = userRepository.findByName(user.getUserName());
        if (userEntity.isPresent()) {
            User newUser = UserMapper.dtoToEntity(user);
            userRepository.delete(userEntity.get());
            User updatedUser = userRepository.save(newUser);
            return UserMapper.entityToDTO(updatedUser);
        }
        return null;

    }

    public UserDTO deleteUser(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteAllById(user.get().getId());
            return UserMapper.entityToDTO(user.get());
        }
        else return null;

    }
}
