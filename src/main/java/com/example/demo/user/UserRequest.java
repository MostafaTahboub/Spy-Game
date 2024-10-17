package com.example.demo.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name cannot be blank")
    private String userName;

    @Email(regexp = "[A-Za-z0-9_.-]+@gmail\\.com$", message = "Invalid E-mail format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password length must be at least 8")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&])[a-zA-Z0-9!@#$%^&]{8,}$",
            message = "Password must contain uppercase, lowercase, number and special character")
    private String password;




    

}
