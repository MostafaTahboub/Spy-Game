package com.example.demo.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private List<Choice> choices;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Choice {

        private int index;
        private Message message;

        // constructors, getters and setters
    }
}
