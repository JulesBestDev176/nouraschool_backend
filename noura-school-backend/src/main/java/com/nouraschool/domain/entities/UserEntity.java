package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.UserRole;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@UserDefinition
public class UserEntity extends AbstractUuidEntity {

    @Column(nullable = false, unique = true)
    @Username
    public String username;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    @Password
    public String password;

    @Column(name = "first_name", nullable = false)
    public String firstName;

    @Column(name = "last_name", nullable = false)
    public String lastName;

    @Column(nullable = false)
    public String telephone;

    @Column(nullable = false)
    public String adresse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserRole role;

    @Roles
    public String getRoles() {
        return role != null ? role.name() : "";
    }

    @Column(nullable = false)
    public Boolean active = true;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}