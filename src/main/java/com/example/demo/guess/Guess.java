package com.example.demo.guess;

import com.example.demo.game.Game;
import com.example.demo.user.User;
import com.example.demo.utilities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Entity
@Setter
@Getter
@SuperBuilder
@Table(name = "guesses")
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class Guess extends BaseEntity {
    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column
    @NotBlank
    private String guess;

    @ManyToOne
    @JoinColumn
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private int rightNumberInRightPlace;

    @Column
    private int rightNumberInLWrongPlace;
}
