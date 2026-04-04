package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.platform.TenantCreateDto;
import com.nouraschool.domain.dtos.platform.TenantDto;
import com.nouraschool.domain.dtos.platform.TenantUpdateDto;
import com.nouraschool.domain.entities.AdministrateurEntity;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import com.nouraschool.domain.services.TenantService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class TenantServiceImpl implements TenantService {

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9][a-z0-9-]*[a-z0-9]$|^[a-z0-9]$");

    @Inject
    TenantRepository tenantRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Override
    public List<TenantDto> findAll() {
        return tenantRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<TenantDto> findById(UUID id) {
        return tenantRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional
    public TenantDto create(TenantCreateDto dto) {
        if (tenantRepository.findBySlug(dto.getSlug()).isPresent()) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        TenantEntity entity = new TenantEntity();
        entity.slug = dto.getSlug();
        entity.nom = dto.getNom();
        entity.emailContact = dto.getEmailContact();
        entity.telephone = dto.getTelephone();
        entity.adresse = dto.getAdresse();
        entity.logoUrl = dto.getLogoUrl();
        entity.plan = dto.getPlan() != null ? dto.getPlan() : "TRIAL";
        entity.actif = true;
        entity.createdAt = Instant.now();
        tenantRepository.persist(entity);
        createInitialAdmin(entity, dto.getInitialAdminEmail(), dto.getInitialAdminPassword());
        return toDto(entity);
    }

    private void createInitialAdmin(TenantEntity tenant, String email, String password) {
        String slug = tenant.slug;
        String adminUsername = "admin-" + slug;
        if (userRepository.findByUsername(adminUsername) != null) {
            return;
        }
        String adminEmail = email != null && !email.isBlank()
                ? email
                : "admin@" + slug + ".noura-school.local";
        String adminPassword = password != null && !password.isBlank()
                ? passwordEncoder.encode(password)
                : passwordEncoder.encode(UUID.randomUUID().toString());
        boolean mustChange = password == null || password.isBlank();

        AdministrateurEntity admin = new AdministrateurEntity();
        admin.tenant = tenant;
        admin.username = adminUsername;
        admin.email = adminEmail;
        admin.password = adminPassword;
        admin.firstName = "Admin";
        admin.lastName = slug;
        admin.telephone = tenant.telephone != null ? tenant.telephone : "+221000000000";
        admin.adresse = tenant.adresse != null ? tenant.adresse : "";
        admin.role = UserRole.ADMIN;
        admin.active = true;
        admin.mustChangePassword = mustChange;
        userRepository.persist(admin);
    }

    @Override
    @Transactional
    public TenantDto update(UUID id, TenantUpdateDto dto) {
        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RESSOURCE_INTROUVABLE"));
        if (dto.getNom() != null) entity.nom = dto.getNom();
        if (dto.getEmailContact() != null) entity.emailContact = dto.getEmailContact();
        if (dto.getTelephone() != null) entity.telephone = dto.getTelephone();
        if (dto.getAdresse() != null) entity.adresse = dto.getAdresse();
        if (dto.getLogoUrl() != null) entity.logoUrl = dto.getLogoUrl();
        if (dto.getPlan() != null) entity.plan = dto.getPlan();
        if (dto.getActif() != null) entity.actif = dto.getActif();
        if (dto.getDateExpiration() != null) entity.dateExpiration = dto.getDateExpiration();
        entity.updatedAt = Instant.now();
        tenantRepository.persist(entity);
        return toDto(entity);
    }

    @Override
    @Transactional
    public void suspend(UUID id) {
        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RESSOURCE_INTROUVABLE"));
        if ("default".equals(entity.slug)) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        entity.actif = false;
        entity.updatedAt = Instant.now();
        tenantRepository.persist(entity);
    }

    @Override
    @Transactional
    public void reactivate(UUID id) {
        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RESSOURCE_INTROUVABLE"));
        entity.actif = true;
        entity.updatedAt = Instant.now();
        tenantRepository.persist(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RESSOURCE_INTROUVABLE"));
        if ("default".equals(entity.slug)) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        entity.deletedAt = Instant.now();
        tenantRepository.persist(entity);
    }

    @Override
    @Transactional
    public TenantDto autoRegister(String nom, String slug) {
        if (nom == null || nom.isBlank()) {
            throw new InvalidRequestException("VALIDATION_ECHOUEE");
        }
        String s = slug != null && !slug.isBlank() ? slug : toSlug(nom);
        Optional<TenantEntity> existing = tenantRepository.findBySlug(s);
        if (existing.isPresent()) {
            return toDto(existing.get());
        }
        TenantCreateDto dto = new TenantCreateDto();
        dto.setSlug(s);
        dto.setNom(nom.trim());
        dto.setPlan("TRIAL");
        return create(dto);
    }

    private String toSlug(String nom) {
        return nom.trim().toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private TenantDto toDto(TenantEntity e) {
        return TenantDto.builder()
                .id(e.id)
                .slug(e.slug)
                .nom(e.nom)
                .emailContact(e.emailContact)
                .telephone(e.telephone)
                .adresse(e.adresse)
                .logoUrl(e.logoUrl)
                .plan(e.plan)
                .actif(e.actif)
                .dateExpiration(e.dateExpiration)
                .createdAt(e.createdAt)
                .updatedAt(e.updatedAt)
                .build();
    }
}
