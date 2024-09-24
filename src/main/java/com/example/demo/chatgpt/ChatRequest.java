package com.example.demo.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    private String model;
    private List<Message> messages = new ArrayList<>();
    private double temperature;

    public ChatRequest(String model) {
        this.model = model;
        this.messages.add(new Message("system", "start a new spy game so, pick a random four digits number and assign it to this id."));
        this.temperature = 0.3;
    }
    public void addMessages(Message message) {
        this.messages.add(message);
    }
}
