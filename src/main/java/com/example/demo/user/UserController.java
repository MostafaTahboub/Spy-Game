package com.example.demo.user;

import com.example.demo.response.ApiResponse;
import com.example.demo.security.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
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
        try {
            UserDTO userDTO = userService.createUser(userRequest);
            if (userDTO == null) {
                return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ApiResponse<>(userDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ApiResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest loginRequest) throws Exception {
        String response = userService.login(loginRequest);
        if (response == null) {
            return new ApiResponse<>(null, HttpStatus.FORBIDDEN);
        }
        return new ApiResponse<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(@RequestBody UserRequest userRequest, @PathVariable String id) {
        UserDTO userDTO = userService.putUser(userRequest, id);
        if (userDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(userDTO, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<UserDTO> deleteUser(@PathVariable String id) {
        User user = userService.deleteUser(id);
        if (user == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        }
        UserDTO userDTO = UserMapper.entityToDTO(user);

        return new ApiResponse<>(userDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    @GetMapping("/leaderboard")
    public ApiResponse<List<LeaderboardEntryDTO>> getLeaderboard() {
        List<LeaderboardEntryDTO> leaderboard = userService.getLeaderboard();
        return new ApiResponse<>(leaderboard, HttpStatus.OK);
    }


}
