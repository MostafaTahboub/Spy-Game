package com.example.demo.chatgpt;

import com.example.demo.game.Game;
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
@Inheritance(strategy = InheritanceType.JOINED)
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class ChatMessagse extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Game game;

    @Column
    @NotBlank
    private String role;

    @Column
    @NotBlank
    private String context;

}
