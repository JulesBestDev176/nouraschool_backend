package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.platform.PlateformeUtilisateurCreateDto;
import com.nouraschool.domain.dtos.platform.PlateformeUtilisateurDto;
import com.nouraschool.domain.entities.PlateformeUtilisateurEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.PlateformeUtilisateurRepository;
import com.nouraschool.domain.services.EmailService;
import com.nouraschool.domain.services.NotificationLogService;
import com.nouraschool.domain.services.PasswordEncoder;
import com.nouraschool.domain.services.PlateformeUtilisateurService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PlateformeUtilisateurServiceImpl implements PlateformeUtilisateurService {

    private static final SecureRandom PASSWORD_RANDOM = new SecureRandom();
    private static final String PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    @Inject
    PlateformeUtilisateurRepository repository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    NotificationLogService notificationLogService;

    @Inject
    EmailService emailService;

    @Override
    public List<PlateformeUtilisateurDto> findAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<PlateformeUtilisateurDto> findById(UUID id) {
        return repository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional
    public PlateformeUtilisateurDto create(PlateformeUtilisateurCreateDto dto) {
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new InvalidRequestException("EMAIL_DEJA_UTILISE");
        }
        if (!"SUPER_ADMIN".equals(dto.getRolePlateforme()) && !"GESTIONNAIRE".equals(dto.getRolePlateforme())) {
            throw new InvalidRequestException("VALIDATION_ECHOUEE");
        }
        PlateformeUtilisateurEntity entity = new PlateformeUtilisateurEntity();
        String temporaryPassword = generateSimplePassword();
        entity.nom = dto.getNom();
        entity.prenom = dto.getPrenom();
        entity.email = dto.getEmail();
        entity.telephone = dto.getTelephone();
        entity.motDePasse = passwordEncoder.encode(temporaryPassword);
        entity.rolePlateforme = dto.getRolePlateforme();
        entity.actif = true;
        entity.createdAt = Instant.now();
        repository.persist(entity);
        logPlatformUserCredentials(entity, temporaryPassword);
        return toDto(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PlateformeUtilisateurEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("RESSOURCE_INTROUVABLE"));
        repository.delete(entity);
    }

    @Override
    @Transactional
    public void setActif(UUID id, boolean actif) {
        PlateformeUtilisateurEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("RESSOURCE_INTROUVABLE"));
        entity.actif = actif;
        entity.updatedAt = Instant.now();
        repository.persist(entity);
    }

    private PlateformeUtilisateurDto toDto(PlateformeUtilisateurEntity e) {
        return PlateformeUtilisateurDto.builder()
                .id(e.id)
                .nom(e.nom)
                .prenom(e.prenom)
                .email(e.email)
                .telephone(e.telephone)
                .rolePlateforme(e.rolePlateforme)
                .actif(e.actif)
                .createdAt(e.createdAt)
                .build();
    }

    private String generateSimplePassword() {
        StringBuilder password = new StringBuilder("Ns-");
        for (int i = 0; i < 8; i++) {
            password.append(PASSWORD_ALPHABET.charAt(PASSWORD_RANDOM.nextInt(PASSWORD_ALPHABET.length())));
        }
        return password.toString();
    }

    private void logPlatformUserCredentials(PlateformeUtilisateurEntity user, String temporaryPassword) {
        String content = String.format(
                "Bonjour %s %s,%n%n"
                        + "Votre compte plateforme NouraSchool a été créé.%n%n"
                        + "Identifiants de connexion :%n"
                        + "Email : %s%n"
                        + "Mot de passe temporaire : %s%n"
                        + "Rôle : %s%n%n"
                        + "Merci de modifier ce mot de passe après la première connexion.%n",
                user.prenom,
                user.nom,
                user.email,
                temporaryPassword,
                user.rolePlateforme
        );
        UUID logId = notificationLogService.log(
                null,
                NotificationLogService.CANAL_EMAIL,
                user.email,
                "Identifiants compte plateforme NouraSchool",
                content
        );
        try {
            emailService.send(user.email, "Identifiants compte plateforme NouraSchool", content);
            notificationLogService.markSent(logId);
        } catch (Exception e) {
            notificationLogService.markFailed(logId, e.getMessage());
        }
    }
}
