package com.example.demo.guess;

import com.example.demo.game.Game;
import com.example.demo.user.User;
import com.example.demo.utilities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    @Pattern(regexp = "[0-9]{4}")
    @NotBlank
    private String guess;

    @ManyToOne
    @JoinColumn
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    @NotNull
    @Pattern(regexp = "[0-4]")
    private int rightNumberInRightPlace;

    @Column
    @NotNull
    @Pattern(regexp = "[0-4]")
    private int rightNumberInLWrongPlace;
}
