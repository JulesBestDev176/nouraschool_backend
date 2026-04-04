package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.TypeNotification;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity extends AbstractEntity {

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(nullable = false)
    public String titre;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypeNotification type;

    @Column(nullable = false)
    public Boolean lue = false;

    @Column(name = "reference_id")
    public UUID referenceId;

    @Column(name = "reference_type")
    public String referenceType;

}
