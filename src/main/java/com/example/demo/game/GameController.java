package com.example.demo.game;

import com.example.demo.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    //for user and admin
    @Operation(summary = "Create a new game")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    @PostMapping("/create")
    public ApiResponse<GameDTO> createGame(@Validated @RequestBody GameRequest gameRequest) {
        try {
            GameDTO gameDTO = gameService.createGame(gameRequest);
            log.info("Game created: {}", gameDTO);
            return new ApiResponse<>(gameDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.info("Game creation failed: {}", e.getMessage());
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.info("Game creation failed sdds: {}", e.getMessage());
            return new ApiResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Join a game")
    @PreAuthorize("hasRole('PLAYER')")
    @PostMapping("/join")
    public ApiResponse<GameDTO> joinGame(@RequestParam String gameId, @RequestParam String password) {
        GameDTO gameDTO = gameService.joinGame(gameId, password);
        if (gameDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        } else {
            return new ApiResponse<>(gameDTO, HttpStatus.OK);
        }
    }


    @Operation(summary = "Leave a game ")
    //for player
    @PreAuthorize("hasRole('PLAYER')")
    @PostMapping("/leave/{id}")
    public ApiResponse<GameDTO> leaveGame(@PathVariable String id) {
        GameDTO gameDTO = gameService.leaveGame(id);
        if (gameDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        } else {
            return new ApiResponse<>(gameDTO, HttpStatus.OK);
        }
    }

    //for admin
    @Operation(summary = "Delete a game")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<GameDTO> deleteGame(@PathVariable String id) {
        GameDTO gameDTO = gameService.deleteGame(id);
        if (gameDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        } else {
            return new ApiResponse<>(gameDTO, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get a game")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    @GetMapping("/{id}")
    public ApiResponse<GameDTO> getGame(@PathVariable String id) {
        GameDTO gameDTO = gameService.getGame(id);
        if (gameDTO == null) {
            return new ApiResponse<>(null, HttpStatus.NO_CONTENT);
        } else {
            return new ApiResponse<>(gameDTO, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get all available games by mode")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    @GetMapping("/availableGamesByMode")
    public ApiResponse<List<GameDTO>> getAllAvailableGamesByMode(@RequestParam GameMode mode) {
        List<GameDTO> availableGames = gameService.getAvailableGamesByMode(mode);
        if (availableGames.isEmpty()) {
            return new ApiResponse<>(null, HttpStatus.NO_CONTENT);
        } else {
            return new ApiResponse<>(availableGames, HttpStatus.OK);
        }
    }

}
