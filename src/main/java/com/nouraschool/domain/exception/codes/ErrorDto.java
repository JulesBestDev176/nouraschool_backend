package com.nouraschool.domain.exception.codes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDto {
    /** Code métier (task.md section 3.7) : IDENTIFIANTS_INVALIDES, RESSOURCE_INTROUVABLE, etc. */
    private String code;
    private String message;
    private List<String> details;
    private String correlationId;
    /** ISO 8601 : 2025-06-01T10:30:00Z */
    private String timestamp;
}
