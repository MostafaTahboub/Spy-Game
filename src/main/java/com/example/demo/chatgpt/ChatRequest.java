package com.example.demo.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

    private String model;
    private List<Message> messages = new ArrayList<>();
    @Value("${openai.api.temperature}")
    private double temperature;

    public ChatRequest(String model) {
        this.model = model;
        this.temperature = 0.3;
    }
    public void addMessages(Message message) {
        this.messages.add(message);
    }
}
