package com.example.demo.user;

import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

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

    public String login(LoginRequest loginRequest) throws Exception {
        String username = loginRequest.getUsername();
        User user = userRepository.findByName(username).orElseThrow(() -> new Exception("User not found"));
        log.info("Attempting to authenticate user: {}", user.getName());

        String existingToken = jwtUtil.getTokenFromCache(username);
        if (existingToken != null && jwtUtil.validateToken(existingToken, username)) {
            log.warn("User {} already has a valid token. Login attempt forbidden.", username);
            throw new Exception("User already logged in with a valid token.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword())
            );
            log.info("User {} authenticated successfully", username);
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {} - Incorrect username or password", username);
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        log.info("Generated JWT token for user: {}", username);

        jwtUtil.storeTokenInCache(username, jwt);

        return "jwtToken: " + jwt;
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @CachePut(value = "users", key = "#id")
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

    public List<LeaderboardEntryDTO> getLeaderboard() {
        return userRepository.findTopPlayers();
    }

    @CacheEvict(value = "users", key = "#id")
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
