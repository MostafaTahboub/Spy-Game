package com.example.demo.game;

import com.example.demo.chatgpt.ChatMessages;
import com.example.demo.guess.Guess;
import com.example.demo.user.User;
import com.example.demo.utilities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@SuperBuilder
@Table(name = "games")
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class Game extends BaseEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column
    private String chatID;

    @Column
    @NotBlank
    private String password;

    @Column
    private LocalDateTime startsAt;

    @Column
    private String secret;

    @Column
    private LocalDateTime endsAt;

    @OneToMany
    private List<Guess> guesses;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_GAME_MAPPING", joinColumns = @JoinColumn(name = "game_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private GameMode mode;

    @Column
    private String winnerId;

    @OneToMany
    private List<ChatMessages> chatMessages;
}
