package com.example.demo.user;

import com.example.demo.response.ApiResponse;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Autowired
    private UserService userService;

    public UserController(AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/all")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOList = userService.getAllUsers()
                .stream()
                .map(UserMapper::entityToDTO)
                .toList();

        if (userDTOList.isEmpty()) {
            return new ApiResponse<>(null, HttpStatus.NO_CONTENT);
        } else {
            return new ApiResponse<>(userDTOList, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable String id) {
        UserDTO userDTO = userService.findById(id)
                .map(UserMapper::entityToDTO)
                .orElse(null);
        if (userDTO == null) {
            return new ApiResponse<>(null, HttpStatus.NO_CONTENT);
        }
        return new ApiResponse<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/signUp")
    public ApiResponse<UserDTO> signUp(@Validated @RequestBody UserRequest userRequest) {
        UserDTO userDTO = userService.createUser(userRequest);
        if (userDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(userDTO, HttpStatus.CREATED);
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
        String response = "jwtToken: " + jwt;
        log.info("Generated JWT token for user: {}", username);

        return new ApiResponse<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/test")
    public String test() {
        return "Hello World!";
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser( @RequestBody UserRequest userRequest,@PathVariable String id) {
        UserDTO userDTO = userService.putUser(userRequest, id);
        if (userDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(userDTO, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ApiResponse<UserDTO> deleteUser(@PathVariable String id) {
        User user = userService.deleteUser(id);
        if (user == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        }
        UserDTO userDTO = UserMapper.entityToDTO(user);
        return new ApiResponse<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/bestTalentedUsers")
    public ApiResponse<List<UserDTO>> getBestTalentedUsers() {
        return null;
    }


}
