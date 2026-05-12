package com.nouraschool.runtime.controller.platform;

import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.dtos.platform.AuditLogDto;
import com.nouraschool.domain.entities.AuditLogEntity;
import com.nouraschool.domain.repositories.AuditLogRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * API plateforme : consultation des logs d'audit.
 */
@Path("/api/platform/audit-logs")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"SUPER_ADMIN", "GESTIONNAIRE"})
@SecurityRequirement(name = "Bearer")
public class AuditLogResource {

    @Inject
    AuditLogRepository auditLogRepository;

    @GET
    public PageDto<AuditLogDto> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("tenantId") UUID tenantId,
            @QueryParam("utilisateurId") UUID utilisateurId) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        List<AuditLogEntity> entities;
        long total;
        if (tenantId != null) {
            entities = auditLogRepository.findByTenantId(tenantId, safePage, safeSize);
            total = auditLogRepository.countByTenantId(tenantId);
        } else if (utilisateurId != null) {
            entities = auditLogRepository.findByUtilisateurId(utilisateurId, safePage, safeSize);
            total = auditLogRepository.countByUtilisateurId(utilisateurId);
        } else {
            entities = auditLogRepository.findAll(safePage, safeSize);
            total = auditLogRepository.count();
        }
        List<AuditLogDto> items = entities.stream().map(this::toDto).collect(Collectors.toList());
        return PageDto.of(items, safePage, safeSize, total);
    }

    private AuditLogDto toDto(AuditLogEntity e) {
        return AuditLogDto.builder()
                .id(e.id)
                .tenantId(e.tenantId)
                .utilisateurId(e.utilisateurId)
                .role(e.role)
                .action(e.action)
                .resourceType(e.resourceType)
                .resourceId(e.resourceId)
                .details(e.details)
                .ipAddress(e.ipAddress)
                .userAgent(e.userAgent)
                .createdAt(e.createdAt)
                .build();
    }
}
