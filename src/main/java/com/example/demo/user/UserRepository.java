package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByName(String username);
    Optional<User> findById(String id);
    void deleteById(String id);
    @Query("SELECT new com.example.demo.user.LeaderboardEntryDTO(u.name, u.score, COUNT(g)) " +
            "FROM User u LEFT JOIN u.gameList g " +
            "GROUP BY u.id ORDER BY u.score DESC")
    List<LeaderboardEntryDTO> findTopPlayers();
    Optional<User> findByNameAndIdNot(String name, String id);
}
