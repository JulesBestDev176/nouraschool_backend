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
import com.nouraschool.domain.services.EmailService;
import com.nouraschool.domain.services.NotificationLogService;
import com.nouraschool.domain.services.PasswordEncoder;
import com.nouraschool.domain.services.StorageService;
import com.nouraschool.domain.services.TenantService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TenantServiceImpl implements TenantService {

    private static final SecureRandom PASSWORD_RANDOM = new SecureRandom();
    private static final String PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    @Inject
    TenantRepository tenantRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    NotificationLogService notificationLogService;

    @Inject
    EmailService emailService;

    @Inject
    StorageService storageService;

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
        String slug = generateUniqueSlug(dto.getNom(), null);
        TenantEntity entity = new TenantEntity();
        entity.slug = slug;
        entity.nom = dto.getNom();
        entity.emailContact = dto.getEmailContact();
        entity.telephone = dto.getTelephone();
        entity.adresse = dto.getAdresse();
        entity.logoUrl = dto.getLogoUrl();
        entity.plan = dto.getPlan() != null ? dto.getPlan() : "TRIAL";
        entity.actif = true;
        entity.dateExpiration = resolveExpiration(dto.getDurationMonths());
        entity.createdAt = Instant.now();
        tenantRepository.persist(entity);
        createInitialAdmin(entity, dto.getInitialAdminEmail(), dto.getInitialAdminTelephone());
        return toDto(entity);
    }

    private void createInitialAdmin(TenantEntity tenant, String email, String telephone) {
        String slug = tenant.slug;
        String adminUsername = "admin-" + slug;
        if (userRepository.findByUsername(adminUsername) != null) {
            return;
        }
        String adminEmail = email != null && !email.isBlank()
                ? email
                : "admin@" + slug + ".noura-school.local";
        String temporaryPassword = generateSimplePassword();

        AdministrateurEntity admin = new AdministrateurEntity();
        admin.tenant = tenant;
        admin.username = adminUsername;
        admin.email = adminEmail;
        admin.password = passwordEncoder.encode(temporaryPassword);
        admin.firstName = "Admin";
        admin.lastName = slug;
        admin.telephone = telephone != null && !telephone.isBlank()
                ? telephone
                : tenant.telephone != null ? tenant.telephone : "+221000000000";
        admin.adresse = tenant.adresse != null ? tenant.adresse : "";
        admin.role = UserRole.ADMIN;
        admin.active = true;
        admin.mustChangePassword = true;
        userRepository.persist(admin);
        logInitialAdminCredentials(tenant, adminEmail, temporaryPassword);
    }

    @Override
    @Transactional
    public TenantDto update(UUID id, TenantUpdateDto dto) {
        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RESSOURCE_INTROUVABLE"));
        if (dto.getNom() != null && !dto.getNom().isBlank() && !dto.getNom().equals(entity.nom)) {
            entity.nom = dto.getNom();
            entity.slug = generateUniqueSlug(dto.getNom(), entity.id);
        }
        if (dto.getEmailContact() != null) entity.emailContact = dto.getEmailContact();
        if (dto.getTelephone() != null) entity.telephone = dto.getTelephone();
        if (dto.getAdresse() != null) entity.adresse = dto.getAdresse();
        if (dto.getLogoUrl() != null) entity.logoUrl = dto.getLogoUrl();
        if (dto.getPlan() != null) entity.plan = dto.getPlan();
        if (dto.getDurationMonths() != null) entity.dateExpiration = resolveExpiration(dto.getDurationMonths());
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
        String s = generateUniqueSlug(nom, null);
        Optional<TenantEntity> existing = tenantRepository.findBySlug(s);
        if (existing.isPresent()) {
            return toDto(existing.get());
        }
        TenantCreateDto dto = new TenantCreateDto();
        dto.setNom(nom.trim());
        dto.setPlan("TRIAL");
        return create(dto);
    }

    @Override
    @Transactional
    public String uploadLogo(UUID id, InputStream input, String contentType, long contentLength, String extension) {
        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RESSOURCE_INTROUVABLE"));

        // Supprimer l'ancien logo best-effort
        if (entity.logoUrl != null && !entity.logoUrl.isBlank()) {
            try {
                String oldKey = "tenants/" + entity.slug + "/logos/logo." + extractExtension(entity.logoUrl);
                storageService.delete(oldKey);
            } catch (Exception ignored) {}
        }

        String ext = (extension != null && !extension.isBlank()) ? extension.toLowerCase() : "jpg";
        String key = "tenants/" + entity.slug + "/logos/logo." + ext;
        storageService.upload(key, input, contentType, contentLength);

        String logoUrl = storageService.getPublicUrl(key);
        entity.logoUrl = logoUrl;
        entity.updatedAt = Instant.now();
        tenantRepository.persist(entity);
        return logoUrl;
    }

    private String extractExtension(String url) {
        int dot = url.lastIndexOf('.');
        if (dot > 0 && dot < url.length() - 1) {
            String ext = url.substring(dot + 1).split("[?#]")[0];
            if (ext.length() <= 5) return ext;
        }
        return "jpg";
    }

    private String toSlug(String nom) {
        String base = nom == null ? "" : nom.trim().toLowerCase()
                .replaceAll("[àáâãäå]", "a")
                .replaceAll("[èéêë]", "e")
                .replaceAll("[ìíîï]", "i")
                .replaceAll("[òóôõö]", "o")
                .replaceAll("[ùúûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[ñ]", "n")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return base.isBlank() ? "ecole" : base;
    }

    private String generateUniqueSlug(String nom, UUID currentTenantId) {
        String base = toSlug(nom);
        String candidate = base;
        int suffix = 2;
        while (isSlugTaken(candidate, currentTenantId)) {
            candidate = base + "-" + suffix;
            suffix++;
        }
        return candidate;
    }

    private boolean isSlugTaken(String slug, UUID currentTenantId) {
        return tenantRepository.findBySlug(slug)
                .map(existing -> currentTenantId == null || !existing.id.equals(currentTenantId))
                .orElse(false);
    }

    private LocalDate resolveExpiration(Integer durationMonths) {
        if (durationMonths == null || durationMonths <= 0) {
            return null;
        }
        return LocalDate.now().plusMonths(durationMonths);
    }

    private String generateSimplePassword() {
        StringBuilder password = new StringBuilder("Ns-");
        for (int i = 0; i < 8; i++) {
            password.append(PASSWORD_ALPHABET.charAt(PASSWORD_RANDOM.nextInt(PASSWORD_ALPHABET.length())));
        }
        return password.toString();
    }

    private void logInitialAdminCredentials(TenantEntity tenant, String adminEmail, String temporaryPassword) {
        String content = String.format(
                "Bonjour,%n%n"
                        + "Votre école %s a été créée sur NouraSchool.%n%n"
                        + "Identifiants de connexion admin école :%n"
                        + "Email : %s%n"
                        + "Mot de passe temporaire : %s%n%n"
                        + "Ce mot de passe doit être modifié à la première connexion.%n",
                tenant.nom,
                adminEmail,
                temporaryPassword
        );
        UUID logId = notificationLogService.log(
                tenant.id,
                NotificationLogService.CANAL_EMAIL,
                adminEmail,
                "Identifiants admin école NouraSchool",
                content
        );
        try {
            emailService.send(adminEmail, "Identifiants admin école NouraSchool", content);
            notificationLogService.markSent(logId);
        } catch (Exception e) {
            notificationLogService.markFailed(logId, e.getMessage());
        }
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
