package com.example.demo.guess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {

    int rightNumberInRightPlace;
    int rightNumberInWrongPlace;

}
