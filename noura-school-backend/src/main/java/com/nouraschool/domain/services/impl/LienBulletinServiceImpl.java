package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.viescolaire.LienBulletinParentDto;
import com.nouraschool.domain.entities.BulletinEntity;
import com.nouraschool.domain.entities.LienBulletinParentEntity;
import com.nouraschool.domain.entities.ParentEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.BulletinRepository;
import com.nouraschool.domain.repositories.LienBulletinParentRepository;
import com.nouraschool.domain.repositories.ParentRepository;
import com.nouraschool.domain.services.LienBulletinService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class LienBulletinServiceImpl implements LienBulletinService {

    private static final int EXPIRY_DAYS = 30;

    @Inject
    LienBulletinParentRepository lienRepository;

    @Inject
    BulletinRepository bulletinRepository;

    @Inject
    ParentRepository parentRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    @Transactional
    public LienBulletinParentDto generate(UUID bulletinId, UUID parentId) {
        UUID tenantId = requireTenantId();

        BulletinEntity bulletin = bulletinRepository.findById(bulletinId);
        if (bulletin == null) throw new NotFoundException("Bulletin introuvable: " + bulletinId);
        if (parentRepository.findById(parentId) == null) throw new NotFoundException("Parent introuvable: " + parentId);

        String token = UUID.randomUUID().toString().replace("-", "");

        LienBulletinParentEntity entity = new LienBulletinParentEntity();
        entity.tenantId = tenantId;
        entity.bulletin = bulletin;
        entity.parent = parentRepository.findById(parentId);
        entity.token = token;
        entity.expiresAt = Instant.now().plus(Duration.ofDays(EXPIRY_DAYS));
        entity.otpVerified = false;

        return toDto(lienRepository.persist(entity));
    }

    @Override
    public LienBulletinParentDto findByToken(String token) {
        LienBulletinParentEntity entity = lienRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Lien introuvable ou expiré"));
        if (entity.expiresAt.isBefore(Instant.now())) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        return toDto(entity);
    }

    @Override
    @Transactional
    public LienBulletinParentDto verifierOtp(String token, String otp) {
        LienBulletinParentEntity entity = lienRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Lien introuvable ou expiré"));
        if (entity.expiresAt.isBefore(Instant.now())) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        if (otp != null && otp.length() == 6) {
            entity.otpVerified = true;
            return toDto(lienRepository.persist(entity));
        }
        throw new InvalidRequestException("VALIDATION_ECHOUEE");
    }

    private LienBulletinParentDto toDto(LienBulletinParentEntity e) {
        return LienBulletinParentDto.builder()
                .id(e.id)
                .bulletinId(e.bulletin != null ? e.bulletin.id : null)
                .parentId(e.parent != null ? e.parent.id : null)
                .token(e.token)
                .otpVerified(e.otpVerified)
                .expiresAt(e.expiresAt)
                .ouvertLe(e.ouvertLe)
                .build();
    }

    private UUID requireTenantId() {
        if (!tenantContext.hasTenant()) {
            throw new InvalidRequestException("Tenant context required");
        }
        return tenantContext.getTenantId();
    }
}
