package com.example.demo.game;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    @NonNull
    Optional<Game> findById(String id);
    List<Game> findByModeAndStatusInAndStartsAtBeforeAndEndsAtAfter(
            @NonNull GameMode mode,
            @NonNull Collection<GameStatus> status,
            @NonNull LocalDateTime now,
            @NonNull LocalDateTime now2);
}
