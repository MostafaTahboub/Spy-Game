package com.example.demo.guess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NumberGuessingFeedbackDTO implements FeedbackDTO {
    int rightNumberInRightPlace;
    int rightNumberInWrongPlace;

}
