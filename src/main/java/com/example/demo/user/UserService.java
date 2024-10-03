package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO createUser(UserRequest userRequest) {
        if (nameExist(userRequest.getUserName())) {
            throw new IllegalArgumentException("User with this name already exists");
        }
        User user = UserMapper.requestToEntity(userRequest);
        Optional<User> found = userRepository.findByName(userRequest.getUserName());

        if (found.isEmpty()) {
            userRepository.save(user);
            return UserMapper.entityToDTO(user);
        }
        return null;
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public UserDTO putUser(UserRequest userRequest, String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User updatedUser = UserMapper.requestToEntity(userRequest);
            updatedUser.setId(id);
            userRepository.save(updatedUser);
            return UserMapper.entityToDTO(updatedUser);
        } else
            return null;
    }

    public User deleteUser(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return user.get();
        } else
            return null;

    }

    private boolean nameExist(String name) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getName().equals(name));
    }

    private boolean nameExist(String name, String id) {
        return userRepository.findByNameAndIdNot(name, id).isPresent();
    }
}
