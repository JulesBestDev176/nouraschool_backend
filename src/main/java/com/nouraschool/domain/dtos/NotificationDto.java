package com.nouraschool.domain.dtos;

import com.nouraschool.domain.enums.TypeNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private UUID id;
    private UUID userId;
    private String titre;
    private String message;
    private TypeNotification type;
    private Boolean lue;
    private UUID referenceId;
    private String referenceType;
    private LocalDateTime createdAt;
}
