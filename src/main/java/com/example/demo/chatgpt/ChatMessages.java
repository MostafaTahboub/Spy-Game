package com.example.demo.chatgpt;

import com.example.demo.game.Game;
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
@Inheritance(strategy = InheritanceType.JOINED)
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class ChatMessages extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn
    private Game game;

    @Column
    @NotBlank
    private String role;

    @Column
    @NotBlank
    private String messages;

}
