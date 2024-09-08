package com.example.demo.user;

import com.example.demo.game.Game;
import com.example.demo.guess.Guess;
import com.example.demo.utilities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jdk.jfr.Unsigned;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@Table(name = "users" )
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Column
    @UniqueElements
    private String name;

    @Column
    @NotBlank
    @Email(regexp = "[A-Za-z0-9_.-]+@gmail\\.com$")
    private String email;

    @Column
    @NotBlank
    @Pattern(regexp = "^(?=.[a-z])(?=.[A-Z])(?=.[0-9])(?=.[!@#$%^&])[a-zA-Z0-9!@#$%^&]{8}$")
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


}
