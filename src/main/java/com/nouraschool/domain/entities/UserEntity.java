package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.UserRole;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@UserDefinition
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class UserEntity extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    public TenantEntity tenant;

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

    @Column(name = "must_change_password", nullable = false)
    public Boolean mustChangePassword = true;
}