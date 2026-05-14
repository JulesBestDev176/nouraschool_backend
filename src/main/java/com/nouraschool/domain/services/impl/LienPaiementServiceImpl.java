package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.inscription.LienPaiementCreateDto;
import com.nouraschool.domain.dtos.inscription.LienPaiementParentDto;
import com.nouraschool.domain.entities.LienPaiementParentEntity;
import com.nouraschool.domain.entities.ParentEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.LienPaiementParentRepository;
import com.nouraschool.domain.repositories.ParentRepository;
import com.nouraschool.domain.services.LienPaiementService;
import com.nouraschool.domain.services.OtpService;
import com.nouraschool.domain.services.WhatsAppService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class LienPaiementServiceImpl implements LienPaiementService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int LINK_EXPIRY_DAYS = 7;

    @Inject
    LienPaiementParentRepository lienRepository;

    @Inject
    ParentRepository parentRepository;

    @Inject
    TenantContext tenantContext;

    @Inject
    OtpService otpService;

    @Inject
    WhatsAppService whatsAppService;

    @Override
    @Transactional
    public List<LienPaiementParentDto> generate(List<LienPaiementCreateDto> dtos) {
        UUID tenantId = requireTenantId();
        return dtos.stream()
                .map(dto -> createLien(tenantId, dto))
                .collect(Collectors.toList());
    }

    private LienPaiementParentDto createLien(UUID tenantId, LienPaiementCreateDto dto) {
        ParentEntity parent = parentRepository.findById(dto.getParentId());
        if (parent == null) throw new NotFoundException("Parent introuvable: " + dto.getParentId());

        String token = UUID.randomUUID().toString().replace("-", "");

        LienPaiementParentEntity entity = new LienPaiementParentEntity();
        entity.tenantId = tenantId;
        entity.parent = parent;
        entity.token = token;
        entity.montantTotal = dto.getMontantTotal();
        entity.mois = dto.getMois();
        entity.statut = "EN_ATTENTE";
        entity.expiresAt = Instant.now().plus(Duration.ofDays(LINK_EXPIRY_DAYS));
        entity.otpVerified = false;

        LienPaiementParentEntity persisted = lienRepository.persist(entity);
        sendOtpToParent(persisted, parent);
        return toDto(persisted);
    }

    @Override
    public LienPaiementParentDto findByToken(String token) {
        LienPaiementParentEntity entity = lienRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Lien introuvable ou expiré"));
        if (entity.expiresAt.isBefore(Instant.now())) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        return toDto(entity);
    }

    @Override
    @Transactional
    public LienPaiementParentDto verifierOtp(String token, String otp) {
        LienPaiementParentEntity entity = lienRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Lien introuvable ou expiré"));
        if (entity.expiresAt.isBefore(Instant.now())) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        if (otp == null || otp.length() != 6 || !otpService.verify(otpKey(token), otp)) {
            throw new InvalidRequestException("OTP_INVALIDE");
        }
        entity.otpVerified = true;
        entity.otpExpiresAt = Instant.now().plus(Duration.ofMinutes(OTP_EXPIRY_MINUTES));
        return toDto(lienRepository.persist(entity));
    }

    @Override
    @Transactional
    public LienPaiementParentDto payer(String token) {
        LienPaiementParentEntity entity = lienRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Lien introuvable ou expiré"));
        if (entity.expiresAt.isBefore(Instant.now())) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        if (!Boolean.TRUE.equals(entity.otpVerified)) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        entity.statut = "PAYE";
        return toDto(lienRepository.persist(entity));
    }

    private LienPaiementParentDto toDto(LienPaiementParentEntity e) {
        return LienPaiementParentDto.builder()
                .id(e.id)
                .parentId(e.parent != null ? e.parent.id : null)
                .token(e.token)
                .montantTotal(e.montantTotal)
                .mois(e.mois)
                .statut(e.statut)
                .expiresAt(e.expiresAt)
                .otpVerified(e.otpVerified)
                .createdAt(e.createdAt != null ? e.createdAt.atZone(java.time.ZoneOffset.UTC).toInstant() : null)
                .build();
    }

    private UUID requireTenantId() {
        if (!tenantContext.hasTenant()) {
            throw new InvalidRequestException("Tenant context required");
        }
        return tenantContext.getTenantId();
    }

    private void sendOtpToParent(LienPaiementParentEntity lien, ParentEntity parent) {
        if (parent.telephone == null || parent.telephone.isBlank()) {
            throw new InvalidRequestException("TELEPHONE_PARENT_REQUIS");
        }
        String code = otpService.generate(otpKey(lien.token));
        whatsAppService.sendOtp(parent.telephone, code);
    }

    private String otpKey(String token) {
        return "lien-paiement:" + token;
    }
}
