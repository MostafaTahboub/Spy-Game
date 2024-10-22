package com.example.demo.guess;


import com.example.demo.chatgpt.ChatService;
import com.example.demo.game.HintType;
import com.example.demo.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController()
@RequestMapping("/guess")
public class GuessController {
    @Autowired
    ChatService chatService;
    @Autowired
    GuessService guessService;

    @Operation(summary = "Make a guess")
    @PreAuthorize("hasRole('PLAYER')")
    @PostMapping("/play")
    public ApiResponse<FeedbackDTO> makeAGuess(@RequestBody String guess, @RequestParam String gameId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            FeedbackDTO feedback = guessService.processGuess(guess, gameId, username);
            return new ApiResponse<>(feedback, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Use a hint")
    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/hint")
    public ApiResponse<String> useHint(@RequestParam String gameId, @RequestParam HintType hintType) {
        try {
            String hint = chatService.useHint(gameId, hintType);
            return new ApiResponse<>(hint, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
