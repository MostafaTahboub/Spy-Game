package com.example.demo.user;

import com.example.demo.game.Game;
import com.example.demo.guess.Guess;
import com.example.demo.utilities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@SuperBuilder
@Table(name = "users" ,indexes = {
        @Index(name = "idx_user_name", columnList = "name"),
        @Index(name="idx_user_status", columnList = "status")
})
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @NotBlank
    @Column
    private String name;

    @Column
    @NotBlank
    @Email(regexp = "[A-Za-z0-9_.-]+@gmail\\.com$")
    private String email;

    @Column
    @NotBlank
    private String password;


    @ManyToMany
    @JoinTable(
            name = "USER_GAME_MAPPING",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> gameList;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Guess> guessList;

    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private int tries;

    @Column
    private int hints;

    @Column
    private int score;
}
