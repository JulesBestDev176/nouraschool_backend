package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.etablissement.EtablissementStatsDto;
import com.nouraschool.domain.entities.ClasseEntity;
import com.nouraschool.domain.entities.EleveEntity;
import com.nouraschool.domain.entities.EnseignantEntity;
import com.nouraschool.domain.entities.MatiereEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.services.EtablissementStatsService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class EtablissementStatsServiceImpl implements EtablissementStatsService {

    @Inject
    TenantContext tenantContext;

    @Override
    public EtablissementStatsDto getStats() {
        UUID tenantId = requireTenantId();
        long nbClasses = ClasseEntity.count("tenantId", tenantId);
        long nbEleves = EleveEntity.count("tenant.id", tenantId);
        long nbEnseignants = EnseignantEntity.count("tenant.id", tenantId);
        long nbMatieres = MatiereEntity.count("tenantId", tenantId);
        return EtablissementStatsDto.builder()
                .nbClasses(nbClasses)
                .nbEleves(nbEleves)
                .nbEnseignants(nbEnseignants)
                .nbMatieres(nbMatieres)
                .build();
    }

    private UUID requireTenantId() {
        if (!tenantContext.hasTenant()) {
            throw new InvalidRequestException("Tenant context required");
        }
        return tenantContext.getTenantId();
    }
}
