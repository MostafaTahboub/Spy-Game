package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.TimeZone;

@RestController
@SpringBootApplication
@EnableCaching
@RequestMapping("/")
public class ProjectApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Hebron"));
		SpringApplication.run(ProjectApplication.class, args);
	}
	@GetMapping
	public String home() {
		return "Welcome to the World of Guessing ";
	}

}
