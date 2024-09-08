package com.example.demo.user;

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

@Entity
@Setter
@Getter
@SuperBuilder
@Table(name = "users" )
@AllArgsConstructor
@NoArgsConstructor
@Audited
@AuditTable(value="AUD_User")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Column
    @UniqueElements
    private String name;

    @Column
    @NotBlank
    @Email(regexp = "[A-Za-z0-9_.-]+@gmail\\.com$", message = "Invalid E-mail format")
    private String email;

    @Column
    @NotBlank
    @Pattern(regexp = "^(?=.[a-z])(?=.[A-Z])(?=.[0-9])(?=.[!@#$%^&])[a-zA-Z0-9!@#$%^&]{8}$")
    private String password;

    // many to many with Games Entity
//    @ManyToMany

    // many to many with Guess Entity

}
