package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.platform.PlateformeUtilisateurCreateDto;
import com.nouraschool.domain.dtos.platform.PlateformeUtilisateurDto;
import com.nouraschool.domain.entities.PlateformeUtilisateurEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.PlateformeUtilisateurRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import com.nouraschool.domain.services.PlateformeUtilisateurService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PlateformeUtilisateurServiceImpl implements PlateformeUtilisateurService {

    @Inject
    PlateformeUtilisateurRepository repository;

    @Inject
    PasswordEncoder passwordEncoder;

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
        entity.nom = dto.getNom();
        entity.prenom = dto.getPrenom();
        entity.email = dto.getEmail();
        entity.motDePasse = passwordEncoder.encode(dto.getMotDePasse());
        entity.rolePlateforme = dto.getRolePlateforme();
        entity.actif = true;
        entity.createdAt = Instant.now();
        repository.persist(entity);
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
                .rolePlateforme(e.rolePlateforme)
                .actif(e.actif)
                .createdAt(e.createdAt)
                .build();
    }
}
