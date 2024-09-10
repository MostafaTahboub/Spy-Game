package com.example.demo.user;

import com.example.demo.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/all")
    public ApiResponse<> getAllUsers() {
        return null;
    }

    @GetMapping("/{id}")
    public ApiResponse getUserById(String id) {
        return null;
    }

    @PostMapping("/signUp")
    public ApiResponse signUp(UserRequest userRequest) {
        return null;
    }

    @PostMapping("/login")
    public ApiResponse login(UserRequest userRequest) {
        return null;
    }

    @PutMapping("/{id}")
    public ApiResponse updateUser(UserRequest userRequest) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteUser(String id) {
    }

    @GetMapping("/bestTalentedUsers")
    public ApiResponse getBestTalentedUsers() {
        return null;
    }


}
