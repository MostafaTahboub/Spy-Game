package com.example.demo.guess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuessRepository extends JpaRepository<Guess, String> {

    List<Guess> findGuessesByGameIdAndUserId(String gameId, String id);
}
