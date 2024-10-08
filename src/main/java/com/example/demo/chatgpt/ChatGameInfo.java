package com.example.demo.chatgpt;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ChatGameInfo {
    private String chatId;
    private String secret;

    public ChatGameInfo(String chatId, String secret) {
        this.chatId = chatId;
        this.secret = secret;
    }
}