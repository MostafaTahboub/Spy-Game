package com.example.demo.game;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    //for user and admin
    @PostMapping("/create")
    public String createGame(@RequestBody String request) {return "";}

    //for user and admin
    @PostMapping("/join")
    public String joinGame(@RequestBody String joinRequest){return "";}

    //for user and admin check using token
    @PostMapping("/start")
    public String startGame() {return "";}

    //for player
    @PostMapping("/leave")
    public String leaveGame(){return "";}

    //for admin
    @DeleteMapping("/{id}")
    public String deleteGame(@PathVariable String id){return "";}
}
