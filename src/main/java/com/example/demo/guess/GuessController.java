package com.example.demo.guess;

import com.example.demo.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
public class GuessController {

    //the feedback should be returned
    @PostMapping("/play/{gameId}")
    public ApiResponse<String> makeAGuess(@RequestBody String guess, @PathVariable String gameId){
        return new ApiResponse<>("", HttpStatus.OK);
    }
}
