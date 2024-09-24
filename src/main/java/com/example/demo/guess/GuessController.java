package com.example.demo.guess;


import com.example.demo.chatgpt.AiMessageToFeedbackMapper;
import com.example.demo.chatgpt.ChatService;
import com.example.demo.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController()
public class GuessController {
    @Autowired
    ChatService chatService;
    //the feedback should be returned
    @PostMapping("/create")
    public ApiResponse<String> makeAGuess(){
        String response = chatService.startGame();
        return new ApiResponse<>(response, HttpStatus.OK);
    }
    @PostMapping("/play")
    public ApiResponse<FeedbackDTO> makeAGuess(@RequestBody String number){
        String response = chatService.guessSecret(number);
        log.info(response);
        return new ApiResponse<>(AiMessageToFeedbackMapper.aiToFeedback(response), HttpStatus.OK);
    }
}
