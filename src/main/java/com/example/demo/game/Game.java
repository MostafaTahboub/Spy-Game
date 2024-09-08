package com.example.demo.game;

import com.example.demo.guess.Guess;
import com.example.demo.user.User;
import com.example.demo.utilities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@Table(name = "guesses")
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class Game extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String password;

    @Column
    private LocalDateTime startsAt;

    @Column
    private LocalDateTime endsAt;

    @OneToMany
    private List<Guess> guesses;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_GAME_MAPPING", joinColumns = @JoinColumn(name = "game_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;
}
