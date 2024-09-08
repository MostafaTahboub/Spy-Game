package com.example.demo.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "users" )
@AllArgsConstructor
@NoArgsConstructor
@Audited
@AuditTable(value="AUD_User")

public class User {


}
