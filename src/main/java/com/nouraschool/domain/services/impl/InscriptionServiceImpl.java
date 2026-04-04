package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.inscription.InscriptionCreateDto;
import com.nouraschool.domain.dtos.inscription.InscriptionDto;
import com.nouraschool.domain.entities.*;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.*;
import com.nouraschool.domain.services.InscriptionService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class InscriptionServiceImpl implements InscriptionService {

    @Inject
    InscriptionRepository inscriptionRepository;

    @Inject
    EleveRepository eleveRepository;

    @Inject
    ClasseRepository classeRepository;

    @Inject
    AnneeAcademiqueRepository anneeAcademiqueRepository;

    @Inject
    ParentRepository parentRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<InscriptionDto> findAll() {
        UUID tenantId = requireTenantId();
        return inscriptionRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public InscriptionDto findById(UUID id) {
        InscriptionEntity entity = inscriptionRepository.findById(id);
        if (entity == null) throw new NotFoundException("Inscription not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public InscriptionDto create(InscriptionCreateDto dto, UUID creePar) {
        UUID tenantId = requireTenantId();

        EleveEntity eleve = eleveRepository.findById(dto.getEleveId());
        if (eleve == null) throw new NotFoundException("Élève introuvable: " + dto.getEleveId());

        ClasseEntity classe = classeRepository.findById(dto.getClasseId());
        if (classe == null) throw new NotFoundException("Classe introuvable: " + dto.getClasseId());

        AnneeAcademiqueEntity anneeAcademique = anneeAcademiqueRepository.findById(dto.getAnneeAcademiqueId());
        if (anneeAcademique == null)
            throw new NotFoundException("Année académique introuvable: " + dto.getAnneeAcademiqueId());

        if (inscriptionRepository.findByEleveAndAnneeAcademique(dto.getEleveId(), dto.getAnneeAcademiqueId()).isPresent()) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }

        long effectif = inscriptionRepository.countActifsByClasseAndAnnee(dto.getClasseId(), dto.getAnneeAcademiqueId());
        if (classe.effectifMax != null && effectif >= classe.effectifMax) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }

        String numeroInscription = generateNumeroInscription(tenantId);

        InscriptionEntity entity = new InscriptionEntity();
        entity.tenantId = tenantId;
        entity.numeroInscription = numeroInscription;
        entity.eleve = eleve;
        entity.classe = classe;
        entity.anneeAcademiqueId = dto.getAnneeAcademiqueId();
        entity.statut = "ACTIF";
        entity.creePar = creePar;

        inscriptionRepository.persist(entity);

        eleve.classe = classe;
        eleve.dateInscription = LocalDate.now();
        eleveRepository.persist(eleve);

        if (dto.getParentIds() != null && !dto.getParentIds().isEmpty()) {
            if (dto.getParentIds().size() > 2) {
                throw new InvalidRequestException("REGLE_METIER_VIOLEE");
            }
            eleve.parents = dto.getParentIds().stream()
                    .map(parentRepository::findById)
                    .filter(p -> p != null)
                    .collect(Collectors.toList());
            eleveRepository.persist(eleve);
        }

        return toDto(entity);
    }

    @Override
    @Transactional
    public InscriptionDto transferer(UUID id, UUID nouvelleClasseId) {
        InscriptionEntity entity = inscriptionRepository.findById(id);
        if (entity == null) throw new NotFoundException("Inscription not found: " + id);

        ClasseEntity nouvelleClasse = classeRepository.findById(nouvelleClasseId);
        if (nouvelleClasse == null) throw new NotFoundException("Classe introuvable: " + nouvelleClasseId);

        long effectif = inscriptionRepository.countActifsByClasseAndAnnee(nouvelleClasseId, entity.anneeAcademiqueId);
        if (nouvelleClasse.effectifMax != null && effectif >= nouvelleClasse.effectifMax) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }

        entity.classe = nouvelleClasse;
        entity.statut = "TRANSFERE";

        entity.eleve.classe = nouvelleClasse;
        eleveRepository.persist(entity.eleve);

        return toDto(inscriptionRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        InscriptionEntity entity = inscriptionRepository.findById(id);
        if (entity == null) throw new NotFoundException("Inscription not found: " + id);
        inscriptionRepository.delete(entity);
    }

    private String generateNumeroInscription(UUID tenantId) {
        String prefix = "INSC-" + LocalDate.now().getYear() + "-";
        long count = inscriptionRepository.findByTenantId(tenantId).stream()
                .filter(i -> i.numeroInscription != null && i.numeroInscription.startsWith(prefix))
                .count();
        return prefix + String.format("%04d", count + 1);
    }

    private InscriptionDto toDto(InscriptionEntity e) {
        return InscriptionDto.builder()
                .id(e.id)
                .numeroInscription(e.numeroInscription)
                .eleveId(e.eleve != null ? e.eleve.id : null)
                .classeId(e.classe != null ? e.classe.id : null)
                .anneeAcademiqueId(e.anneeAcademiqueId)
                .statut(e.statut)
                .creePar(e.creePar)
                .createdAt(e.createdAt)
                .updatedAt(e.updatedAt)
                .build();
    }

    private UUID requireTenantId() {
        if (!tenantContext.hasTenant()) {
            throw new InvalidRequestException("Tenant context required");
        }
        return tenantContext.getTenantId();
    }
}
