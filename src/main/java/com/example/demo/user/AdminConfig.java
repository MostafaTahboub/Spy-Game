package com.example.demo.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "users.admin")
public class AdminConfig {

    public static UserDTO admin;
    private String name;
    private String password;
    private String email;
    private String phone;

    @Bean
    public UserDTO admin(UserRepository userRepository, UserMapper userMapper){
        Optional<User> admin = userRepository.findByName(name);
        if(admin.isPresent()){
            return UserMapper.entityToDTO(admin.get());
        }
        User createAdmin =User.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .password(new BCryptPasswordEncoder().encode(password))
                .role(UserRole.ADMIN)
                .email(email)
                .status(UserStatus.IDLE)
                .build();
        userRepository.save(createAdmin);
        return UserMapper.entityToDTO(createAdmin);
    }
}
