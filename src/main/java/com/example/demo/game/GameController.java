package com.example.demo.game;

import com.example.demo.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    //for user and admin
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

    //for user and admin
    @PostMapping("/join")
    public ApiResponse<GameDTO> joinGame(@RequestParam String gameId , @RequestParam String password) {
        GameDTO gameDTO = gameService.joinGame(gameId, password);
        if (gameDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        } else {
            return new ApiResponse<>(gameDTO, HttpStatus.OK);
        }
    }



    //for player
    @PostMapping("/leave")
    public ApiResponse<GameDTO> leaveGame(@PathVariable String gameId) {
        GameDTO gameDTO = gameService.leaveGame(gameId);
        if (gameDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        } else {
            return new ApiResponse<>(gameDTO, HttpStatus.OK);
        }
    }

    //for admin
    @DeleteMapping("/{id}")
    public ApiResponse<GameDTO> deleteGame(@PathVariable String id) {
        GameDTO gameDTO = gameService.deleteGame(id);
        if (gameDTO == null) {
            return new ApiResponse<>(null, HttpStatus.BAD_REQUEST);
        } else {
            return new ApiResponse<>(gameDTO, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<GameDTO> getGame(@PathVariable String id) {
        GameDTO gameDTO = gameService.getGame(id);
        if (gameDTO == null) {
            return new ApiResponse<>(null, HttpStatus.NO_CONTENT);
        } else {
            return new ApiResponse<>(gameDTO, HttpStatus.OK);
        }
    }
}
