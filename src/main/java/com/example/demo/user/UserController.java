package com.example.demo.user;

import com.example.demo.game.Game;
import com.example.demo.guess.Guess;
import com.example.demo.response.ApiResponse;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public UserController(AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/all")
    public ApiResponse getAllUsers() {
        return null;
    }

    @GetMapping("/{id}")
    public ApiResponse getUserById(String id) {
        return null;
    }

    @PostMapping("/signUp")
    public ApiResponse<User> signUp(@RequestBody UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(userRequest.getPassword()));
        user.setRole(UserRole.PLAYER);
        user.setStatus(UserStatus.IDLE);
        user.setGameList(new ArrayList<Game>());
        user.setGuessList(new ArrayList<Guess>());
        user.setTries(0);
        userRepository.save(user);
        return new ApiResponse<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest loginRequest) throws Exception {
        String username = loginRequest.getUsername();
        User user = userRepository.findByName(username).orElseThrow(() -> new Exception("User not found"));
        log.info("Attempting to authenticate user: {}", user.getName());

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
        String response = "jwtToken: "+jwt;
        log.info("Generated JWT token for user: {}", username);

        return new ApiResponse<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/test")
    public String test() {
        return "Hello World!";
    }

    @PutMapping("/{id}")
    public ApiResponse updateUser(UserRequest userRequest) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteUser(String id) {
        return null;
    }

    @GetMapping("/bestTalentedUsers")
    public ApiResponse getBestTalentedUsers() {
        return null;
    }


}
