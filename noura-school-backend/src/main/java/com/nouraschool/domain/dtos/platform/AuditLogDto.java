package com.nouraschool.domain.dtos.platform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private UUID id;
    private UUID tenantId;
    private UUID utilisateurId;
    private String role;
    private String action;
    private String resourceType;
    private UUID resourceId;
    private Map<String, Object> details;
    private String ipAddress;
    private String userAgent;
    private Instant createdAt;
}
