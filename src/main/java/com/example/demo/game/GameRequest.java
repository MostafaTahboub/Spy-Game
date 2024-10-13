package com.example.demo.game;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameRequest {

    @NotNull
    private GameType type;

    @NotBlank
    @NotNull
    private String password;

    @FutureOrPresent( message = "Starts at must be in the future or present")
    private LocalDateTime startsAt;

}
