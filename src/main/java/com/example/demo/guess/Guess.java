package com.example.demo.guess;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity
@Setter
@Getter
@SuperBuilder
@Table(name = "guesses")
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class Guess {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    @Pattern(regexp = "[0-9]{4}")
    @NotBlank
    private String guess;

//    @Column
//    private Game game

//    @Column
//    private User user

    @Column
    @NotNull
    @Pattern(regexp = "[0-4]")
    private int right_in_right;

    @Column
    @NotNull
    @Pattern(regexp = "[0-4]")
    private int right_in_wrong;
}
