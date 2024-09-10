package com.example.demo.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Getter
@Setter
@Builder
public class ApiResponse<T> extends ResponseEntity<T> {

    private T content;
    private HttpStatus status;

    public ApiResponse(T body, HttpStatus status) {
        super(body, status);
        this.content = body;
        this.status = status;
    }

}
