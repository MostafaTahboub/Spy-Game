package com.example.demo.game;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    @NonNull
    Optional<Game> findById(String id);
    Game findByType(GameType type);
}
